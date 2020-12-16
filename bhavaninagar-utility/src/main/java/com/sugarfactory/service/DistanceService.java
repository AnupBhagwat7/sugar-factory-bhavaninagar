package com.sugarfactory.service;

import com.sugarfactory.controller.HomeController;
import com.sugarfactory.handler.RecordNotFoundException;
import com.sugarfactory.model.DistanceInfo;
import com.sugarfactory.repository.DistanceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistanceService {

    @Autowired
    private DistanceRepository distanceRepository;

    static Logger log = LoggerFactory.getLogger(HomeController.class);

    public DistanceInfo getBySlipNumber (Integer slipNo)
    {
        //DistanceInfo distanceInfo = null;
        DistanceInfo distanceInfo = distanceRepository.findBySlipNo(slipNo);

        if(distanceInfo == null) {
            log.debug("Invalid slip number : " + slipNo);
            throw new RecordNotFoundException("Invalid slip number : " + slipNo);
        }

        log.debug("Slip number : " + slipNo + " details fetched successfully ");
        return distanceInfo;
    }

    public DistanceInfo updateDistance(int slipNo , int distance)
    {
            DistanceInfo distanceInfo = distanceRepository.findBySlipNo(slipNo);

            if(distanceInfo != null) {
                distanceInfo.setActualDistance(distance);

                distanceInfo = distanceRepository.save(distanceInfo);
            }

            return distanceInfo;

    }
}
