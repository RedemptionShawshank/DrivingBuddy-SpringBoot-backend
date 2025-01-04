package com.driving_school.backend.Entity;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class DrivingSchoolResponse {

    private String photoUrl;
    private String phoneNumber;
    private String googleRatings;
    private List<JsonNode> reviews;
    private String address;
    private String googleMapLink;
    private String locality;
    private String city;

    public DrivingSchoolResponse(){

    }

    public DrivingSchoolResponse(String photoUrl, String phoneNumber, String googleRatings, List<JsonNode> reviews) {
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.googleRatings = googleRatings;
        this.reviews = reviews;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGoogleRatings() {
        return googleRatings;
    }

    public void setGoogleRatings(String googleRatings) {
        this.googleRatings = googleRatings;
    }

    public List<JsonNode> getReviews() {
        return reviews;
    }

    public void setReviews(List<JsonNode> reviews) {
        this.reviews = reviews;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGoogleMapLink() {
        return googleMapLink;
    }

    public void setGoogleMapLink(String googleMapLink) {
        this.googleMapLink = googleMapLink;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
