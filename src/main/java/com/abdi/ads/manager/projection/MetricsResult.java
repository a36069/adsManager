package com.abdi.ads.manager.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetricsResult {

	@JsonProperty("app_id")
	private Integer appId;

	@JsonProperty("country_code")
	private String countryCode;

	@JsonProperty("impressions")
	private Long impressions;

	@JsonProperty("clicks")
	private Long clicks;

	@JsonProperty("revenue")
	private Double revenue;

	public MetricsResult(Integer appId, String countryCode, Long impressions, Long clicks, Double revenue) {
		this.appId = appId;
		this.countryCode = countryCode;
		this.impressions = impressions;
		this.clicks = clicks;
		this.revenue = revenue != null ? revenue : 0.0;
	}
}
