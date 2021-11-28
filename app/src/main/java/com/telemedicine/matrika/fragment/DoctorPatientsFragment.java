package com.telemedicine.matrika.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.adapter.UsersAdapter;
import com.telemedicine.matrika.base.AppBaseActivity;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.model.other.Request;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import java.util.ArrayList;
import java.util.List;

public class DoctorPatientsFragment extends Fragment implements AppBaseActivity.OnUserRequestListener {

    private Activity        activity;
    private View            rootView;
    private UsersAdapter    patients_Adapter;
    private LoadingFragment loading;

    public DoctorPatientsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_vertical_list, container, false);

        init();

        return rootView;
    }

    private void init(){
        NavPatientsFragment.empty_Layout.setVisibility(View.GONE);
        patients_Adapter = new UsersAdapter(1);
        ((RecyclerView) rootView.findViewById(R.id.list_Rcv)).setAdapter(patients_Adapter);
        showRequests();
        ((AppBaseActivity) activity).setOnUserRequestListener(this);
    }

    private void showRequests(){
        List<String> allIds = new ArrayList<>();
        List<String> acceptedIds = new ArrayList<>();
        for (Request request : LocalStorage.userRequests) {
            allIds.add(request.getRequestedBy());
            if (request.getAccepted().getStatus()) acceptedIds.add(request.getRequestedBy());
        }

        /** All Accepted Patients **/
        List<User> acceptedUsers = new ArrayList<>();
        if (!acceptedIds.isEmpty()) {
            loading = LoadingFragment.show();
            FirebaseFirestore.getInstance().collection(FirebaseHelper.USERS_TABLE).whereIn(User.ID, acceptedIds).get()
                    .addOnSuccessListener(userSnapshots -> {
                        AppExtensions.dismissLoading(loading);
                        NavPatientsFragment.refresh_Layout.setRefreshing(false);
                        for (QueryDocumentSnapshot snapshot : userSnapshots) {
                            User user = snapshot.toObject(User.class);
                            user.setRequestId(LocalStorage.userRequests.get(allIds.indexOf(user.getId())).getId());
                            acceptedUsers.add(user);
                        }
                        patients_Adapter.setUsers(acceptedUsers);
                        NavPatientsFragment.empty_Layout.setVisibility(acceptedUsers.isEmpty() ? View.VISIBLE : View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        AppExtensions.dismissLoading(loading);
                        NavPatientsFragment.refresh_Layout.setRefreshing(false);
                        NavPatientsFragment.empty_Layout.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    });
        }
        else {
            NavPatientsFragment.empty_Layout.setVisibility(View.VISIBLE);
            patients_Adapter.setUsers(acceptedUsers);
            NavPatientsFragment.refresh_Layout.setRefreshing(false);
        }
    }

    @Override
    public void onRequest(List<Request> requests) {
        showRequests();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }
}