package com.telemedicine.matrika.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.model.device.Feed;
import com.telemedicine.matrika.model.device.HealthData;
import com.telemedicine.matrika.model.device.HealthDataModel;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import java.util.Collections;
import java.util.Locale;

public class DeviceDataFragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String     TAG = DeviceDataFragment.class.getSimpleName();
    private Activity                activity;

    /**
     * Toolbar
     **/
    private AppCompatImageView      toolbar_Back_Button;
    private AppCompatTextView       toolbar_title;
    private AppCompatImageView      toolbar_Right_Button;

    /**
     * Content
     **/
    private SwipeRefreshLayout      refreshLayout;
    private HealthDataModel         dataModel;
    private AppCompatTextView       pulseValue;
    private AppCompatTextView       lastPulseValue;
    private AppCompatTextView       pulseStatus;
    private AppCompatTextView       oxygenValue;
    private AppCompatTextView       lastOxygenValue;
    private AppCompatTextView       oxygenStatus;
    private AppCompatTextView       ecgValue;
    private AppCompatTextView       lastEcgValue;
    private AppCompatTextView       ecgStatus;
    private AppCompatTextView       temperatureValue;
    private AppCompatTextView       lastTemperatureValue;
    private AppCompatTextView       temperatureStatus;

    /**
     * Other
     **/
    private final Handler           mHandler = new Handler();
    private Runnable                mRunnable;
    private LoadingFragment         loading;

    public DeviceDataFragment() {
    }

    public static DeviceDataFragment show(){
        DeviceDataFragment fragment = new DeviceDataFragment();
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullDark_FadeAnimation);
        setRetainInstance(true);
        setCancelable(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout_device_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        AppExtensions.fullScreenDialog(getDialog(), false);
        super.onViewCreated(view, savedInstanceState);

        idSetup(view);

        init();
    }

    private void idSetup(View rootView) {
        toolbar_title = rootView.findViewById(R.id.toolbar_Title_Tv);
        toolbar_Back_Button = rootView.findViewById(R.id.toolbar_Left_Button);
        toolbar_Right_Button = rootView.findViewById(R.id.toolbar_Right_Button);

        refreshLayout = rootView.findViewById(R.id.refresh_Layout);
        refreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent
        );

        dataModel = new HealthDataModel(requireActivity().getApplication());

        pulseValue = rootView.findViewById(R.id.pulseRate_Value);
        lastPulseValue = rootView.findViewById(R.id.pulseRate_Last);
        pulseStatus = rootView.findViewById(R.id.pulseRate_Status);

        oxygenValue = rootView.findViewById(R.id.oxygenSaturation_Value);
        lastOxygenValue = rootView.findViewById(R.id.oxygenSaturation_Last);
        oxygenStatus = rootView.findViewById(R.id.oxygenSaturation_Status);

        ecgValue = rootView.findViewById(R.id.ecg_Value);
        lastEcgValue = rootView.findViewById(R.id.ecg_Last);
        ecgStatus = rootView.findViewById(R.id.ecg_Status);

        temperatureValue = rootView.findViewById(R.id.bodyTemperature_Value);
        lastTemperatureValue = rootView.findViewById(R.id.bodyTemperature_Last);
        temperatureStatus = rootView.findViewById(R.id.bodyTemperature_Status);
    }

    private void init(){
        toolbar_title.setText(AppExtensions.getString(R.string.deviceData));
        toolbar_Back_Button.setOnClickListener(v -> dismiss());
        toolbar_Right_Button.setVisibility(View.GONE);

        refreshLayout.setOnRefreshListener(this);

        getFeedData();
    }

    /**
     * Get data from https://thingspeak.com/
     **/
    private void getFeedData() {
        loading = LoadingFragment.show();
        dataModel.getRefresh().observe((LifecycleOwner) activity, o ->
                dataModel.getHealthData().observe((LifecycleOwner) activity, newHealthData -> {
                    refreshLayout.setRefreshing(false);
                    AppExtensions.dismissLoading(loading);
                    if (newHealthData == null || newHealthData.getFeeds().size() == 0) return;
                    Collections.reverse(newHealthData.getFeeds());
                    dataSetup(newHealthData);
                }));

        reloadData();
    }

    private void dataSetup(HealthData healthData){
        Feed lastFeed = healthData.getFeeds().size() == 2 && healthData.getFeeds().get(1) != null ? healthData.getFeeds().get(1) : null;

        Feed feed = healthData.getFeeds().get(0);
        if(feed == null) return;

        /**
         * Pulse Rate
         **/
        String getLastPR = AppExtensions.formatValue(lastFeed == null ? null : lastFeed.getPulse(), null);
        if(getLastPR != null){
            lastPulseValue.setVisibility(View.VISIBLE);
            lastPulseValue.setText(String.format(Locale.getDefault(), "%s %s", getLastPR, AppExtensions.getString(R.string.bpm)));
        }
        else lastPulseValue.setVisibility(View.GONE);

        pulseValue.setText(AppExtensions.formatValue(feed.getPulse(), AppExtensions.getString(R.string.nullSymbol)));
        String getCurPR = AppExtensions.formatValue(feed.getPulse(), null);
        if(getCurPR != null) {
            double pulse = Double.parseDouble(getCurPR);

            if(pulse < Constants.PULSE_MIN_VALUE){
                pulseStatus.setText(AppExtensions.getString(R.string.low));
            }
            else if(pulse > Constants.PULSE_MAX_VALUE){
                pulseStatus.setText(AppExtensions.getString(R.string.high));
            }
            else {
                pulseStatus.setText(AppExtensions.getString(R.string.normal));
            }
        }

        /**
         * Oxygen Saturation
         **/
        String getLastSpO2 = AppExtensions.formatValue(lastFeed == null ? null : lastFeed.getOxygen(), null);
        if(getLastSpO2 != null){
            lastOxygenValue.setVisibility(View.VISIBLE);
            lastOxygenValue.setText(String.format(Locale.getDefault(), "%s %s", getLastSpO2, AppExtensions.getString(R.string.percent)));
        }
        else lastOxygenValue.setVisibility(View.GONE);

        oxygenValue.setText(AppExtensions.formatValue(feed.getOxygen(), AppExtensions.getString(R.string.nullSymbol)));
        String getCurSpO2 = AppExtensions.formatValue(feed.getOxygen(), null);
        if(getCurSpO2 != null) {
            double oxygen = Double.parseDouble(getCurSpO2);

            if(oxygen < Constants.SPO2_NORMAL_VALUE){
                oxygenStatus.setText(AppExtensions.getString(R.string.low));
            }
            else {
                oxygenStatus.setText(AppExtensions.getString(R.string.normal));
            }
        }

        /**
         * ECG
         **/
        String getLastEcg = AppExtensions.formatValue(lastFeed == null ? null : lastFeed.getEcg(), null);
        if(getLastEcg != null){
            lastEcgValue.setVisibility(View.VISIBLE);
            lastEcgValue.setText(String.format(Locale.getDefault(), "%s %s", getLastEcg, AppExtensions.getString(R.string.millisecondSymbol)));
        }
        else lastEcgValue.setVisibility(View.GONE);

        ecgValue.setText(AppExtensions.formatValue(feed.getEcg(), AppExtensions.getString(R.string.nullSymbol)));
        String getCurEcg = AppExtensions.formatValue(feed.getEcg(), null);

        if(getCurEcg != null){
            double ecg = Double.parseDouble(getCurEcg);

            if(ecg < Constants.ECG_MIN_VALUE){
                ecgStatus.setText(AppExtensions.getString(R.string.low));
            }
            else if(ecg > Constants.ECG_MAX_VALUE){
                ecgStatus.setText(AppExtensions.getString(R.string.high));
            }
            else {
                ecgStatus.setText(AppExtensions.getString(R.string.normal));
            }
        }

        /**
         * Body Temperature
         **/
        String getLastTemp = AppExtensions.formatValue(lastFeed == null ? null : lastFeed.getTemperature(), null);
        if(getLastTemp != null){
            lastTemperatureValue.setVisibility(View.VISIBLE);
            lastTemperatureValue.setText(String.format(Locale.getDefault(), "%s %s", getLastTemp, AppExtensions.getString(R.string.degreeFerSymbol)));
        }
        else lastTemperatureValue.setVisibility(View.GONE);

        temperatureValue.setText(AppExtensions.formatValue(feed.getTemperature(), AppExtensions.getString(R.string.nullSymbol)));
        String getCurTemp = AppExtensions.formatValue(feed.getTemperature(), null);

        if(getCurTemp != null){
            double temperature = Double.parseDouble(getCurTemp);

            if(temperature < Constants.TEMPERATURE_MIN_VALUE){
                temperatureStatus.setText(AppExtensions.getString(R.string.low));
            }
            else if(temperature > Constants.TEMPERATURE_MAX_VALUE){
                temperatureStatus.setText(AppExtensions.getString(R.string.high));
            }
            else {
                temperatureStatus.setText(AppExtensions.getString(R.string.normal));
            }
        }
    }

    /**
     * Reload data every 10 Seconds
     **/
    private void reloadData(){
        mRunnable = new Runnable() {
            @Override
            public void run() {
                dataModel.RefreshData();
                mHandler.postDelayed(this, Constants.DATA_RELOAD_DELAY);
            }
        };

        mHandler.postDelayed(mRunnable, Constants.DATA_RELOAD_DELAY);
    }

    /**
     * Swipe to reload data
     **/
    @Override
    public void onRefresh() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, Constants.DATA_RELOAD_DELAY);
        dataModel.RefreshData();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
