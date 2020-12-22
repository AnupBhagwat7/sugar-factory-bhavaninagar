package com.sugarfactory.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
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

    @GetMapping({"/"})
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("distanceInfo", null);
        return "searchUpdateDistance";
    }
    
    @GetMapping({"/searchUpdateDistance"})
    public String seachUpdatePage(Model model) {
        //model.addAttribute("distanceInfo", model.getAttribute("distanceInfo"));
        return "searchUpdateDistance";
    }
    
    @GetMapping({"/distanceInfo"})
    public String distanceInfoPage(Model model) {
        //model.addAttribute("distanceInfo", model.getAttribute("distanceInfo"));
        return "distanceInfo";
    }
    
    
    @GetMapping("{tab}")
    public String tab(@PathVariable String tab) {
        if (Arrays.asList("searchUpdateDistance", "distanceInfo")
                .contains(tab)) {
            return tab;
        }

        return "empty";
    }

    @GetMapping(value = "/getSlipDistance")
    public String getBySlipNumber (@RequestParam(value = "slipNumber", required = false) Integer slipNumber, Model model)
    {
        DistanceInfo distanceInfo = distanceService.getBySlipNumber(slipNumber);

        if(distanceInfo == null) {
            log.debug("Invalid slip number : " + slipNumber);
            model.addAttribute("message", "Please enter a valid slip number and then search again");
            return "redirect:/searchUpdateDistance";
            //throw new RecordNotFoundException("Invalid slip number : " + slipNo);
        }

        log.debug("Distance details fetched are : " + distanceInfo);
        model.addAttribute("distanceInfo", distanceInfo);
        log.debug("Slip number : " + slipNumber + " details fetched successfully ");
        //return "index";
        return "searchUpdateDistance";
    }

    @GetMapping(value = "/getSlipDistanceData")
    public ResponseEntity<Resource> getFile(@RequestParam(value = "fromDate", required = false) String fromDate, 
    		@RequestParam(value = "toDate", required = false) String toDate) throws IOException {
    	
    	Date from = new Date(fromDate);
    	Date to = new Date(toDate);
    	
        List<DistanceInfo> list = distanceRepository.findDistanceInfoByCreateDateBetween(from, to);
        
        Gson gson = new Gson();
        String json = gson.toJson(list);
        
        File file =  new File("data.json");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(json.getBytes());
        fos.close();
        
        //Create CSV
        JsonNode jsonTree = new ObjectMapper().readTree(file);
        
        Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = jsonTree.elements().next();
        firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);} );
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        
        File csvfile = new File("slipdistanceData.csv");
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.writerFor(JsonNode.class)
          .with(csvSchema)
          .writeValue(csvfile, jsonTree);
        //

        //FileInputStream fis = new FileInputStream(file);
        //fis.read();
        //return IOUtils.toByteArray(in);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=slipdistanceData.csv");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(csvfile.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        
        //InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(csvfile.length())
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    	
    }
    
    @GetMapping("/getSlipDistanceDataold")
    public ModelAndView getSlipDataBetweenDates(@RequestParam(value = "fromDate", required = false) String fromDate, 
    		@RequestParam(value = "toDate", required = false) String toDate) {

    	Date from = new Date(fromDate);
    	Date to = new Date(toDate);
    	
        List<DistanceInfo> list = distanceRepository.findDistanceInfoByCreateDateBetween(from, to);
        
        Gson gson = new Gson();
        String json = gson.toJson(list);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("distanceInfo");
        modelAndView.addObject(json);

        return modelAndView;
        
        //return list;
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
            
            model.addAttribute("message", "Distance updated successfully!!!");
            log.debug("Slip number : "+ distanceInfo2.getSlipNo() +" is updated with distance : "+ distanceInfo2.getActualDistance());
            model.addAttribute("distanceInfo", null);
        }

        return "searchUpdateDistance";
    }

}