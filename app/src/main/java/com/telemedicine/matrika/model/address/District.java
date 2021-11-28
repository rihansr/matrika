package com.telemedicine.matrika.model.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class District implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("division_id")
    @Expose
    private String division_id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("bn_name")
    @Expose
    private String bn_name;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("upazila")
    @Expose
    private List<Upazila> upazilas;

    public District() {}

    /**
     * @param id
     * @param division_id
     * @param name
     * @param bn_name
     * @param lat
     * @param lon
     * @param url
     * @param upazilas
     */

    public District(String id, String division_id, String name, String bn_name, String lat, String lon, String url, List<Upazila> upazilas) {
        super();
        this.id = id;
        this.division_id = division_id;
        this.name = name;
        this.bn_name = bn_name;
        this.lat = lat;
        this.lon = lon;
        this.url = url;
        this.upazilas = upazilas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDivision_id() {
        return division_id;
    }

    public void setDivision_id(String division_id) {
        this.division_id = division_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBn_name() {
        return bn_name;
    }

    public void setBn_name(String bn_name) {
        this.bn_name = bn_name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Upazila> getUpazilas() {
        return upazilas;
    }

    public void setUpazilas(List<Upazila> upazilas) {
        this.upazilas = upazilas;
    }
}
