package com.abdi.ads.manager.api.web.metric;

import java.util.List;

import com.abdi.ads.manager.api.web.validator.ValidFile;
import com.abdi.ads.manager.exceptions.DataNotFoundException;
import com.abdi.ads.manager.projection.RecommendationResult;
import com.abdi.ads.manager.services.ads.impl.TrackingServiceImpl;
import com.abdi.ads.manager.services.metric.AnalyticsService;
import com.abdi.ads.manager.projection.MetricsResult;
import com.abdi.ads.manager.services.ads.dto.TrackingResponse;
import com.abdi.ads.manager.services.ads.dto.TrackingStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Slf4j
@RequestMapping("/metrics")
@RestController
@Validated
public class MetricsController {

	private AnalyticsService analyticsService;

	private TrackingServiceImpl trackingService;

	@PostMapping("/loadData")
	public ResponseEntity<TrackingResponse> loadData(@ValidFile @RequestParam(value = "impressionsFile", required = false) MultipartFile impressionsFile,
			@ValidFile @RequestParam(value = "clicksFile", required = false) MultipartFile clicksFile) {
		String trackingId = analyticsService.loadDataWithImpressionsAndClicks(impressionsFile, clicksFile);
		return ResponseEntity.ok(new TrackingResponse(trackingId, TrackingStatus.PENDING));
	}


	@GetMapping("/loadData/state/{trackingId}")
	public ResponseEntity<TrackingResponse> getStatus(@PathVariable String trackingId) {
		TrackingStatus status = trackingService.getStatus(trackingId);
		if (status == null) {
			throw new DataNotFoundException("Tracking ID not found: " + trackingId);
		}
		return ResponseEntity.ok(new TrackingResponse(trackingId, status));
	}

	@GetMapping()
	public ResponseEntity<List<MetricsResult>> getMetrics() {
		List<MetricsResult> metrics = analyticsService.calculateMetrics();
		return ResponseEntity.ok(metrics);
	}

	@GetMapping("/recommendations")
	public ResponseEntity<List<RecommendationResult>> getRecommendations() {
		List<RecommendationResult> recommendations = analyticsService.generateRecommendations();
		return ResponseEntity.ok(recommendations);
	}

}
