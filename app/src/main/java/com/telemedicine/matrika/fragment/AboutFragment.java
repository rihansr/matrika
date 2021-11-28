package com.telemedicine.matrika.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import com.telemedicine.matrika.BuildConfig;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import java.util.Locale;

public class AboutFragment extends DialogFragment{

    private static final String TAG = AboutFragment.class.getSimpleName();

    /**
     * Toolbar
     **/
    private AppCompatImageView  toolbar_Back_Button;
    private AppCompatTextView   toolbar_title;
    private AppCompatImageView  toolbar_Right_Button;

    /**
     * Other
     **/
    private AppCompatTextView title_Tv;
    private AppCompatTextView subtitle_Tv;
    private AppCompatTextView version_Tv;

    public static AboutFragment show(){
        AboutFragment fragment = new AboutFragment();
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
        return inflater.inflate(R.layout.fragment_layout_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        AppExtensions.fullScreenDialog(getDialog(), false);
        super.onViewCreated(view, savedInstanceState);

        idSetup(view);

        init();
    }

    private void idSetup(View rootView) {
        toolbar_title = rootView.findViewById(R.id.toolbar_Title_Tv);
        toolbar_Back_Button = rootView.findViewById(R.id.toolbar_Left_Button);
        toolbar_Right_Button = rootView.findViewById(R.id.toolbar_Right_Button);

        title_Tv = rootView.findViewById(R.id.about_Title_Tv);
        subtitle_Tv = rootView.findViewById(R.id.about_Subtitle_Tv);
        version_Tv = rootView.findViewById(R.id.about_Version_Tv);
    }

    private void init(){
        toolbar_title.setText(AppExtensions.getString(R.string.about));
        toolbar_Back_Button.setOnClickListener(v -> dismiss());
        toolbar_Right_Button.setVisibility(View.GONE);

        version_Tv.setText(String.format(Locale.getDefault(), "V %s", BuildConfig.VERSION_NAME));
        AppExtensions.doGradientText(title_Tv);
        AppExtensions.doGradientText(subtitle_Tv);
        AppExtensions.doGradientText(version_Tv);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
