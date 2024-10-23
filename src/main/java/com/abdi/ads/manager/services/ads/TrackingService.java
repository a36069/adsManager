package com.abdi.ads.manager.services.ads;

import com.abdi.ads.manager.services.ads.dto.TrackingStatus;

public interface TrackingService {
	void updateStatus(String trackingId, TrackingStatus status);

	TrackingStatus getStatus(String trackingId);
}
