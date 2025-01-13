package com.driving_school.backend.Service;

import com.driving_school.backend.Entity.DrivingSchoolList;
import com.driving_school.backend.Entity.DrivingSchoolResponse;
import com.driving_school.backend.Entity.DrivingSchoolsTable;
import com.driving_school.backend.Repository.DrivingSchoolRepositiory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;


@Service

public class GooglePlacesService {

    private static final String API_KEY = "";
    private static final String FIND_PLACE_API_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json";
    private static final String PLACE_DETAILS_API_URL = "https://maps.googleapis.com/maps/api/place/details/json";
    private static final String PLACE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo";
    private static final String GOOGLE_MAP = "https://www.google.com/maps/search/";
    private static final String GOOGLE_MAP_SEARCH = "https://maps.googleapis.com/maps/api/place/textsearch/json";
    private static final String GOOGLE_MAP_LOCATION = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final long TIMER_THRESHOLD = TimeUnit.DAYS.toMillis(30); // 30 days

    @Autowired
    private DrivingSchoolRepositiory drivingSchoolRepositiory;

    @Autowired
    private RestTemplate restTemplate;

    private DrivingSchoolsTable addDrivingSchoolToDatabase(JsonNode allInfo, Map<String,Object> otherInfo, String schoolName, String placeId,String cityName) throws JSONException {

        DrivingSchoolsTable ds = new DrivingSchoolsTable();

        try {

            StringBuffer locality = new StringBuffer("");
            HashMap<String, String> addressLocality = new HashMap<>();

            JSONObject responseObject = new JSONObject(allInfo.get("result").toString());
            JSONArray addressComponents = responseObject.getJSONArray("address_components");

            for (int i = 0; i < addressComponents.length(); i++) {

                JSONObject component = addressComponents.getJSONObject(i);
                JSONArray types = component.getJSONArray("types");

                if (types.toString().contains("locality")) {
                    if (!addressLocality.containsKey("locality")) {
                        addressLocality.put("locality", component.getString("long_name"));
                    } else {
                        addressLocality.put("locality", addressLocality.get("locality") + "," + component.getString("long_name"));
                    }
                }


            }
            StringBuffer city = new StringBuffer(cityName);
            if (addressLocality.get("locality").contains(city.toString())) {
                StringBuffer str = new StringBuffer(addressLocality.get("locality"));
                int index = addressLocality.get("locality").indexOf(city.toString());
                if(index!=0){
                    String[] arr = addressLocality.get("locality").split(",");
                    if (arr[arr.length - 1].equals(city.toString())) {
                        str.replace(index - 1, index + city.length(), "");
                    } else {
                        str.replace(index, index + city.length() + 1, "");
                    }
                    addressLocality.put("locality", str.toString());
                }
            }

            ds.setSchool_name(schoolName);

            JSONArray reviewArray = responseObject.getJSONArray("reviews");
            ds.setCustomer_reviews(reviewArray.toString());
            ds.setGoogle_rating(allInfo.get("result").get("rating").toString() != null ? allInfo.get("result").get("rating").toString() : "0");
            ds.setSchool_id(placeId);
            ds.setLast_updated(new Timestamp(System.currentTimeMillis()));
            ds.setPhone_number(otherInfo.get("phone_number").toString() != null ? otherInfo.get("phone_number").toString() : "Not available");
            ds.setPhoto_url(otherInfo.get("photo_url").toString());
            ds.setAddress(allInfo.get("result").get("formatted_address").toString().substring(1, allInfo.get("result").get("formatted_address").toString().length() - 1) != null ? allInfo.get("result").get("formatted_address").toString().substring(1, allInfo.get("result").get("formatted_address").toString().length() - 1) : "Address not available");

            String location = allInfo.get("result").get("geometry").get("location").get("lat").toString() + "," + allInfo.get("result").get("geometry").get("location").get("lng").toString();
            URI uri = URI.create("https://www.google.com/maps/search/")
                    .resolve("?");
            URI googleMapLink = URI.create(uri + "api=1&query=" + location + "&query_place_id=" + placeId);
            ds.setGoogle_map_link(googleMapLink.toString());
            ds.setCity(city.toString());
            ds.setLocality(addressLocality.get("locality"));
            ds.setLocation(location);
            drivingSchoolRepositiory.save(ds);
        } catch (Exception e) {
            System.out.println("Error while adding driving school to database: " + e.getMessage());
        }

        return ds;
    }

    public DrivingSchoolResponse getPlaceReviews(String schoolName,String cityName) throws IOException, JSONException {
        //String input = "Escort Motor Driving School"; // Replace with the place name

        DrivingSchoolsTable schoolInfo = drivingSchoolRepositiory.findByschool_name(schoolName);

        Map<String,Object> schoolDetails = new HashMap<>();
        DrivingSchoolResponse drivingSchoolResponse = new DrivingSchoolResponse();

        if(schoolInfo!=null){

            schoolDetails.put("googleRatings",schoolInfo.getGoogle_rating());
            schoolDetails.put("phoneNumber",schoolInfo.getPhone_number());
            schoolDetails.put("photoUrl",schoolInfo.getPhoto_url());

            drivingSchoolResponse.setGoogleRatings(schoolInfo.getGoogle_rating());
            drivingSchoolResponse.setPhoneNumber(schoolInfo.getPhone_number());
            drivingSchoolResponse.setPhotoUrl(schoolInfo.getPhoto_url());
            drivingSchoolResponse.setAddress(schoolInfo.getAddress());
            drivingSchoolResponse.setGoogleMapLink(schoolInfo.getGoogle_map_link());
            drivingSchoolResponse.setCity(schoolInfo.getCity());
            drivingSchoolResponse.setLocality(schoolInfo.getLocality());

            long lastUpdatedTime = schoolInfo.getLast_updated().getTime();
            long currentTime = System.currentTimeMillis();

            // Check if timer has exceeded
            if (currentTime - lastUpdatedTime < TIMER_THRESHOLD) {
                schoolDetails.put("reviews",schoolInfo.getCustomer_reviews());
                List<JsonNode> arrayList = stringToJsonArray(schoolInfo.getCustomer_reviews());
                drivingSchoolResponse.setReviews(arrayList);
            }
            else{
                String placeId = schoolInfo.getSchool_id();
                List<JsonNode> jsonArray = stringToJsonArray(getPlaceDetails(placeId).get("result").get("reviews").toString());
                schoolDetails.put("reviews",getPlaceDetails(placeId));
                //not updating because it is get request and should not apply post or put request things
                drivingSchoolResponse.setReviews(jsonArray);
            }

        }
        else{
            String placeId = getPlaceId(schoolName);
            System.out.println("Place ID: " + placeId);
            JsonNode allInfo = getPlaceDetails(placeId);
            Map<String,Object> otherInfo = getShopDetails(placeId);

            DrivingSchoolsTable ds = addDrivingSchoolToDatabase(allInfo,otherInfo,schoolName,placeId,cityName);

            schoolDetails.put("googleRatings",ds.getGoogle_rating());
            schoolDetails.put("reviews",ds.getCustomer_reviews());
            schoolDetails.put("phoneNumber",ds.getPhone_number());
            schoolDetails.put("photoUrl",ds.getPhoto_url());

            drivingSchoolResponse.setGoogleRatings(ds.getGoogle_rating());
            drivingSchoolResponse.setReviews(stringToJsonArray(ds.getCustomer_reviews()));
            drivingSchoolResponse.setPhoneNumber(ds.getPhone_number());
            drivingSchoolResponse.setPhotoUrl(ds.getPhoto_url());
            drivingSchoolResponse.setAddress(ds.getAddress());
            drivingSchoolResponse.setGoogleMapLink(ds.getGoogle_map_link());
            drivingSchoolResponse.setCity(ds.getCity());
            drivingSchoolResponse.setLocality(ds.getLocality());
        }
        return drivingSchoolResponse;
    }

    private List<JsonNode> stringToJsonArray(String reviews){
        String inputString = reviews.substring(1,reviews.length()-1);
        String[] jsonObjects = inputString.split("(?<=\\})(?=\\{)");
        String regex = "\\{[^{}]+\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonObjects[0]);
        List<JsonNode>jsonArray = new ArrayList<>();

        try{

            while(matcher.find()){
                JsonNode node = new ObjectMapper().readTree(matcher.group());
                jsonArray.add(node);
            }

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonArray;
    }

    private JsonNode stringToJson(String reviews) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = null;
        try {
//            System.out.println("Place Details API Response: " + response);
            rootNode = objectMapper.readTree(reviews);
            //result = objectMapper.convertValue(rootNode.get("result"), Map.class);

        } catch (Exception e) {
            System.err.println("Error while fetching place details: " + e.getMessage());
        }
        return rootNode;
    }

    private JsonNode getPlaceDetails(String placeId) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Build the API URL
        String url = UriComponentsBuilder.fromHttpUrl(PLACE_DETAILS_API_URL)
                .queryParam("place_id", placeId)
                .queryParam("fields", "name,rating,reviews,formatted_address,geometry/location,address_components")
                .queryParam("key", API_KEY)
                .toUriString();

        String response = null;
        try {
            response = restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("Error while fetching place details: " + e.getMessage());
        }
        return stringToJson(response);
    }

    private static String extractPlaceId(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Navigate to candidates array and fetch place_id
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                return firstCandidate.path("place_id").asText();
            } else {
                System.err.println("No candidates found in the response.");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    // Method to get placeId using Find Place API
    private String getPlaceId(String schoolName) {
        //RestTemplate restTemplate = new RestTemplate();

        String formattedInput = schoolName.replace(" ", "+");

        // Build the API URL
        String url = UriComponentsBuilder.fromHttpUrl(FIND_PLACE_API_URL)
                .queryParam("input", formattedInput)
                .queryParam("inputtype", "textquery")
                .queryParam("fields", "place_id")
                .queryParam("key", API_KEY)
                .toUriString();

        // Make the API call
        try {
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Find Place API Response: " + response);

            String placeId = extractPlaceId(response);
            System.out.println("Extracted Place ID: " + placeId);
            return placeId;

        } catch (Exception e) {
            System.err.println("Error while fetching placeId: " + e.getMessage());
            return null;
        }
    }

    private Map<String, Object> getShopDetails(String placeId) {
        String url = PLACE_DETAILS_API_URL
                + "?placeid=" + placeId
                + "&fields=formatted_phone_number,photos,user_ratings_total"
                + "&key=" + API_KEY;

        String response = restTemplate.getForObject(url, String.class);

        Map<String, Object> result = new HashMap<>();
        try {
            JsonNode responseMap = stringToJson(response);

            // Extract phone number
            if (responseMap.get("result").get("formatted_phone_number") == null) {
                result.put("phone_number", "Unavailable");
            } else {
                result.put("phone_number", responseMap.get("result").get("formatted_phone_number").toString().substring(1, responseMap.get("result").get("formatted_phone_number").toString().length() - 1));
            }

            if(responseMap.get("result").get("photos") == null){
                result.put("photo_url", "No photo available");
            }
            else{

                JsonNode photos = responseMap.get("result").get("photos").get(0);
                // Extract photo url here
                String photoUrl = PLACE_PHOTO_URL
                        + "?maxwidth=400&photoreference=" + photos.get("photo_reference").toString().substring(1,photos.get("photo_reference").toString().length()-1)
                        + "&key=" + API_KEY;
                result.put("photo_url",photoUrl);
            }



        } catch (Exception e) {
            throw new RuntimeException("Error parsing response: " + e.getMessage());
        }
        return result;
    }

    //----------------------------------------------------------------------------------------------

    public List<DrivingSchoolList> getDrivingSchoolsList(String city){

        List<DrivingSchoolsTable> allSchools = drivingSchoolRepositiory.findBycity(city);

        List<DrivingSchoolList> drivingSchoolList = new ArrayList<>();

        for(DrivingSchoolsTable school:allSchools){
            DrivingSchoolList drivingSchool = new DrivingSchoolList();
            drivingSchool.setAddress(school.getAddress());
            drivingSchool.setGoogleMapLink(school.getGoogle_map_link());
            drivingSchool.setGoogleRating(school.getGoogle_rating());
            drivingSchool.setPhoneNumber(school.getPhone_number());
            drivingSchool.setSchoolName(school.getSchool_name().toLowerCase());
            drivingSchool.setPhotoUrl(school.getPhoto_url());
            drivingSchoolList.add(drivingSchool);
        }
        return drivingSchoolList;
    }


    //----------------------------------------------------------------------------------------------

    public String addSchools(Map<String,String> localitySearchMap){

        String urlLocation = UriComponentsBuilder.fromHttpUrl(GOOGLE_MAP_LOCATION)
                .queryParam("address", localitySearchMap.get("searchArea"))
                .queryParam("key", API_KEY)
                .toUriString();

        String locationResponse = restTemplate.getForObject(urlLocation, String.class);
        JsonNode locationResponseJson = stringToJson(locationResponse);
        String location = "";

        try{
            location += locationResponseJson.get("results").get(0).get("geometry").get("location").get("lat").toString() + "," + locationResponseJson.get("results").get(0).get("geometry").get("location").get("lng").toString();
        }catch(Exception e){
            System.out.println("Error while fetching location: " + e.getMessage());
        }

//                        .queryParam("radius", localitySearchMap.get("radius"))    ||||                   .queryParam("location", location)

        String url = UriComponentsBuilder.fromHttpUrl(GOOGLE_MAP_SEARCH)
                .queryParam("query", localitySearchMap.get("search").replace(" ", "+"))
                .queryParam("fields", "name,place_id,geometry,user_ratings_total")
                .queryParam("key", API_KEY)
                .toUriString();

        System.out.println("URL: " + url);

        String testSearchResponse = restTemplate.getForObject(url, String.class);
        JsonNode testSearchResponseJson = stringToJson(testSearchResponse);
        String arrayList = testSearchResponseJson.get("results").toString();
        JsonNode allSchools = stringToJson(arrayList);

        //List<JsonNode> schoolList = stringToJsonArray(arrayList);


        try {

            for (int i = 0; i < allSchools.size(); i++) {

                String schoolName = allSchools.get(i).get("name").toString().substring(1, allSchools.get(i).get("name").toString().length() - 1);
                String placeId = allSchools.get(i).get("place_id").toString().substring(1, allSchools.get(i).get("place_id").toString().length() - 1);
                Integer user_ratings_total = 0;
                if(allSchools.get(i).get("user_ratings_total")!=null) {
                    user_ratings_total = allSchools.get(i).get("user_ratings_total").asInt();
                }

                DrivingSchoolsTable school = drivingSchoolRepositiory.findByschool_name(schoolName);
                System.out.println("School: " + schoolName);
                if (school == null) {
//                    String placeId = getPlaceId(schoolName);
                    JsonNode allInfo = getPlaceDetails(placeId);
                    Map<String, Object> otherInfo = getShopDetails(placeId);
                    if(allInfo!=null && otherInfo!=null) {
                        DrivingSchoolsTable ds = addDrivingSchoolToDatabase(allInfo, otherInfo, schoolName, placeId, localitySearchMap.get("city"));
                    }
                }

            }
            return "success";
        }catch(Exception e){
            System.out.println("Error while adding schools: " + e.getMessage());
            return "failure";
        }
    }

    public boolean checkCity(String cityName) {
        return drivingSchoolRepositiory.existsByCityIgnoreCase(cityName);
    }


    public List<String> getCities() {
        return drivingSchoolRepositiory.findDistinctCity();
    }



}

