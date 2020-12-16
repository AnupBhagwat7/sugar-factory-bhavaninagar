package com.sugarfactory.controller;

import java.sql.Timestamp;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sugarfactory.model.DistanceInfo;
import com.sugarfactory.repository.DistanceRepository;
import com.sugarfactory.service.DistanceService;


@Controller
public class HomeController {

    @Autowired
    private DistanceService distanceService;

    @Autowired
    private DistanceRepository distanceRepository;

    static Logger log = LoggerFactory.getLogger(HomeController.class);

    @Value("${spring.application.name}")
    String appName;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("distanceInfo", null);
        model.addAttribute("message", "");
        model.addAttribute("distError", "");
        return "home";
    }

    @GetMapping(value = "/getSlipDistance")
    public String getBySlipNumber (@RequestParam(value = "slipNumber", required = false) Integer slipNumber, Model model)
    {
        DistanceInfo distanceInfo = distanceService.getBySlipNumber(slipNumber);

        if(distanceInfo == null) {
            log.debug("Invalid slip number : " + slipNumber);
            model.addAttribute("message", "Please enter a valid slip number and then search again");
            return "redirect:/";
            //throw new RecordNotFoundException("Invalid slip number : " + slipNo);
        }

        log.debug("Distance details fetched are : " + distanceInfo);
        model.addAttribute("distanceInfo", distanceInfo);
        log.debug("Slip number : " + slipNumber + " details fetched successfully ");
        return "home";
    }

    @PostMapping("/updateSlipDistance")
    public String updateDistance( @Valid DistanceInfo distanceInfo,
                                  BindingResult result,
                                  Model model) {
        
        Optional<DistanceInfo> distanceInfo1 = distanceRepository.findById(distanceInfo.getId());
        if (distanceInfo1.get().getDistance() < distanceInfo.getActualDistance()) {
        	log.debug("Actual distance : "+ distanceInfo.getActualDistance() +" factory distance: "+ distanceInfo1.get().getDistance());
        	model.addAttribute("distError", "Actual distance should be less than or equal to factory distance");
        	return "home";
        }
        
        log.debug("Before updating distanceInfo object values are: "+ distanceInfo);

        if(distanceInfo != null){
            DistanceInfo distanceInfo2 = distanceInfo1.get();
            distanceInfo2.setActualDistance(distanceInfo.getActualDistance());
            distanceInfo2.setStatus("DONE");
            distanceInfo2.setUpdateDate(new Timestamp(System.currentTimeMillis()));
            distanceRepository.save(distanceInfo2);
            model.addAttribute("distanceInfo", distanceInfo2);
            model.addAttribute("message", "Distance updated successfully!!!");
            log.debug("Slip number : "+ distanceInfo2.getSlipNo() +" is updated with distance : "+ distanceInfo2.getActualDistance());
        }

        return "redirect:/";
    }

}