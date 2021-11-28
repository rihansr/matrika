package com.telemedicine.matrika.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.telemedicine.matrika.activity.DoctorHomeActivity;
import com.telemedicine.matrika.activity.PatientHomeActivity;
import com.telemedicine.matrika.activity.SignInActivity;
import com.telemedicine.matrika.activity.SignUpActivity;
import com.telemedicine.matrika.util.Constants;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *  Continuously Monitor Internet Connection
 **/
public class NetworkStatusChangeReceiver extends BroadcastReceiver {

    private Context mContext;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        startCheckingInternet();
    }

    private void sendInternetInfo(Boolean isConnected) {
        Constants.IS_INTERNET_CONNECTED = isConnected;

        if (mContext.getClass() == SignInActivity.class) {
            ((SignInActivity) mContext).updateInternetConnectionStatus(isConnected);
        }
        else if (mContext.getClass() == SignUpActivity.class) {
            ((SignUpActivity) mContext).updateInternetConnectionStatus(isConnected);
        }
        else if (mContext.getClass() == PatientHomeActivity.class) {
            ((PatientHomeActivity) mContext).updateInternetConnectionStatus(isConnected);
        }
        else if (mContext.getClass() == DoctorHomeActivity.class) {
            ((DoctorHomeActivity) mContext).updateInternetConnectionStatus(isConnected);
        }
    }

    private void startCheckingInternet() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            /**
             * If ConnectivityManager says connected,
             * then check really any active connection of not!
             */
            new InternetCheck().execute();
        }
        else {
            /**
             * ConnectivityManager says Not connected,
             * So Not connected, no further question.. :)
             */
            sendInternetInfo(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class InternetCheck extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress("www.google.com", 80), 1500);
                sock.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            sendInternetInfo(isConnected);
        }
    }
}
