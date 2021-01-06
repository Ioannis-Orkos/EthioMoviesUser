package com.ioannisnicos.ethiomoviesuser.models;

import com.google.gson.annotations.SerializedName;

public class Stores {

    private int id;
    private String google_id;
    private String notif_token;

    private String first_name;
    private String last_name;
    private String display_name;
    private String email;

    private String language;

    private String prof_img;
    @SerializedName("banner_img")
    private String banner;


    private String description;


    private String business_name;
    private String address;
    private double geolng;
    private double geolat;

    private String reg_date;

    public Stores(int id, String google_id, String notif_token, String first_name, String last_name, String display_name, String email, String language, String prof_img, String banner, String description, String business_name, String address, double geolng, double geolat, String reg_date) {
        this.id = id;
        this.google_id = google_id;
        this.notif_token = notif_token;
        this.first_name = first_name;
        this.last_name = last_name;
        this.display_name = display_name;
        this.email = email;
        this.language = language;
        this.prof_img = prof_img;
        this.banner = banner;
        this.description = description;
        this.business_name = business_name;
        this.address = address;
        this.geolng = geolng;
        this.geolat = geolat;
        this.reg_date = reg_date;
    }

    public Stores() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public String getNotif_token() {
        return notif_token;
    }

    public void setNotif_token(String notif_token) {
        this.notif_token = notif_token;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProf_img() {
        return prof_img;
    }

    public void setProf_img(String prof_img) {
        this.prof_img = prof_img;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getGeolng() {
        return geolng;
    }

    public void setGeolng(double geolng) {
        this.geolng = geolng;
    }

    public double getGeolat() {
        return geolat;
    }

    public void setGeolat(double geolat) {
        this.geolat = geolat;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }
}
