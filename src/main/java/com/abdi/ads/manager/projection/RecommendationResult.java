package com.abdi.ads.manager.projection;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RecommendationResult {

	@JsonProperty("app_id")
	private Integer appId;

	@JsonProperty("country_code")
	private String countryCode;

	@JsonProperty("recommended_advertiser_ids")
	private List<Integer> recommendedAdvertiserIds;

	public RecommendationResult(Integer appId, String countryCode, List<Integer> recommendedAdvertiserIds) {
		this.appId = appId;
		this.countryCode = countryCode;
		this.recommendedAdvertiserIds = recommendedAdvertiserIds;
	}
}

