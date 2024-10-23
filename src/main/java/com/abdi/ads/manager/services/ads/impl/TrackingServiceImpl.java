package com.abdi.ads.manager.services.ads.impl;

import com.abdi.ads.manager.services.ads.TrackingService;
import com.abdi.ads.manager.services.ads.dto.TrackingStatus;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TrackingServiceImpl implements TrackingService {

	private final RedisTemplate<String, String> redisTemplate;

	private static final String TRACKING_KEY_PREFIX = "tracking:";

	public TrackingServiceImpl(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void updateStatus(String trackingId, TrackingStatus status) {
		String key = TRACKING_KEY_PREFIX + trackingId;
		redisTemplate.opsForValue().set(key, status.name());
	}

	@Override
	public TrackingStatus getStatus(String trackingId) {
		String key = TRACKING_KEY_PREFIX + trackingId;
		String status = redisTemplate.opsForValue().get(key);
		return status != null ? TrackingStatus.valueOf(status) : null;
	}
}


