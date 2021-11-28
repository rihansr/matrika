package com.telemedicine.matrika.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.adapter.UsersAdapter;
import com.telemedicine.matrika.base.AppBaseActivity;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.model.other.Request;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.extensions.AppExtensions;

import java.util.ArrayList;
import java.util.List;

public class PatientDoctorsFragment extends DialogFragment implements AppBaseActivity.OnUserRequestListener {

    private static final String  TAG = PatientDoctorsFragment.class.getSimpleName();
    private View                 rootView;
    private Activity             activity;

    /**
     * Toolbar
     **/
    private AppCompatImageView   toolbar_Back_Button;
    private AppCompatTextView    toolbar_title;
    private AppCompatImageView   toolbar_Right_Button;

    /**
     * Content
     **/
    private RecyclerView         rcv_Doctors;
    private UsersAdapter         doctors_Adapter;

    /**
     * Empty Layout
     **/
    private LinearLayoutCompat   empty_Layout;
    private LottieAnimationView  empty_Icon;
    private AppCompatTextView    empty_Title;
    private AppCompatTextView    empty_Subtitle;

    private LoadingFragment      loading;

    public static PatientDoctorsFragment show(){
        PatientDoctorsFragment fragment = new PatientDoctorsFragment();
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
        rootView = inflater.inflate(R.layout.fragment_layout_doctors, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        AppExtensions.fullScreenDialog(getDialog(), false);
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        initId();

        toolbar_title.setText(AppExtensions.getString(R.string.doctors));
        toolbar_Back_Button.setOnClickListener(v -> dismiss());
        toolbar_Right_Button.setVisibility(View.GONE);

        empty_Icon.setAnimation("lottie/blank_users.json");
        empty_Title.setText(AppExtensions.getString(R.string.emptyDoctorsTitle));
        empty_Subtitle.setText(AppExtensions.getString(R.string.emptyDoctorsSubtitle));

        setDoctorsAdapter();

        showRequests();

        ((AppBaseActivity) activity).setOnUserRequestListener(this);
    }

    private void initId() {
        toolbar_title = rootView.findViewById(R.id.toolbar_Title_Tv);
        toolbar_Back_Button = rootView.findViewById(R.id.toolbar_Left_Button);
        toolbar_Right_Button = rootView.findViewById(R.id.toolbar_Right_Button);

        empty_Layout = rootView.findViewById(R.id.doctors_Empty_Layout);
        empty_Icon = rootView.findViewById(R.id.empty_Icon_Animation);
        empty_Title = rootView.findViewById(R.id.empty_Title_Tv);
        empty_Subtitle = rootView.findViewById(R.id.empty_Subtitle_Tv);

        rcv_Doctors = rootView.findViewById(R.id.doctors_Doctors_Rcv);
    }

    private void setDoctorsAdapter() {
        doctors_Adapter = new UsersAdapter(1);
        rcv_Doctors.setAdapter(doctors_Adapter);
    }

    private void showRequests(){
        List<String> allIds = new ArrayList<>();
        List<String> acceptedIds = new ArrayList<>();
        for (Request request : LocalStorage.sentRequests) {
            allIds.add(request.getRequestedTo());
            if (request.getAccepted().getStatus()) acceptedIds.add(request.getRequestedTo());
        }

        /** All Accepted Doctors **/
        List<User> acceptedUsers = new ArrayList<>();
        if (!acceptedIds.isEmpty()) {
            loading = LoadingFragment.show();
            FirebaseFirestore.getInstance().collection(FirebaseHelper.USERS_TABLE).whereIn(User.ID, acceptedIds).get()
                    .addOnSuccessListener(userSnapshots -> {
                        AppExtensions.dismissLoading(loading);
                        for (QueryDocumentSnapshot snapshot : userSnapshots) {
                            User user = snapshot.toObject(User.class);
                            user.setRequestId(LocalStorage.sentRequests.get(allIds.indexOf(user.getId())).getId());
                            acceptedUsers.add(user);
                        }
                        doctors_Adapter.setUsers(acceptedUsers);
                        empty_Layout.setVisibility(acceptedUsers.isEmpty() ? View.VISIBLE : View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        AppExtensions.dismissLoading(loading);
                        empty_Layout.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    });
        }
        else {
            empty_Layout.setVisibility(View.VISIBLE);
            doctors_Adapter.setUsers(acceptedUsers);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
