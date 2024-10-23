package com.abdi.ads.manager.services.metric;

import java.util.List;

import com.abdi.ads.manager.projection.RecommendationResult;
import com.abdi.ads.manager.projection.MetricsResult;

import org.springframework.web.multipart.MultipartFile;

public interface AnalyticsService {
	String loadDataWithImpressionsAndClicks(MultipartFile impressionsFile, MultipartFile clicksFile);

	List<MetricsResult> calculateMetrics();

	List<RecommendationResult> generateRecommendations();
}
