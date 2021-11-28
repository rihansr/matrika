package com.telemedicine.matrika.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.activity.SignInActivity;
import com.telemedicine.matrika.activity.SignUpActivity;
import com.telemedicine.matrika.activity.SplashActivity;
import com.telemedicine.matrika.fragment.AlertDialogFragment;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.helper.PreferenceManager;
import com.telemedicine.matrika.model.other.Follower;
import com.telemedicine.matrika.model.other.Request;
import com.telemedicine.matrika.model.other.Status;
import com.telemedicine.matrika.model.specialty.Specialty;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.receiver.NetworkStatusChangeReceiver;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.CustomSnackBar;
import com.telemedicine.matrika.util.LocalStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class AppBaseActivity extends AppCompatActivity {

    private ListenerRegistration    profileListenerRegistration;
    private OnUserFollowListener    mOnUserFollowListener;

    private ListenerRegistration    requestsListenerRegistration;
    private OnUserRequestListener   mOnUserRequestListener;

    private ListenerRegistration    followersListenerRegistration;
    private OnUserInfoListener      mOnUserInfoListener;

    private OnSpecialtyListener      mOnSpecialtyListener;

    private boolean                 checkIsProfileVerified = false;
    private final FirebaseHelper    firebaseHelper = new FirebaseHelper();
    private final PreferenceManager pm = new PreferenceManager();
    private final NetworkStatusChangeReceiver networkStatusChangeReceiver = new NetworkStatusChangeReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalStorage.setUserInfo(new User(), false);
        Constants.roleMode = pm.getUserMode();
        switch (Constants.roleMode){
            case PATIENT: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case DOCTOR: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
        }

        AppController.setActivity(AppBaseActivity.this);

        checkUserToken();
        getSpecialties();
        getUserInfo();
        getUserRequests();
        getFollowers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppController.setActivity(AppBaseActivity.this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mTokenReceiver, new IntentFilter(Constants.TOKEN_LISTENER_KEY));
        onlinePreference(true);
    }

    /**
     * Update token to https://console.firebase.google.com/project/matrika-af9b1/firestore/data~2Fusers
     * {@link User} (Table data model)
     **/
    private void onlinePreference(boolean isOnline){
        if(this instanceof SplashActivity || this instanceof SignInActivity || this instanceof SignUpActivity) return;
        if(LocalStorage.USER == null || LocalStorage.USER.getId() == null) return;
        HashMap<String, Object> activeStatus = new HashMap<>();
        activeStatus.put(Status.STATUS, isOnline);
        activeStatus.put(Status.DATE, new Date());

        firebaseHelper.setDocumentData(FirebaseFirestore.getInstance()
                .collection(FirebaseHelper.USERS_TABLE)
                .document(LocalStorage.USER.getId())
                .update(User.ACTIVE, activeStatus), null);
    }


    /**
     * Get data from https://console.firebase.google.com/project/matrika-af9b1/firestore/data~2FSpecialties
     * {@link Specialty} (Table data model)
     **/
    private void getSpecialties() {
        if(this instanceof SplashActivity || this instanceof SignInActivity) return;
        FirebaseFirestore.getInstance().collection(FirebaseHelper.SPECIALTIES_TABLE)
                .orderBy(Specialty.TITLE, Query.Direction.ASCENDING)
                .orderBy(Specialty.SPECIALTY, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    LocalStorage.specialties = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Specialty specialty = snapshot.toObject(Specialty.class);
                        LocalStorage.specialties.add(specialty);
                    }

                    if(mOnSpecialtyListener != null) mOnSpecialtyListener.onSpecialty(LocalStorage.specialties);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.e(Constants.TAG, e.toString()+"");
                });
    }


    /**
     * Get data from https://console.firebase.google.com/project/matrika-af9b1/firestore/data~2Fusers
     * {@link User} (Table data model)
     **/
    private void getUserInfo() {
        if(this instanceof SplashActivity || this instanceof SignInActivity || this instanceof SignUpActivity) return;
        profileListenerRegistration = firebaseHelper.userReference()
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.e(Constants.TAG, "Profile Load Failed: " + error.getMessage());
                        return;
                    }
                    if (snapshot != null) {
                        User user = snapshot.toObject(User.class);
                        if (user == null) return;
                        LocalStorage.setUserInfo(user, true);
                        if (!user.isProfileVerified() && !checkIsProfileVerified) {
                            AlertDialogFragment.show(R.string.profileNotVerifiedTitle, R.string.profileNotVerifiedMessage, R.string.cancel, R.string.okay);
                            checkIsProfileVerified = true;
                        }
                        if (mOnUserInfoListener != null) mOnUserInfoListener.onUser(user);
                    }
                    else signOut();
                });
    }


    /**
     * Check current Device Token
     * Token for sending push notifications
     **/
    private void checkUserToken(){
        if(this instanceof SplashActivity || this instanceof SignInActivity || this instanceof SignUpActivity) return;
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.i(Constants.TAG, "Token Task Failed: ", task.getException());
                return;
            }

            String currentToken = Objects.requireNonNull(task.getResult());
            updateToken(currentToken);
        });
    }


    /**
     * Update Token (if changed) https://console.firebase.google.com/project/matrika-af9b1/firestore/data~2Fusers
     * {@link User} (Table data model)
     **/
    private void updateToken(String newToken){
        if(this instanceof SplashActivity || this instanceof SignInActivity || this instanceof SignUpActivity) return;
        if(newToken == null) return;
        if (LocalStorage.USER == null) return;
        if (LocalStorage.USER.getToken() == null || !LocalStorage.USER.getToken().equals(newToken)) {
            Map<String, Object> token = new HashMap<>();
            token.put(User.TOKEN, newToken);

            firebaseHelper.setDocumentData(firebaseHelper.userReference().update(token),
                    new FirebaseHelper.OnFirebaseUpdateListener() {
                        @Override
                        public void onSuccess() {
                            LocalStorage.USER.setToken(newToken);
                            LocalStorage.setUserInfo(LocalStorage.USER, true);
                        }

                        @Override
                        public void onFailure() {}

                        @Override
                        public void onCancelled() {}
                    });
        }
    }


    /**
     * Get data from https://console.firebase.google.com/project/matrika-af9b1/firestore/data~2Frequests
     * {@link Request} (Table data model)
     **/
    private void getUserRequests(){
        if(this instanceof SplashActivity || this instanceof SignInActivity || this instanceof SignUpActivity) return;
        if(LocalStorage.USER == null || LocalStorage.USER.getId() == null) return;
        EventListener<QuerySnapshot> eventListener = (requestSnapshot, error) -> {

            /** Error Checking **/
            if (error != null) {
                Log.e(Constants.TAG, "Request Error, Reason: " + error.getMessage(), error);
                return;
            }
            else if (requestSnapshot == null || requestSnapshot.isEmpty()) {
                Log.e(Constants.TAG, "No Requests");
                LocalStorage.allRequests.clear();
                LocalStorage.userRequests.clear(); LocalStorage.userRequestIds.clear();
                LocalStorage.sentRequests.clear(); LocalStorage.sentRequestIds.clear();
                if(mOnUserRequestListener != null) mOnUserRequestListener.onRequest(LocalStorage.allRequests);
                return;
            }

            /** Get All Requests **/
            LocalStorage.allRequests = new ArrayList<>();
            LocalStorage.userRequests = new ArrayList<>();
            LocalStorage.userRequestIds = new ArrayList<>();
            LocalStorage.sentRequests = new ArrayList<>();
            LocalStorage.sentRequestIds = new ArrayList<>();

            for (QueryDocumentSnapshot snapshot : requestSnapshot) {
                Request request = snapshot.toObject(Request.class);
                if(request.getRequestedTo().equals(LocalStorage.USER.getId()) || request.getRequestedBy().equals(LocalStorage.USER.getId())) LocalStorage.allRequests.add(request);
                if(request.getRequestedTo().equals(LocalStorage.USER.getId())) LocalStorage.userRequests.add(request);
                if(request.getRequestedTo().equals(LocalStorage.USER.getId())) LocalStorage.userRequestIds.add(request.getRequestedBy());
                if(request.getRequestedBy().equals(LocalStorage.USER.getId())) LocalStorage.sentRequests.add(request);
                if(request.getRequestedBy().equals(LocalStorage.USER.getId())) LocalStorage.sentRequestIds.add(request.getRequestedTo());
            }

            if(mOnUserRequestListener != null) mOnUserRequestListener.onRequest(LocalStorage.allRequests);
        };

        Query requestQuery = FirebaseFirestore.getInstance().collection(FirebaseHelper.USER_REQUESTS_TABLE)
                .whereEqualTo(Request.REJECTED_STATUS, false);

        requestsListenerRegistration = requestQuery.addSnapshotListener(eventListener);
    }


    /**
     * Get data from https://console.firebase.google.com/project/matrika-af9b1/firestore/data~2Ffollowers
     * {@link Follower} (Table data model)
     **/
    private void getFollowers(){
        if(this instanceof SplashActivity || this instanceof SignInActivity || this instanceof SignUpActivity) return;
        if(LocalStorage.USER == null || LocalStorage.USER.getId() == null) return;
        EventListener<QuerySnapshot> followerEventListener = (requestSnapshot, error) -> {

            /** Error Checking **/
            if (error != null) {
                Log.e(Constants.TAG, "Follower Error, Reason: " + error.getMessage(), error);
                return;
            }
            else if (requestSnapshot == null || requestSnapshot.isEmpty()) {
                Log.e(Constants.TAG, "No Followers");
                LocalStorage.allFollows.clear();
                LocalStorage.followers.clear(); LocalStorage.followerIds.clear();
                LocalStorage.followings.clear();  LocalStorage.followingIds.clear();
                if(mOnUserFollowListener != null) mOnUserFollowListener.onFollow(LocalStorage.allFollows);
                return;
            }

            /** Get All Followers **/
            LocalStorage.allFollows = new ArrayList<>();
            LocalStorage.followers = new ArrayList<>();
            LocalStorage.followerIds = new ArrayList<>();
            LocalStorage.followings = new ArrayList<>();
            LocalStorage.followingIds = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : requestSnapshot) {
                Follower follower = snapshot.toObject(Follower.class);
                if(follower.getFollowedTo().equals(LocalStorage.USER.getId()) || follower.getFollowedBy().equals(LocalStorage.USER.getId())) LocalStorage.allFollows.add(follower);
                if(follower.getFollowedTo().equals(LocalStorage.USER.getId())) LocalStorage.followers.add(follower);
                if(follower.getFollowedTo().equals(LocalStorage.USER.getId())) LocalStorage.followerIds.add(follower.getFollowedBy());
                if(follower.getFollowedBy().equals(LocalStorage.USER.getId())) LocalStorage.followings.add(follower);
                if(follower.getFollowedBy().equals(LocalStorage.USER.getId())) LocalStorage.followingIds.add(follower.getFollowedTo());
            }

            if(mOnUserFollowListener != null) mOnUserFollowListener.onFollow(LocalStorage.allFollows);
        };

        followersListenerRegistration = FirebaseFirestore.getInstance().collection(FirebaseHelper.FOLLOWERS_TABLE)
                .addSnapshotListener(followerEventListener);
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        LocalStorage.setUserInfo(null, true);

        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkStatusChangeReceiver, new IntentFilter(CONNECTIVITY_ACTION));
    }

    private final BroadcastReceiver mTokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String token = intent.getStringExtra(Constants.TOKEN_INTENT_KEY);
            updateToken(token);
            intent.removeExtra(Constants.TOKEN_INTENT_KEY);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStatusChangeReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onlinePreference(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTokenReceiver);
        if(profileListenerRegistration != null) profileListenerRegistration.remove();
        if(requestsListenerRegistration != null) requestsListenerRegistration.remove();
        if(followersListenerRegistration != null) followersListenerRegistration.remove();
    }

    public void setOnUserRequestListener(OnUserRequestListener mOnUserRequestListener) {
        this.mOnUserRequestListener = mOnUserRequestListener;
    }

    public interface OnUserRequestListener {
        void onRequest(List<Request> requests);
    }

    public void setOnUserFollowListener(OnUserFollowListener mOnUserFollowListener) {
        this.mOnUserFollowListener = mOnUserFollowListener;
    }

    public interface OnUserFollowListener {
        void onFollow(List<Follower> followers);
    }

    public void setOnUserInfoListener(OnUserInfoListener mOnUserInfoListener) {
        this.mOnUserInfoListener = mOnUserInfoListener;
    }

    public interface OnUserInfoListener {
        void onUser(User info);
    }

    public void setOnSpecialtyListener(OnSpecialtyListener mOnSpecialtyListener) {
        this.mOnSpecialtyListener = mOnSpecialtyListener;
    }

    public interface OnSpecialtyListener {
        void onSpecialty(List<Specialty> specialties);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * {@link NetworkStatusChangeReceiver} Monitor internet connection
     **/
    public void updateInternetConnectionStatus(boolean isConnected) {
        CustomSnackBar customSnackBar = new CustomSnackBar(R.string.network_Error, R.string.retry, CustomSnackBar.Duration.INDEFINITE);

        if (isConnected) {
            customSnackBar.dismiss();
        }
        else {
            customSnackBar.show();
            customSnackBar.setOnDismissListener(snackBar -> {
                networkStatusChangeReceiver.onReceive(AppController.getActivity(), null);
                snackBar.dismiss();
            });
        }
    }
}
