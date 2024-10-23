package com.abdi.ads.manager.model.click.dao;

import java.util.UUID;

import com.abdi.ads.manager.model.click.Click;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickRepository extends JpaRepository<Click, Long> {
	boolean existsByImpressionUuidAndRevenue(String impressionId, Double revenue);
}