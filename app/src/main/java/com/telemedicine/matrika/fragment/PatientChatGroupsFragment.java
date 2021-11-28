package com.telemedicine.matrika.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.adapter.ChatGroupAdapter;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.model.chat.Group;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.extensions.AppExtensions;

import java.util.ArrayList;
import java.util.List;

public class PatientChatGroupsFragment extends DialogFragment {

    private static final String  TAG = PatientChatGroupsFragment.class.getSimpleName();
    private View                 rootView;

    /**
     * Toolbar
     **/
    private AppCompatImageView   toolbar_Back_Button;
    private AppCompatTextView    toolbar_title;
    private AppCompatImageView   toolbar_Right_Button;

    /**
     * Content
     **/
    private RecyclerView        rcv_Chats;
    private ChatGroupAdapter    chatGroupAdapter;

    /**
     * Empty Layout
     **/
    private LinearLayoutCompat   empty_Layout;
    private LottieAnimationView  empty_Icon;
    private AppCompatTextView    empty_Title;
    private AppCompatTextView    empty_Subtitle;

    private ListenerRegistration chatGroupsListenerRegistration;
    private LoadingFragment      loading;

    public static PatientChatGroupsFragment show(){
        PatientChatGroupsFragment fragment = new PatientChatGroupsFragment();
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
        rootView = inflater.inflate(R.layout.fragment_layout_patient_chat_groups, container, false);
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

        toolbar_title.setText(AppExtensions.getString(R.string.chats));
        toolbar_Back_Button.setOnClickListener(v -> dismiss());
        toolbar_Right_Button.setVisibility(View.GONE);

        empty_Icon.setAnimation("lottie/blank_chats.json");
        empty_Title.setText(AppExtensions.getString(R.string.emptyChatsTitle));
        empty_Subtitle.setText(AppExtensions.getString(R.string.emptyChatsSubtitle));

        setAdapter();

        getChatGroups();
    }

    private void initId() {
        toolbar_title = rootView.findViewById(R.id.toolbar_Title_Tv);
        toolbar_Back_Button = rootView.findViewById(R.id.toolbar_Left_Button);
        toolbar_Right_Button = rootView.findViewById(R.id.toolbar_Right_Button);

        empty_Layout = rootView.findViewById(R.id.chats_Empty_Layout);
        empty_Icon = rootView.findViewById(R.id.empty_Icon_Animation);
        empty_Title = rootView.findViewById(R.id.empty_Title_Tv);
        empty_Subtitle = rootView.findViewById(R.id.empty_Subtitle_Tv);

        rcv_Chats = rootView.findViewById(R.id.chat_Chats_Rcv);
    }

    private void getChatGroups() {
        loading = LoadingFragment.show();
        EventListener<QuerySnapshot> chatGroupsEventListener = (requestSnapshot, error) -> {
            AppExtensions.dismissLoading(loading);

            /** Error & Null Data Checking **/
            if (error != null) {
                chatGroupAdapter.setChatGroups(new ArrayList<>());
                Log.e(Constants.TAG, "Chat Group Error, Reason: " + error.getMessage(), error);
                if(error.getMessage().contains("PERMISSION_DENIED")){
                    empty_Icon.setAnimation("lottie/not_verified.json");
                    empty_Layout.setVisibility(View.VISIBLE);
                }
                return;
            }
            else if (requestSnapshot == null || requestSnapshot.isEmpty()) {
                Log.e(Constants.TAG, "No Chat Groups");
                chatGroupAdapter.setChatGroups(new ArrayList<>());
                empty_Icon.setAnimation("lottie/blank_chats.json");
                empty_Layout.setVisibility(View.VISIBLE);
                return;
            }

            /** Get All Chat Groups **/
            List<Group> chatGroups = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : requestSnapshot) {
                Group group = snapshot.toObject(Group.class);
                chatGroups.add(group);
            }

            chatGroupAdapter.setChatGroups(chatGroups);

            empty_Icon.setAnimation("lottie/blank_chats.json");
            empty_Layout.setVisibility(chatGroups.isEmpty() ? View.VISIBLE : View.GONE);
        };

        chatGroupsListenerRegistration = FirebaseFirestore.getInstance()
                .collection(FirebaseHelper.CHAT_GROUPS_TABLE)
                .whereArrayContains(Group.USERS, LocalStorage.USER.getId())
                .addSnapshotListener(chatGroupsEventListener);
    }

    private void setAdapter(){
        chatGroupAdapter = new ChatGroupAdapter();
        rcv_Chats.setAdapter(chatGroupAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(chatGroupsListenerRegistration != null) chatGroupsListenerRegistration.remove();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
