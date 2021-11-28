package com.telemedicine.matrika.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.adapter.UsersAdapter;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.model.specialty.Specialty;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.enums.DrawablePosition;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.telemedicine.matrika.wiget.ClickableEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AllUsersFragment extends DialogFragment {

    private static final String     TAG = AllUsersFragment.class.getSimpleName();
    private Activity                activity;
    private View                    rootView;

    /**
     * Toolbar
     **/
    private AppCompatImageView      toolbar_Back_Button;
    private AppCompatTextView       toolbar_title;
    private AppCompatImageView      toolbar_Right_Button;

    /**
     * Content
     **/
    private ClickableEditText       searchUsers_Input;
    private RecyclerView            rcv_Users;
    private UsersAdapter            usersAdapter;
    private Role                    role;
    private Specialty               specialty;

    /**
     * Empty Layout
     **/
    private LinearLayoutCompat      empty_Layout;
    private LottieAnimationView     empty_Icon;
    private AppCompatTextView       empty_Title;
    private AppCompatTextView       empty_Subtitle;

    private ListenerRegistration    usersListenerRegistration;

    public static AllUsersFragment show(Role role, Specialty specialty){
        AllUsersFragment fragment = new AllUsersFragment();
        fragment.setArguments(buildArguments(role, specialty));
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    public static AllUsersFragment show(Role role){
        AllUsersFragment fragment = new AllUsersFragment();
        fragment.setArguments(buildArguments(role, null));
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    private static Bundle buildArguments(Role role, Specialty specialty){
        Bundle args = new Bundle();
        if(role != null) args.putSerializable(Constants.ROLE_BUNDLE_KEY, role);
        if(specialty != null) args.putSerializable(Constants.SPECIALTY_BUNDLE_KEY, specialty);
        return args;
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
        rootView = inflater.inflate(R.layout.fragment_layout_all_users, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        AppExtensions.fullScreenDialog(getDialog(), false);
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    private void init(){
        if (getArguments() == null || !getArguments().containsKey(Constants.ROLE_BUNDLE_KEY)) { dismiss(); return; }
        role = (Role) getArguments().getSerializable(Constants.ROLE_BUNDLE_KEY);
        getArguments().remove(Constants.ROLE_BUNDLE_KEY);
        if (role == null) { dismiss(); return; }

        initId();

        toolbar_title.setText(AppExtensions.getString(role == Role.PATIENT ? R.string.patients : R.string.doctors));
        toolbar_Back_Button.setOnClickListener(v -> dismiss());
        toolbar_Right_Button.setVisibility(View.GONE);

        if (getArguments() != null && getArguments().containsKey(Constants.SPECIALTY_BUNDLE_KEY)){
            specialty = (Specialty) getArguments().getSerializable(Constants.SPECIALTY_BUNDLE_KEY);
            getArguments().remove(Constants.SPECIALTY_BUNDLE_KEY);

            if(specialty != null) toolbar_title.setText(String.format(Locale.getDefault(), "%s%s", specialty.getTitle(), "'s"));
        }

        empty_Icon.setAnimation("lottie/blank_users.json");
        empty_Title.setText(AppExtensions.getString(role == Role.PATIENT ? R.string.emptyPatientsTitle : R.string.emptyDoctorsTitle));
        empty_Subtitle.setText(AppExtensions.getString(role == Role.PATIENT ? R.string.emptyPatientsSubtitle : R.string.emptyDoctorsSubtitle));

        setUsersAdapter();

        searchUsers_Input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usersAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchUsers_Input.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_input_search,
                        0,
                        editable.length() > 0 ? R.drawable.ic_input_clear : 0,
                        0
                );
            }
        });

        searchUsers_Input.setOnDrawableClickListener(DrawablePosition.RIGHT, () -> searchUsers_Input.setText(null));

        getAllUsers();
    }

    private void initId() {
        toolbar_title = rootView.findViewById(R.id.toolbar_Title_Tv);
        toolbar_Back_Button = rootView.findViewById(R.id.toolbar_Left_Button);
        toolbar_Right_Button = rootView.findViewById(R.id.toolbar_Right_Button);

        empty_Layout = rootView.findViewById(R.id.users_Empty_Layout);
        empty_Icon = rootView.findViewById(R.id.empty_Icon_Animation);
        empty_Title = rootView.findViewById(R.id.empty_Title_Tv);
        empty_Subtitle = rootView.findViewById(R.id.empty_Subtitle_Tv);

        searchUsers_Input = rootView.findViewById(R.id.users_Search_Input);
        rcv_Users = rootView.findViewById(R.id.users_Users_Rcv);
    }

    private void getAllUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(FirebaseHelper.USERS_TABLE);

        EventListener<QuerySnapshot> messagesEventListener = (usersSnapshot, error) -> {

            /** Error & Null Data Checking **/
            if (error != null) {
                Log.e(Constants.TAG, "User Error, Reason: " + error.getMessage(), error);
                return;
            }
            else if (usersSnapshot == null || usersSnapshot.isEmpty()) {
                Log.e(Constants.TAG, "No Users");
                usersAdapter.setUsers(new ArrayList<>());
                empty_Layout.setVisibility(View.VISIBLE);
                return;
            }

            List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : usersSnapshot) {
                User user = snapshot.toObject(User.class);
                users.add(user);
            }

            empty_Layout.setVisibility(users.size() == 0 ? View.VISIBLE : View.GONE);
            usersAdapter.setUsers(users);
        };

        Query query;
        if (specialty != null) {
            query = reference.whereEqualTo(User.ROLE, role.getId())
                    .whereEqualTo(User.VERIFIED, true)
                    .whereEqualTo((User.SPECIALTY + "." + Specialty.ID), specialty.getId())
                    .orderBy(User.RATING, Query.Direction.ASCENDING);
        }
        else {
            query = reference.whereEqualTo(User.ROLE, role.getId())
                    .whereEqualTo(User.VERIFIED, true)
                    .orderBy(User.RATING, Query.Direction.ASCENDING);

        }

        usersListenerRegistration = query.addSnapshotListener(messagesEventListener);
    }

    private void setUsersAdapter() {
        usersAdapter = new UsersAdapter(role.getAction());
        rcv_Users.setAdapter(usersAdapter);
        usersAdapter.setOnUserSelectListener(DoctorProfileFragment::show);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(usersListenerRegistration != null) usersListenerRegistration.remove();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
