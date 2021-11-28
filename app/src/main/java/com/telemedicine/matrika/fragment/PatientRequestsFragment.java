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
import com.telemedicine.matrika.util.extensions.AppExtensions;

import java.util.ArrayList;
import java.util.List;

public class PatientRequestsFragment extends Fragment implements AppBaseActivity.OnUserRequestListener {

    private Activity        activity;
    private View            rootView;
    private UsersAdapter    requests_Adapter;
    private LoadingFragment loading;

    public PatientRequestsFragment() {}

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
        requests_Adapter = new UsersAdapter(0);
        ((RecyclerView) rootView.findViewById(R.id.list_Rcv)).setAdapter(requests_Adapter);
        showRequests();
        ((AppBaseActivity) activity).setOnUserRequestListener(this);
    }

    private void showRequests(){
        List<String> allIds = new ArrayList<>();
        List<String> requestedIds = new ArrayList<>();
        for (Request request : LocalStorage.userRequests) {
            allIds.add(request.getRequestedBy());
            if (!request.getAccepted().getStatus()) requestedIds.add(request.getRequestedBy());
        }

        /** All Requested Users **/
        List<User> requestedUsers = new ArrayList<>();
        if (!requestedIds.isEmpty()) {
            loading = LoadingFragment.show();
            FirebaseFirestore.getInstance().collection(FirebaseHelper.USERS_TABLE).whereIn(User.ID, requestedIds).get()
                    .addOnSuccessListener(userSnapshots -> {
                        AppExtensions.dismissLoading(loading);
                        NavPatientsFragment.refresh_Layout.setRefreshing(false);
                        for (QueryDocumentSnapshot snapshot : userSnapshots) {
                            User user = snapshot.toObject(User.class);
                            user.setRequestId(LocalStorage.userRequests.get(allIds.indexOf(user.getId())).getId());
                            requestedUsers.add(user);
                        }
                        requests_Adapter.setUsers(requestedUsers);
                        NavPatientsFragment.empty_Layout.setVisibility(requestedUsers.isEmpty() ? View.VISIBLE : View.GONE);
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
            requests_Adapter.setUsers(requestedUsers);
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