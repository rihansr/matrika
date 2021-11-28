package com.telemedicine.matrika.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.GsonBuilder;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.api.API;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.model.device.HealthData;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.util.CustomSnackBar;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.extensions.AppExtensions;

import java.util.Objects;

@SuppressLint("SetTextI18n")
public class DeviceInfoFragment extends DialogFragment {

    private static final String     TAG = DeviceInfoFragment.class.getSimpleName();
    private Activity                activity;
    private AppCompatEditText       id_Input;
    private AppCompatEditText       key_Input;
    private AppCompatButton         confirm_Button;
    private AppCompatImageView      back_Button;
    private LoadingFragment         loading;
    private OnDeviceInfoListener    mOnDeviceInfoListener;

    public static DeviceInfoFragment show(){
        DeviceInfoFragment fragment = new DeviceInfoFragment();
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setCancelable(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        AppExtensions.halfScreenDialog(getDialog());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout_device_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initId(view);

        init();
    }

    private void initId(View view) {
        id_Input = view.findViewById(R.id.device_ChanelId_Input);
        id_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());
        key_Input = view.findViewById(R.id.device_Key_Input);
        confirm_Button = view.findViewById(R.id.device_Confirm_Button);
        back_Button = view.findViewById(R.id.device_Back_Button);
    }

    private void init() {
        back_Button.setOnClickListener(view -> dismiss());

        confirm_Button.setOnClickListener(view -> {
            String id = Objects.requireNonNull(id_Input.getText()).toString().trim();
            String key = Objects.requireNonNull(key_Input.getText()).toString().trim();

            if (!AppExtensions.isInputValid(id_Input, R.string.channelId_Error)
                    || !AppExtensions.isInputValid(id_Input, id.length() < 7, R.string.validChannelId_Error)
                    || !AppExtensions.isInputValid(key_Input, R.string.key_Error)
                    || !AppExtensions.isInputValid(key_Input, key.length() < 16, R.string.validKey_Error)
            ) return;

            isDataExist(id, key);
        });
    }

    private void isDataExist(String id, String key){
        loading = LoadingFragment.show();
        API.CHANNEL_ID = id;
        API.READ_API_KEY = key;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.getFeedURL(1), response -> {
            HealthData healthData = new GsonBuilder().create().fromJson(response, HealthData.class);

            if(healthData.getCode() == 404){
                AppExtensions.dismissLoading(loading);
                new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.dataNotExist, R.string.okay, CustomSnackBar.Duration.SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference deviceReference = db.collection(FirebaseHelper.USERS_TABLE).document(LocalStorage.USER.getId());
            new FirebaseHelper().setDocumentData(deviceReference.update(User.CHANNEL_ID, id, User.DEVICE_KEY, key),
                    new FirebaseHelper.OnFirebaseUpdateListener() {
                        @Override
                        public void onSuccess() {
                            LocalStorage.USER.setChannelId(id);
                            LocalStorage.USER.setDeviceKey(key);
                            LocalStorage.setUserInfo(LocalStorage.USER, true);
                            AppExtensions.dismissLoading(loading);
                            if(mOnDeviceInfoListener != null) mOnDeviceInfoListener.onInfo(id, key.toUpperCase());
                            dismiss();
                        }

                        @Override
                        public void onFailure() {
                            AppExtensions.dismissLoading(loading);
                            new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.failureMessage, R.string.okay, CustomSnackBar.Duration.SHORT).show();
                        }

                        @Override
                        public void onCancelled() { AppExtensions.dismissLoading(loading); }
                    });

        }, error -> {
            new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.dataNotExist, R.string.okay, CustomSnackBar.Duration.SHORT).show();
            AppExtensions.dismissLoading(loading);
            error.printStackTrace();
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

    public void setOnDeviceInfoListener(OnDeviceInfoListener mOnDeviceInfoListener) {
        this.mOnDeviceInfoListener = mOnDeviceInfoListener;
    }

    public interface OnDeviceInfoListener {
        void onInfo(String id, String key);
    }

    /**
     *  Context Bind
     **/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    /**
     *  Hide soft keyboard when click outside
     **/
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(activity, getTheme()) {
            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View v = getCurrentFocus();
                    if (v instanceof AppCompatEditText) {
                        Rect outRect = new Rect();
                        v.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            v.clearFocus();
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null)
                                imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
                        }
                    }
                }
                return super.dispatchTouchEvent(event);
            }
        };
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
