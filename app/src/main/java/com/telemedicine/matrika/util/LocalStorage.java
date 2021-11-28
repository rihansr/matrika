package com.telemedicine.matrika.util;

import com.hootsuite.nachos.chip.Chip;
import com.telemedicine.matrika.helper.PreferenceManager;
import com.telemedicine.matrika.model.other.Follower;
import com.telemedicine.matrika.model.other.Request;
import com.telemedicine.matrika.model.specialty.Specialty;
import com.telemedicine.matrika.model.user.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LocalStorage {

    public static User USER = new User();
    public static List<Specialty> specialties = new ArrayList<>();

    public static List<Follower> allFollows = new ArrayList<>();
    public static List<Follower> followers = new ArrayList<>();
    public static List<Follower> followings = new ArrayList<>();
    public static List<String> followerIds = new ArrayList<>();
    public static List<String> followingIds = new ArrayList<>();

    public static List<Request> allRequests = new ArrayList<>();
    public static List<Request> userRequests = new ArrayList<>();
    public static List<Request> sentRequests = new ArrayList<>();
    public static List<String> sentRequestIds = new ArrayList<>();
    public static List<String> userRequestIds = new ArrayList<>();

    public static void setUserInfo(User userInfo, boolean storeIt){
        PreferenceManager sp = new PreferenceManager();
        String key = PreferenceManager.USER_INFO_SP_KEY;
        if (storeIt) {
            USER = userInfo;
            sp.setSignInData(key, userInfo == null ? null : new Gson().toJson(userInfo));
        }
        else {
            String storedUserInfo = sp.getSignInData(key);
            USER = (storedUserInfo == null) ? new User() : new Gson().fromJson(storedUserInfo, User.class);
        }
    }

    public static CharSequence[] getFeesLabels(int min, int max, int divider){
        CharSequence[] feesLabels = new CharSequence[(int) Math.ceil(divider) + 1];
        for (int pos=0, fees=min; fees<=max; pos++, fees+=divider){
            feesLabels[pos] = String.valueOf(fees);
        }
        return feesLabels;
    }

    public static List<String> getChips(List<Chip> chips){
        if(chips == null || chips.size() == 0) return null;
        List<String> strings = new ArrayList<>();
        for (Chip chip : chips) strings.add(chip.getText().toString());
        return strings;
    }
}
