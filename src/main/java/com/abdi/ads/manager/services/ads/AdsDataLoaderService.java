package com.abdi.ads.manager.services.ads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.abdi.ads.manager.exceptions.DataProcessingException;
import com.abdi.ads.manager.exceptions.InvalidDataException;
import com.abdi.ads.manager.model.click.Click;
import com.abdi.ads.manager.model.click.dao.ClickRepository;
import com.abdi.ads.manager.model.impression.Impression;
import com.abdi.ads.manager.model.impression.dao.ImpressionRepository;
import com.abdi.ads.manager.services.ads.dto.TrackingStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class AdsDataLoaderService {

	private static final int BATCH_SIZE = 50;

	private final ImpressionRepository impressionRepository;

	private final ClickRepository clickRepository;

	private final TrackingService trackingService;

	@Async
	public void loadDataAsync(MultipartFile impressionsFile,
			MultipartFile clicksFile,
			String trackingId) {
		trackingService.updateStatus(trackingId, TrackingStatus.IN_PROGRESS);
		log.info("Started data loading asynchronously for tracking ID: {}", trackingId);

		try {
			loadData(impressionsFile, clicksFile);
			trackingService.updateStatus(trackingId, TrackingStatus.COMPLETED);
			log.info("Data loading completed for tracking ID: {}", trackingId);
		} catch (Exception e) {
			trackingService.updateStatus(trackingId, TrackingStatus.FAILED);
			log.error("Data loading failed for tracking ID {}: {}", trackingId, e.getMessage(), e);
			throw e;
		}
	}

	public void loadData(MultipartFile impressionsFile, MultipartFile clicksFile) {
		try {
			log.info("Parsing impressions...");
			List<Impression> impressions = parseImpressions(impressionsFile);
			log.info("Parsed {} impressions.", impressions.size());

			saveImpressionsInBatches(impressions);
			log.info("Impressions saved successfully.");

			List<Click> clicks = parseClicks(clicksFile);
			log.info("Parsed {} clicks.", clicks.size());

			saveClicksInBatches(clicks);
			log.info("Clicks saved successfully.");

		} catch (IOException e) {
			log.error("Failed to read input files: {}", e.getMessage(), e);
			throw new DataProcessingException("Failed to read input files", e);
		}
	}

	private List<Impression> parseImpressions(MultipartFile impressionsFile) throws IOException {
		List<Impression> impressions = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode rootNode = objectMapper.readTree(impressionsFile.getInputStream());

		if (rootNode.isArray()) {
			for (JsonNode node : rootNode) {
				try {
					Impression impression = parseImpression(node);
					impressions.add(impression);
				} catch (InvalidDataException e) {
					log.warn("Skipping invalid impression: {}", e.getMessage());
				}
			}
		}

		return impressions;
	}

	private List<Click> parseClicks(MultipartFile clicksFile) throws IOException {
		List<Click> clicks = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode rootNode = objectMapper.readTree(clicksFile.getInputStream());

		if (rootNode.isArray()) {
			for (JsonNode node : rootNode) {
				try {
					Click click = parseClick(node);
					clicks.add(click);
				} catch (InvalidDataException e) {
					log.warn("Skipping invalid click: {}", e.getMessage());
				}
			}
		}

		return clicks;
	}

	private Impression parseImpression(JsonNode node) throws InvalidDataException {
		if (!node.hasNonNull("id") ||
				!node.hasNonNull("app_id") ||
				!node.hasNonNull("country_code") ||
				!node.hasNonNull("advertiser_id")) {
			throw new InvalidDataException("Missing required fields in impression data: " + node.toString());
		}

		try {
			UUID uuid = UUID.fromString(node.get("id").asText());
			Integer appId = node.get("app_id").asInt();
			String countryCode = node.get("country_code").asText();
			Integer advertiserId = node.get("advertiser_id").asInt();

			return new Impression(uuid.toString(), appId, countryCode, advertiserId);

		} catch (Exception e) {
			throw new InvalidDataException("Invalid impression data format: " + node.toString(), e);
		}
	}

	private Click parseClick(JsonNode node) throws InvalidDataException {
		if (!node.hasNonNull("impression_id") || !node.hasNonNull("revenue")) {
			throw new InvalidDataException("Missing required fields in click data: " + node);
		}

		try {
			String impressionId = UUID.fromString(node.get("impression_id").asText()).toString();
			Double revenue = node.get("revenue").asDouble();

			Optional<Impression> impressionOpt = impressionRepository.findByUuid(impressionId);
			if (impressionOpt.isEmpty()) {
				throw new InvalidDataException("Impression not found for click: " + impressionId);
			}

			Impression impression = impressionOpt.get();

			return new Click(impression, revenue);

		} catch (Exception e) {
			throw new InvalidDataException("Invalid click data format: " + node, e);
		}
	}

	private void saveImpressionsInBatches(List<Impression> impressions) {
		List<String> uuids = impressions.stream()
				.map(Impression::getUuid)
				.collect(Collectors.toList());

		Set<String> existingUuids = new HashSet<>(impressionRepository.findUuidsIn(uuids));

		Set<String> processedUuids = new HashSet<>(existingUuids);

		List<Impression> batch = new ArrayList<>();
		int count = 0;

		for (Impression impression : impressions) {
			String uuid = impression.getUuid();

			if (!processedUuids.contains(uuid)) {
				batch.add(impression);
				processedUuids.add(uuid);
				count++;
			} else {
				log.debug("Impression with UUID {} already exists. Skipping.", uuid);
			}

			if (count % BATCH_SIZE == 0 && !batch.isEmpty()) {
				impressionRepository.saveAll(batch);
				batch.clear();
				log.debug("Saved batch of {} impressions.", BATCH_SIZE);
			}
		}

		if (!batch.isEmpty()) {
			impressionRepository.saveAll(batch);
			log.debug("Saved final batch of {} impressions.", batch.size());
		}
	}


	private void saveClicksInBatches(List<Click> clicks) {
		List<Click> batch = new ArrayList<>();
		int count = 0;

		for (Click click : clicks) {
			String impressionUuId = click.getImpression().getUuid();
			Double revenue = click.getRevenue();

			if (!clickRepository.existsByImpressionUuidAndRevenue(impressionUuId, revenue)) {
				batch.add(click);
				count++;
			} else {
				log.debug("Click for impression ID {} with revenue {} already exists. Skipping.", impressionUuId, revenue);
			}

			if (count % BATCH_SIZE == 0 && !batch.isEmpty()) {
				clickRepository.saveAll(batch);
				clickRepository.flush();
				batch.clear();
				log.debug("Saved batch of {} clicks.", BATCH_SIZE);
			}
		}

		if (!batch.isEmpty()) {
			clickRepository.saveAll(batch);
			clickRepository.flush();
			log.debug("Saved final batch of {} clicks.", batch.size());
		}
	}
}




