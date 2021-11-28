package com.telemedicine.matrika.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.ortiz.touchview.TouchImageView;

public class PhotoViewFragment extends DialogFragment {

    private static final String     TAG = PhotoViewFragment.class.getSimpleName();
    private String                  photoLink;
    private TouchImageView          photoHolder;
    private AppCompatImageView      backBtn;

    public static PhotoViewFragment show(String photo){
        PhotoViewFragment fragment = new PhotoViewFragment();
        if (photo != null) {
            Bundle args = new Bundle();
            args.putString(Constants.PHOTO_BUNDLE_KEY, photo);
            fragment.setArguments(args);
        }
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_TopLight_FadeAnimation);
        setRetainInstance(true);
        setCancelable(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout_photo_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        AppExtensions.fullScreenDialog(getDialog(), true);
        super.onViewCreated(view, savedInstanceState);

        initId(view);

        if (getArguments() == null) { dismiss(); return; }
        photoLink = getArguments().getString(Constants.PHOTO_BUNDLE_KEY);
        init();
        getArguments().remove(Constants.PHOTO_BUNDLE_KEY);
    }

    private void initId(View view) {
        photoHolder = view.findViewById(R.id.photo_Holder_Iv);
        backBtn = view.findViewById(R.id.photo_Back_Button);
    }

    private void init() {
        AppExtensions.loadPhoto(photoHolder, photoLink, null, R.drawable.ic_placeholder);
        backBtn.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
