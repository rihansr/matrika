package com.telemedicine.matrika.model.address;

import java.io.Serializable;

public class Address implements Serializable {

    private String  address;
    private String  country;
    private String  district;
    private String  upazila;
    private String  postalCode;

    public Address() {}

    public Address(String address, String country, String district, String upazila, String postalCode) {
        this.address = address;
        this.country = country;
        this.district = district;
        this.upazila = upazila;
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getUpazila() {
        return upazila;
    }

    public void setUpazila(String upazila) {
        this.upazila = upazila;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
