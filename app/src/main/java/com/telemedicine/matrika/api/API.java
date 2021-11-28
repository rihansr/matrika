package com.telemedicine.matrika.api;

public class API {

    public static String CHANNEL_ID = "";
    public static String READ_API_KEY = "";

    public static String getFeedURL(int limit){
        return "https://api.thingspeak.com/channels/" + CHANNEL_ID + "/feeds.json?api_key=" + READ_API_KEY + "&results="+limit;
    }
}
