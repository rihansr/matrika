package com.telemedicine.matrika.helper;

import android.util.Log;
import androidx.annotation.NonNull;
import com.telemedicine.matrika.BuildConfig;
import com.telemedicine.matrika.model.notification.FCMResponse;
import com.telemedicine.matrika.model.notification.Sender;
import com.telemedicine.matrika.util.Constants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class NotificationManager {

    private final Sender    sender;
    public static String    TITLE_KEY = "mTitle";
    public static String    MESSAGE_KEY = "mMessage";
    public static String    DATA_KEY = "mData";

    public NotificationManager(String token, String title, String message, String extraData) {
        sender = new Sender(token, getData(title, message, extraData));
    }

    public NotificationManager(List<String> tokens, String title, String message, String extraData) {
        if(tokens.size() == 1) sender = new Sender(tokens.get(0), getData(title, message, extraData));
        else sender = new Sender(tokens, getData(title, message, extraData));
    }

    public NotificationManager(String token, String title, String message) {
        sender = new Sender(token, getData(title, message, null));
    }

    public NotificationManager(List<String> tokens, String title, String message) {
        if(tokens.size() == 1) sender = new Sender(tokens.get(0), getData(title, message, null));
        else sender = new Sender(tokens, getData(title, message, null));
    }

    private Map<String, String> getData(String title, String message, String extraData){
        Map<String, String> data = new HashMap<>();
        data.put(TITLE_KEY, title);
        data.put(MESSAGE_KEY, message);
        if(extraData != null) data.put(DATA_KEY, extraData);
        return data;
    }

    public void send(){
        getFCMClient().sendNotification(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(@NonNull Call<FCMResponse> call, @NonNull Response<FCMResponse> response) {
                if((response.body() != null ? response.body().success : 0) == 1){
                    Log.i(Constants.TAG, "Successfully notification has been sent");
                }
                else {
                    Log.i(Constants.TAG, "Sending notification from sender failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<FCMResponse> call, @NonNull Throwable error) {
                Log.i(Constants.TAG, "Sending notification failed, Reason: " + error.getMessage());
            }
        });
    }

    public static IFCMService getFCMClient(){
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.FCM_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(IFCMService.class);
    }

    public interface IFCMService {
        @Headers({
                "Content-Type:application/json",
                "Authorization:key=" + BuildConfig.SERVER_KEY
        })
        @POST("fcm/send")
        Call<FCMResponse> sendNotification(@Body Sender body);
    }
}
