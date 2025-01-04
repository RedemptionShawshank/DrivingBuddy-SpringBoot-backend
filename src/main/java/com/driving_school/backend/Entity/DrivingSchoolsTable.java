package com.driving_school.backend.Entity;


import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "DrivingSchoolsTable")
public class DrivingSchoolsTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "school_name")
    private String school_name;

    @Column(name = "school_id",nullable = false)
    private String school_id;

    @Column(name = "google_rating")
    private String google_rating;

    @Column(name = "customer_reviews",columnDefinition ="JSON")
    private String customer_reviews;

    @Column(name = "last_updated")
    private Timestamp last_updated;

    @Column(name="phone_number")
    private String phone_number;

    @Column(name="photo_url",columnDefinition ="JSON")
    private String photo_url;

    @Column(name="address")
    private String address;

    @Column(name="google_map_link")
    private String google_map_link;

    @Column(name="city")
    private String city;

    @Column(name="locality")
    private String locality;

    @Column(name="location")
    String location;

    public DrivingSchoolsTable(){

    }

    public DrivingSchoolsTable(String school_name, String school_id, String google_rating, String customer_reviews, Timestamp last_updated) {
        this.school_name = school_name;
        this.school_id = school_id;
        this.google_rating = google_rating;
        this.customer_reviews = customer_reviews;
        this.last_updated = last_updated;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getGoogle_rating() {
        return google_rating;
    }

    public void setGoogle_rating(String google_rating) {
        this.google_rating = google_rating;
    }

    public String getCustomer_reviews() {
        return customer_reviews;
    }

    public void setCustomer_reviews(String customer_reviews) {
        this.customer_reviews = customer_reviews;
    }

    public Timestamp getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Timestamp last_updated) {
        this.last_updated = last_updated;
    }


    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGoogle_map_link() {
        return google_map_link;
    }
    public void setGoogle_map_link(String google_map_link) {
        this.google_map_link = google_map_link;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
