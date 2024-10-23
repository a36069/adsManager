package com.abdi.ads.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(exclude = { RedisAutoConfiguration.class})
public class AdsManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdsManagerApplication.class, args);
	}

}
