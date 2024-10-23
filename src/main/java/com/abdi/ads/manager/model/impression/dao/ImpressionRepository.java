package com.abdi.ads.manager.model.impression.dao;

import java.util.List;
import java.util.Optional;

import com.abdi.ads.manager.model.impression.Impression;
import com.abdi.ads.manager.projection.AdvertiserStatsProjection;
import com.abdi.ads.manager.projection.AppCountryProjection;
import com.abdi.ads.manager.projection.MetricsResult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpressionRepository extends JpaRepository<Impression, Long> {

	@Query("SELECT DISTINCT i.appId AS appId, i.countryCode AS countryCode FROM Impression i")
	List<AppCountryProjection> findDistinctAppIdAndCountryCode();

	@Query("SELECT i.advertiserId AS advertiserId, COUNT(i) AS impressions, SUM(c.revenue) AS revenue " +
			"FROM Impression i LEFT JOIN Click c ON i.id = c.impression.id " +
			"WHERE i.appId = :appId AND i.countryCode = :countryCode " +
			"GROUP BY i.advertiserId")
	List<AdvertiserStatsProjection> getAdvertiserStats(@Param("appId") Integer appId, @Param("countryCode") String countryCode);

	@Query("SELECT new com.abdi.ads.manager.projection.MetricsResult(i.appId, i.countryCode, COUNT(i), COUNT(c), SUM(c.revenue)) " +
			"FROM Impression i LEFT JOIN Click c ON i.id = c.impression.id " +
			"GROUP BY i.appId, i.countryCode")
	List<MetricsResult> calculateMetrics();

	@Query("SELECT i.uuid FROM Impression i WHERE i.uuid IN :uuids")
	List<String> findUuidsIn(@Param("uuids") List<String> uuids);

	Optional<Impression> findByUuid(String uuid);

}
