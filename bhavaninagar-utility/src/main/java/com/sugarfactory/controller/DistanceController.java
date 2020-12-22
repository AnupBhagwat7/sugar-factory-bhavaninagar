package com.sugarfactory.controller;

import com.sugarfactory.handler.RecordNotFoundException;
import com.sugarfactory.model.DistanceInfo;
import com.sugarfactory.repository.DistanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/nirabhima")
public class DistanceController {

    @Autowired
    private DistanceRepository distanceRepository;

    static Logger log = LoggerFactory.getLogger(DistanceController.class);

    @GetMapping("/test")
    public String test() {
        return "hello";
    }

    @GetMapping("/getSlipDistance/{slipNo}")
    public ResponseEntity<DistanceInfo> getByslipNumber (@PathVariable("slipNo") Integer slipNo)
    {
        //DistanceInfo distanceInfo = null;
        DistanceInfo distanceInfo = distanceRepository.findBySlipNo(slipNo);

        if(distanceInfo == null) {
            log.debug("Invalid slip number : " + slipNo);
            throw new RecordNotFoundException("Invalid slip number : " + slipNo);
        }

        log.debug("Slip number : " + slipNo + " details fetched successfully ");
        return new ResponseEntity<DistanceInfo>(distanceInfo, HttpStatus.OK);
    }

    @GetMapping("/getAllSlipDistanceData")
    public List<DistanceInfo> getSlipData() {

        List<DistanceInfo> list = distanceRepository.findAll();
        return list;
    }

    @GetMapping("/getSlipDistanceData/{fromDate}/{toDate}")
    public List<DistanceInfo> getSlipDataBetweenDates(@PathVariable String fromDate, @PathVariable String toDate) {

    	Date from = new Date(fromDate);
    	Date to = new Date(toDate);
        List<DistanceInfo> list = distanceRepository.findDistanceInfoByCreateDateBetween(from, to);
        return list;
    }

    @PutMapping("/updateSlipDistance/{slipNo}/{distance}")
        public String updateSlipDistance(@PathVariable Integer slipNo , @PathVariable Integer distance) {

        DistanceInfo distanceInfo = distanceRepository.findBySlipNo(slipNo);

        if(distanceInfo != null) {
            distanceRepository.updateDistance(distanceInfo.getId() , distance , "DONE" );
            log.debug("Slip number : "+ slipNo +" is updated with distance : "+ distance);
            return "Slip number : "+ slipNo +" is updated with distance : "+ distance;
        }else{
            throw new RecordNotFoundException("Invalid slip number : " + slipNo);
        }

    }
}