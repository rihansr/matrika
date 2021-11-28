package com.telemedicine.matrika.util;

import android.app.ProgressDialog;

import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;

import static com.telemedicine.matrika.util.extensions.AppExtensions.getString;

public class CustomProgressDialog {

    private ProgressDialog  progressDialog;

    public CustomProgressDialog() {}

    public void show(Object message){
        init(null, message, true);
    }

    public void show(Object title, Object message){
        init(title, message, true);
    }

    public void show(Object message, Boolean isCancelable){
        init(null, message, isCancelable == null || isCancelable);
    }

    public void show(Object title, Object message, Boolean isCancelable){
       init(title, message, isCancelable == null || isCancelable);
    }

    private void init(Object title, Object message, boolean isCancelable){
        if(message == null) return;
        progressDialog = new ProgressDialog(AppController.getActivity(), R.style.ProgressDialog);
        if(title != null) progressDialog.setTitle(title instanceof String ? (String)title : getString((Integer)title));
        progressDialog.setMessage(message instanceof String ? (String)message : getString((Integer)message));
        progressDialog.setCancelable(isCancelable);
        progressDialog.show();
    }

    public void dismiss(){
        if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }
}
