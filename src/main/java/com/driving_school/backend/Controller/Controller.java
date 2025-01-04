package com.driving_school.backend.Controller;

import com.driving_school.backend.Entity.DrivingSchoolList;
import com.driving_school.backend.Entity.DrivingSchoolResponse;
import com.driving_school.backend.Service.GooglePlacesService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    @Autowired
    private GooglePlacesService googlePlacesService;

    @GetMapping("/reviews")//receive key-value, value is the name of the driving school
    public ResponseEntity<DrivingSchoolResponse> fetchReviews(@RequestParam String schoolName,@RequestParam String cityName) throws IOException, JSONException {

//        ObjectMapper mapper = new ObjectMapper();
//
//        Map<String,String> jsonMap = mapper.convertValue(json, Map.class);
//        String schoolName = jsonMap.get("schoolName");
//        String cityName = jsonMap.get("cityName");
        DrivingSchoolResponse result = googlePlacesService.getPlaceReviews(schoolName,cityName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/drivingschools")
    public ResponseEntity<List<DrivingSchoolList>> fetchDrivingSchoolsList(@RequestParam String cityName) {
        List<DrivingSchoolList> result = googlePlacesService.getDrivingSchoolsList(cityName.trim());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/addSchools")
    public ResponseEntity<Map<String, String>> addSchools(@RequestBody JsonNode localitySearch){

        ObjectMapper mapper = new ObjectMapper();

        Map<String,String> localitySearchMap = mapper.convertValue(localitySearch, Map.class);
        String result = googlePlacesService.addSchools(localitySearchMap);
        Map<String,String> response = Map.of("result", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/checkCity")
    public ResponseEntity<Map<String, Boolean>> checkCity(@RequestParam String cityName) {
        Boolean result = googlePlacesService.checkCity(cityName.trim());
        Map<String,Boolean> response = Map.of("result", result);

        if(response.get("result") == true){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> fetchCities() {
        List<String> result = googlePlacesService.getCities();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
