package com.driving_school.backend.Entity;

public class DrivingSchoolList {

    private String schoolName;
    private String googleMapLink;
    private String googleRating;
    private String phoneNumber;
    private String address;

    private String photoUrl;

    public DrivingSchoolList(){

    }

    public DrivingSchoolList(String schoolName, String googleMapLink, String googleRating, String phoneNumber, String address) {
        this.schoolName = schoolName;
        this.googleMapLink = googleMapLink;
        this.googleRating = googleRating;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getGoogleMapLink() {
        return googleMapLink;
    }

    public void setGoogleMapLink(String googleMapLink) {
        this.googleMapLink = googleMapLink;
    }

    public String getGoogleRating() {
        return googleRating;
    }

    public void setGoogleRating(String googleRating) {
        this.googleRating = googleRating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
