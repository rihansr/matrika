package com.telemedicine.matrika.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.tabs.TabLayout;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppBaseActivity;
import com.telemedicine.matrika.model.other.Request;
import com.telemedicine.matrika.util.extensions.AppExtensions;

import java.util.List;

public class NavPatientsFragment extends Fragment implements AppBaseActivity.OnUserRequestListener, SwipeRefreshLayout.OnRefreshListener {

    private View                        rootView;
    private TabLayout                   patients_Tab;
    public static SwipeRefreshLayout    refresh_Layout;
    public static LinearLayoutCompat    empty_Layout;
    private LottieAnimationView         empty_Icon;
    private AppCompatTextView           empty_Title;
    private AppCompatTextView           empty_Subtitle;

    public NavPatientsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_nav_patients, container, false);
        initId();
        init();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initId() {
        patients_Tab = rootView.findViewById(R.id.patients_Tab);
        refresh_Layout = rootView.findViewById(R.id.patients_Refresh_Layout);
        empty_Layout = rootView.findViewById(R.id.patients_Empty_Layout);
        empty_Icon = rootView.findViewById(R.id.empty_Icon_Animation);
        empty_Title = rootView.findViewById(R.id.empty_Title_Tv);
        empty_Subtitle = rootView.findViewById(R.id.empty_Subtitle_Tv);
    }

    private void init() {
        swapFragment(new PatientRequestsFragment());
        empty_Icon.setAnimation("lottie/blank_users.json");
        empty_Title.setText(AppExtensions.getString(R.string.emptyRequestsTitle));
        empty_Subtitle.setText(AppExtensions.getString(R.string.emptyRequestsSubtitle));

        patients_Tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        swapFragment(new PatientRequestsFragment());
                        empty_Title.setText(AppExtensions.getString(R.string.emptyRequestsTitle));
                        empty_Subtitle.setText(AppExtensions.getString(R.string.emptyRequestsSubtitle));
                        break;
                    case 1:
                        swapFragment(new DoctorPatientsFragment());
                        empty_Title.setText(AppExtensions.getString(R.string.emptyPatientsTitle));
                        empty_Subtitle.setText(AppExtensions.getString(R.string.emptyPatientsSubtitle));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        refresh_Layout.setOnRefreshListener(this);
    }

    private void swapFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.patients_Fragment_Container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onRequest(List<Request> requests) {
        init();
    }

    @Override
    public void onRefresh() {
        swapFragment(patients_Tab.getSelectedTabPosition() == 0 ? new PatientRequestsFragment() : new DoctorPatientsFragment());
    }
}