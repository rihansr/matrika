package com.telemedicine.matrika.model.notification;

import java.util.List;
import java.util.Map;

public class Sender {

    private Map<String, String> data;
    private String              to;
    private List<String>        registration_ids;

    public Sender(List<String> registration_ids, Map<String, String> data) {
        this.data = data;
        this.registration_ids = registration_ids;
    }

    public Sender(String to, Map<String, String> data) {
        this.data = data;
        this.to = to;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(List<String> registration_ids) {
        this.registration_ids = registration_ids;
    }
}
