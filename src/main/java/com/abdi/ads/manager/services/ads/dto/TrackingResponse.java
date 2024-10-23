package com.abdi.ads.manager.services.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackingResponse {
	private String trackingId;
	private TrackingStatus status;
}
