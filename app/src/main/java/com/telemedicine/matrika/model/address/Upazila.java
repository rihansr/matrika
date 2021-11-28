package com.telemedicine.matrika.model.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Upazila implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("district_id")
    @Expose
    private String district_id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("bn_name")
    @Expose
    private String bn_name;
    @SerializedName("url")
    @Expose
    private String url;

    public Upazila() {}

    /**
     *
     * @param id
     * @param district_id
     * @param name
     * @param bn_name
     * @param url
     */

    public Upazila(String id, String district_id, String name, String bn_name, String url) {
        super();
        this.id = id;
        this.district_id = district_id;
        this.name = name;
        this.bn_name = bn_name;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String division_id) {
        this.district_id = division_id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
