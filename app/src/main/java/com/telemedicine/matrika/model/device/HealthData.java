package com.telemedicine.matrika.model.device;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class HealthData implements Serializable {

    @SerializedName("status")
    @Expose
    private Integer code;
    @SerializedName("channel")
    @Expose
    private Channel channel;
    @SerializedName("feeds")
    @Expose
    private List<Feed> feeds = null;

    public HealthData() {}

    public Integer getCode() {
        return code == null ? 200 : code;
    }

    public Channel getChannel() {
        return channel;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }
}
