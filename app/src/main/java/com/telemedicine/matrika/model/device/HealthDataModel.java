package com.telemedicine.matrika.model.device;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.telemedicine.matrika.api.API;

public class HealthDataModel extends AndroidViewModel {

    @Nullable
    private JsonLiveData data;

    public MutableLiveData getRefresh() {
        return refresh;
    }

    private final MutableLiveData<Integer> refresh = new MutableLiveData<>();

    public HealthDataModel(@NonNull Application application) {
        super(application);
        data = new JsonLiveData(application);
    }

    public MutableLiveData<HealthData> getHealthData() {
        return data;
    }

    public void RefreshData(){
        refresh.setValue(0);
        data = new JsonLiveData(this.getApplication());
    }

    public class JsonLiveData extends MutableLiveData<HealthData> {
        private final Context context;

        public JsonLiveData(Context context){
            this.context=context;
            LoadData();
        }

        private void LoadData() {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, API.getFeedURL(2), response -> {
                HealthData healthData = new GsonBuilder().create().fromJson(response, HealthData.class);
                if (healthData == null) return;
                if (healthData.getCode() == 404) return;
                postValue(healthData);
                setValue(healthData);
                refresh.postValue(1);
            }, Throwable::printStackTrace);

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            );

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }
    }
}
