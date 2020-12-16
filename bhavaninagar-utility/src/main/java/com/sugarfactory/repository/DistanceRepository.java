package com.sugarfactory.repository;

import com.sugarfactory.model.DistanceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
//@Profile("admissions")
public interface DistanceRepository extends JpaRepository<DistanceInfo, Long> {


	@Modifying
    @Query("Update DistanceInfo d SET d.actualDistance =:actualDistance, d.status =:status  WHERE d.id=:id ")
    public void updateDistance(@Param("id") Long id, @Param("actualDistance") Integer actualDistance, @Param("status") String status);

    DistanceInfo findBySlipNo(Integer slipNo);
}