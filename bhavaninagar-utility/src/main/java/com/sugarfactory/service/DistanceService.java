package com.sugarfactory.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.gson.Gson;
import com.sugarfactory.controller.HomeController;
import com.sugarfactory.handler.RecordNotFoundException;
import com.sugarfactory.model.DistanceInfo;
import com.sugarfactory.repository.DistanceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DistanceService {

    @Autowired
    private DistanceRepository distanceRepository;

    static Logger log = LoggerFactory.getLogger(HomeController.class);

    public DistanceInfo getBySlipNumber(Integer slipNo) {
        //DistanceInfo distanceInfo = null;
        DistanceInfo distanceInfo = distanceRepository.findBySlipNo(slipNo);

        if (distanceInfo == null) {
            log.debug("Invalid slip number : " + slipNo);
            throw new RecordNotFoundException("Invalid slip number : " + slipNo);
        }

        log.debug("Slip number : " + slipNo + " details fetched successfully ");
        return distanceInfo;
    }

    public DistanceInfo updateDistance(int slipNo, int distance) {
        DistanceInfo distanceInfo = distanceRepository.findBySlipNo(slipNo);

        if (distanceInfo != null) {
            distanceInfo.setActualDistance(distance);

            distanceInfo = distanceRepository.save(distanceInfo);
        }

        return distanceInfo;

    }

    public ResponseEntity<Resource> getCsvfile(List list) throws IOException {

        Gson gson = new Gson();
        String json = gson.toJson(list);

        File file = new File("data.json");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(json.getBytes());
        fos.close();

        //Create CSV
        JsonNode jsonTree = new ObjectMapper().readTree(file);

        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = jsonTree.elements().next();
        firstObject.fieldNames().
                forEachRemaining(fieldName ->
                {
                    csvSchemaBuilder.addColumn(fieldName);
                });
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

        File csvFile = new File("slipdistanceData.csv");
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        csvMapper.writerFor(JsonNode.class).
                with(csvSchema).writeValue(csvFile, jsonTree);

        Path path = Paths.get(csvFile.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=slipdistanceData.csv");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(csvFile.length())
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);

        }
    }
