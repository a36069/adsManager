package com.abdi.ads.manager.services.metric.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.abdi.ads.manager.model.impression.dao.ImpressionRepository;
import com.abdi.ads.manager.projection.AdvertiserStatsProjection;
import com.abdi.ads.manager.projection.AppCountryProjection;
import com.abdi.ads.manager.projection.MetricsResult;
import com.abdi.ads.manager.projection.RecommendationResult;
import com.abdi.ads.manager.services.ads.AdsDataLoaderService;
import com.abdi.ads.manager.services.metric.AnalyticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

	private static final String METRICS_FILE_PREFIX = "metrics_output_";

	private static final String RECOMMENDATIONS_FILE_PREFIX = "recommendations_output_";

	private static final String FILE_EXTENSION = ".json";
	private static final String REPORTS_DIR = "/app/reports";


	private final ImpressionRepository impressionRepository;

	private final AdsDataLoaderService dataImportService;

	@Cacheable(value = "metricsCache")
	@Override
	public List<MetricsResult> calculateMetrics() {
		log.info("Calculating metrics...");
		List<MetricsResult> metrics = impressionRepository.calculateMetrics();
		log.debug("Metrics calculated: {}", metrics);
		writeMetricsToJsonFile(metrics);
		return metrics;
	}

	@Override
	public String loadDataWithImpressionsAndClicks(MultipartFile impressionsFile, MultipartFile clicksFile) {
		log.info("Loading data with impressions and clicks...");
		String trackingId = UUID.randomUUID().toString();
		dataImportService.loadDataAsync(impressionsFile, clicksFile, trackingId);
		log.info("Data loading initiated with tracking ID: {}", trackingId);
		return trackingId;
	}

	@Cacheable(value = "recommendationsCache")
	@Override
	public List<RecommendationResult> generateRecommendations() {
		log.info("Generating recommendations...");
		List<AppCountryProjection> appCountryList = impressionRepository.findDistinctAppIdAndCountryCode();
		List<RecommendationResult> results = new ArrayList<>();

		for (AppCountryProjection appCountry : appCountryList) {
			Integer appId = appCountry.getAppId();
			String countryCode = appCountry.getCountryCode();
			log.debug("Processing app_id: {}, country_code: {}", appId, countryCode);

			List<AdvertiserStatsProjection> advertiserStats = impressionRepository.getAdvertiserStats(appId, countryCode);

			Map<Integer, Double> revenuePerImpressionMap = new HashMap<>();
			for (AdvertiserStatsProjection stats : advertiserStats) {
				Integer advertiserId = stats.getAdvertiserId();
				Long impressions = stats.getImpressions();
				Double revenue = stats.getRevenue() != null ? stats.getRevenue() : 0.0;

				if (impressions > 0) {
					Double revenuePerImpression = revenue / impressions;
					revenuePerImpressionMap.put(advertiserId, revenuePerImpression);
					log.trace("Advertiser ID: {}, Revenue/Impression: {}", advertiserId, revenuePerImpression);
				}
			}

			List<Integer> sortedAdvertiserIds = revenuePerImpressionMap.entrySet()
					.stream()
					.sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
					.map(Map.Entry::getKey)
					.limit(5)
					.collect(Collectors.toList());

			RecommendationResult result = new RecommendationResult(appId, countryCode, sortedAdvertiserIds);
			results.add(result);
			log.debug("Recommendations for app_id {} and country_code {}: {}", appId, countryCode, sortedAdvertiserIds);
		}

		log.info("Recommendations generated.");
		writeRecommendationsToJsonFile(results);

		return results;
	}


	private void writeMetricsToJsonFile(List<MetricsResult> metrics) {
		ObjectMapper objectMapper = new ObjectMapper();
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String fileName = METRICS_FILE_PREFIX + timestamp + FILE_EXTENSION;
		File file = new File(REPORTS_DIR, fileName);

		try {
			objectMapper.writeValue(file, metrics);
			log.info("Metrics written to file: {}", file.getAbsolutePath());
		} catch (IOException e) {
			log.error("Failed to write metrics to file", e);
		}
	}

	private void writeRecommendationsToJsonFile(List<RecommendationResult> recommendations) {
		ObjectMapper objectMapper = new ObjectMapper();
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String fileName = RECOMMENDATIONS_FILE_PREFIX + timestamp + FILE_EXTENSION;
		File file = new File(REPORTS_DIR, fileName);

		try {
			objectMapper.writeValue(file, recommendations);
			log.info("Recommendations written to file: {}", file.getAbsolutePath());
		} catch (IOException e) {
			log.error("Failed to write recommendations to file", e);
		}
	}

}

