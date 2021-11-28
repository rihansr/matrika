package com.telemedicine.matrika.util.extensions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.fragment.AlertDialogFragment;
import com.telemedicine.matrika.fragment.LoadingFragment;
import com.telemedicine.matrika.helper.PermissionManager;
import com.telemedicine.matrika.model.address.District;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.wiget.InstantAutoCompleteTextView;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class AppExtensions {

    /**
     * Dialog & Activity Styles
     **/
    public static void halfScreenDialog(Dialog dialog){
        if (dialog == null) return;

        Window window = dialog.getWindow();
        if (window == null) return;

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.windowAnimations = R.style.DialogDefaultAnimation;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(params);
    }

    public static void fullScreenDialog(Dialog dialog, boolean isLightStatusBar){
        if (dialog == null) return;

        Window window = dialog.getWindow();
        if (window == null) return;

        if (isLightStatusBar) window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        else window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static void fullScreenActivity(Window window, boolean isStatusBarContentLight){
        if (window == null) return;
        if (isStatusBarContentLight) window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        else window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }


    /**
     * Soft Keyboard
     **/
    public static class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

    public static void hideKeyboard(View[] views) {
        InputMethodManager imm = (InputMethodManager) AppController.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(AppController.getActivity().getWindow().getDecorView().getWindowToken(), 0);
        for(View view : views) view.clearFocus();
    }

    public static void hideKeyboard(View view) {
        if(view != null){
            InputMethodManager imm = (InputMethodManager) AppController.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(View view) {
        if(view != null){
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) AppController.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void showKeyboardInDialog(View view) {
        if(view != null){
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) AppController.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)  imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void hideKeyboardInDialog() {
        InputMethodManager imm = (InputMethodManager) AppController.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)  imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void requestFocus(View view) {
        if (view.requestFocus()) {
            Objects.requireNonNull(AppController.getActivity().getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void requestFocus(AlertDialog dialog, View view) {
        if (view.requestFocus()) {
            Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    /**
     * Photo Actions
     **/
    public static void loadPhoto(AppCompatImageView holder, String photo, Integer size, Integer placeholder) {
        if (photo != null) {
            Glide.with(AppController.getContext())
                    .load(photo)
                    .override(size != null ? (int) getDimension(size) : Target.SIZE_ORIGINAL,
                            size != null ? (int) getDimension(size) : Target.SIZE_ORIGINAL)
                    .error(placeholder)
                    .fallback(placeholder)
                    .placeholder(placeholder)
                    .into(holder);
        } else {
            holder.setImageResource(placeholder);
        }
    }

    public static byte[] getBitmapBytes(Bitmap mBitmap, Integer size){
        Bitmap resizedBitmap = (size == null) ? mBitmap.copy(mBitmap.getConfig(), true) : getResizedBitmap(mBitmap, (size == 0 ? 1024 : size));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static HashMap<String, Object> getBitmapData(Bitmap mBitmap, Integer size){
        Bitmap resizedBitmap = (size == null) ? mBitmap.copy(mBitmap.getConfig(), true) : getResizedBitmap(mBitmap, (size == 0 ? 1024 : size));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        long imageSize = imageInByte.length;
        HashMap<String, Object> data = new HashMap<>();
        data.put("file", stream.toByteArray());
        data.put("size", imageSize);
        return data;
    }

    public static HashMap<String, Object> getUriData(Uri mUri){
        HashMap<String, Object> data = new HashMap<>();
        File file = new File(mUri.getPath());
        data.put("file", mUri);
        data.put("size", file.length());
        return data;
    }

    private static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        if(width<maxSize || height<maxSize) return image;

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        }
        else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    /**
     * Validate Input Field
     **/
    public static boolean isEmailValid(EditText inputField, Object errorMessage){
        String input = Objects.requireNonNull(inputField.getText()).toString().trim();

        if (errorMessage != null && TextUtils.isEmpty(Objects.requireNonNull(input))) {
            showError(inputField, errorMessage);
            return false;
        }
        else if (!TextUtils.isEmpty(Objects.requireNonNull(input)) && !Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            showError(inputField, R.string.validEmail_Error);
            return false;
        }
        else return true;
    }

    public static boolean isPasswordValid(EditText inputField, Object errorMessage){
        String input = Objects.requireNonNull(inputField.getText()).toString().trim();

        if (errorMessage != null && TextUtils.isEmpty(Objects.requireNonNull(input))) {
            showError(inputField, errorMessage);
            return false;
        }
        else if (!TextUtils.isEmpty(Objects.requireNonNull(input)) && input.length() < 8) {
            showError(inputField, R.string.validPassword_Error);
            return false;
        }
        else return true;
    }

    public static boolean isNumberValid(EditText inputField, Object errorMessage){
        String phone = inputField.getText().toString().trim();

        if (TextUtils.isEmpty(Objects.requireNonNull(phone))) {
            if(errorMessage != null) showError(inputField, errorMessage);
            return false;
        }

        if (!isNumberValidate(phone)){
            if(errorMessage != null) showError(inputField, R.string.validPhone_Error);
            return false;
        }
        else return true;
    }

    public static boolean isNumberValidate(String phone){
        if(TextUtils.isEmpty(phone.trim()) || phone.length() < 10) return false;
        final String formattedNumber = getValidateNumber(phone);
        return formattedNumber.length() == 14 && formattedNumber.matches(Constants.PHONE_PATTERN);
    }

    public static String getValidateNumber(Object object){
        String input;

        if(object instanceof String){
            input = String.valueOf(object);
        }
        else if(object instanceof Integer){
            input = getString((Integer) object);
        }
        else if(object instanceof InstantAutoCompleteTextView){
            input = Objects.requireNonNull(((InstantAutoCompleteTextView)object).getText()).toString().trim();
        }
        else if(object instanceof AppCompatAutoCompleteTextView){
            input = Objects.requireNonNull(((AppCompatAutoCompleteTextView)object).getText()).toString().trim();
        }
        else if(object instanceof EditText){
            input = Objects.requireNonNull(((EditText)object).getText()).toString().trim();
        }
        else if(object instanceof TextView){
            input = Objects.requireNonNull(((TextView)object).getText()).toString().trim();
        }
        else return null;

        return Constants.COUNTRY_CODE + (input.startsWith("0") ? input : ("0"+input));
    }

    public static boolean isInputValid(View inputField, Object errorMessage){
        String input = null;

        if(inputField instanceof InstantAutoCompleteTextView){
            input = Objects.requireNonNull(((InstantAutoCompleteTextView)inputField).getText()).toString().trim();
        }
        else if(inputField instanceof AutoCompleteTextView){
            input = Objects.requireNonNull(((AutoCompleteTextView)inputField).getText()).toString().trim();
        }
        else if(inputField instanceof EditText){
            input = Objects.requireNonNull(((EditText)inputField).getText()).toString().trim();
        }
        else if(inputField instanceof TextView){
            input = Objects.requireNonNull(((TextView)inputField).getText()).toString().trim();
        }

        if(TextUtils.isEmpty(Objects.requireNonNull(input))){
            showError(inputField, errorMessage);
            return false;
        }
        else return true;
    }

    public static boolean isInputValid(View inputField, boolean isConditionMatched, Object errorMessage){
        if(isConditionMatched){
            if(errorMessage != null) showError(inputField, errorMessage);
            return false;
        }
        else return true;
    }

    public static void showError(View inputField, Object errorMessage){
        if(errorMessage == null) return;

        if(inputField instanceof InstantAutoCompleteTextView){
            ((InstantAutoCompleteTextView) inputField).setError(errorMessage instanceof Integer ? getString((Integer) errorMessage): String.valueOf(errorMessage));
            AppExtensions.requestFocus(inputField);
        }
        else if(inputField instanceof AutoCompleteTextView){
            ((AutoCompleteTextView) inputField).setError(errorMessage instanceof Integer ? getString((Integer) errorMessage): String.valueOf(errorMessage));
            AppExtensions.requestFocus(inputField);
        }
        else if(inputField instanceof EditText){
            ((EditText) inputField).setError(errorMessage instanceof Integer ? getString((Integer) errorMessage): String.valueOf(errorMessage));
            AppExtensions.requestFocus(inputField);
        }
        else if(inputField instanceof TextView){
            ((TextView) inputField).setError(errorMessage instanceof Integer ? getString((Integer) errorMessage): String.valueOf(errorMessage));
            AppExtensions.requestFocus(inputField);
        }
    }

    public static void clearError(View inputField){
        if(inputField instanceof InstantAutoCompleteTextView) ((InstantAutoCompleteTextView) inputField).setError(null);
        else if(inputField instanceof AutoCompleteTextView) ((AutoCompleteTextView) inputField).setError(null);
        else if(inputField instanceof EditText) ((EditText) inputField).setError(null);
        else if(inputField instanceof TextView) ((TextView) inputField).setError(null);
        inputField.clearFocus();
    }


    /**
     * Formatter
     **/
    public static String nameFormat(String name) {
        String[] parts = name.trim().split(" ");
        for (String n : parts) {
            name = n.trim().replaceAll("[^(A-Za-z)]", "").trim();
            if (name.length() > 2) break;
        }
        return name;
    }

    public static String getReferralCode(String name) {
        name = nameFormat(name);
        name = name.toUpperCase();
        name = name.length() > 4 ? name.substring(0, 4) : name;
        int limit = 7;
        int length = name.length();
        String randomCode = getRandomCode(limit - length);
        return name + randomCode;
    }

    public static String getRandomCode(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    public static String formatValue(String value, String defaultText) {
        if(value == null || value.trim().isEmpty()) return defaultText;
        if(value.trim().matches("[a-zA-Z]+")) return defaultText;

        return decimalFormat(Double.parseDouble(value), "#.#", false);
    }

    public static String decimalFormat(double number, String format, Boolean alwaysDecimal) {
        if(format == null || format.trim().isEmpty()) return new DecimalFormat().format(Math.round(number));
        DecimalFormat df = new DecimalFormat(format);
        return (number % 1 == 0) && (alwaysDecimal == null || !alwaysDecimal) ? String.valueOf((int) number) : df.format(number);
    }


    /**
     * Resources
     **/
    public static String getString(int id){
        return AppController.getActivity().getResources().getString(id);
    }

    public static String[] getStringArray(int id){
        return AppController.getActivity().getResources().getStringArray(id);
    }

    public static Spanned getHtmlString(Object string){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(string instanceof String ? (String)string : getString((Integer)string) , Html.FROM_HTML_MODE_LEGACY);
        else
            return Html.fromHtml(string instanceof String ? (String)string : getString((Integer)string));
    }

    public static float getDimension(int id){
        return AppController.getContext().getResources().getDimension(id);
    }

    public static int getDips(int dps) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                AppController.getContext().getResources().getDisplayMetrics());
    }

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = AppController.getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int getColor(int id){
        return ContextCompat.getColor(AppController.getActivity(), id);
    }

    public static Drawable getDrawable(int id){
        return ContextCompat.getDrawable(AppController.getActivity(), id);
    }


    /**
     * Others
     **/
    public static void toast(Object message){
        Toast customToast = Toast.makeText(AppController.getContext(), message instanceof String ? (String)message : getString((Integer)message), Toast.LENGTH_LONG);
        customToast.setGravity(Gravity.BOTTOM,0,50);
        View toastView = customToast.getView();

        TextView textView = Objects.requireNonNull(toastView).findViewById(android.R.id.message);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(14);
        toastView.setPadding(15,10,15,10);
        toastView.setBackgroundResource(R.drawable.shape_toast);

        customToast.show();
    }

    public static List<District> getDistricts() {
        try {
            InputStream inputStream = AppController.getContext().getAssets().open("districts.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            return new LinkedList<>(Arrays.asList(new GsonBuilder().setPrettyPrinting().create().fromJson(new String(buffer, StandardCharsets.UTF_8), District[].class)));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return new LinkedList<>();
        }
    }

    public static void doGradientText(TextView text){
        TextPaint paint = text.getPaint();
        float width = paint.measureText(text.getText().toString().trim());

        Shader textShader = new LinearGradient(0, 0, width, text.getTextSize(),
                new int[]{AppExtensions.getColor(R.color.colorAccent), AppExtensions.getColor(R.color.colorPrimary)},
                null, Shader.TileMode.CLAMP
        );

        text.getPaint().setShader(textShader);
    }

    public static String showGreeting() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if(timeOfDay < 7){
            greeting =  getString(R.string.hello);
        }
        else if(timeOfDay < 12){
            greeting =  getString(R.string.morning);
        }
        else if(timeOfDay < 16){
            greeting =  getString(R.string.afternoon);
        }
        else if(timeOfDay < 21){
            greeting =  getString(R.string.evening);
        }
        else {
            greeting = getString(R.string.hello);
        }

        return greeting + (LocalStorage.USER == null ? "" : ", " + nameFormat(LocalStorage.USER.getName()));
    }

    public static View getRootView(Dialog dialog){
        return Objects.requireNonNull(Objects.requireNonNull(dialog).getWindow()).getDecorView().getRootView();
    }

    public static String join(List<String> list , Object separator){
        if(list == null || list.isEmpty()) return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != (list.size() - 1)) {
                if(!(separator instanceof String ? String.valueOf(separator) : AppExtensions.getString((Integer) separator)).equals(",")) sb.append(" ");
                sb.append(separator instanceof String ? String.valueOf(separator) : AppExtensions.getString((Integer) separator));
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    public static void dismissLoading(LoadingFragment loading){
        if (loading != null && loading.getDialog() != null
                &&
                loading.getDialog().isShowing()
                &&
                !loading.isRemoving())

            loading.dismiss();
    }

    public static void shareApk() {
        try {
            Activity activity = AppController.getActivity();
            File initialApkFile = new File(activity.getPackageManager().getApplicationInfo(activity.getPackageName(), 0).sourceDir);

            File tempFile = new File(activity.getExternalCacheDir() + "/ExtractedApk");

            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;

            tempFile = new File(tempFile.getPath() + "/" + getString(R.string.app_name) + ".apk");

            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }

            InputStream in = new FileInputStream(initialApkFile);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            shareFile(activity, tempFile);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void shareFile(Context context, File sharePath) {
        Uri uri;
        if (Build.VERSION.SDK_INT < 24) {
            uri = Uri.parse("file://" + sharePath);
        }
        else {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(String.valueOf(sharePath)));
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareIntent.setType("*/*");
        context.startActivity(Intent.createChooser(shareIntent, getString(R.string.shareAPkVia)));
    }

    public static void call(Object phone, Object title){
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + (phone instanceof String ? String.valueOf(phone) : getString((Integer) phone))));
        if (!new PermissionManager(PermissionManager.Permission.PHONE, true, response ->
                AppController.getActivity().startActivity(callIntent) ).isGranted()) return;

        AlertDialogFragment.show(title instanceof String ? String.valueOf(title) : getString((Integer) title), null, R.string.cancel, R.string.call)
                .setOnDialogListener(new AlertDialogFragment.OnDialogListener() {
                    @Override
                    public void onLeftButtonClick() {}

                    @Override
                    public void onRightButtonClick() {
                        AppController.getActivity().startActivity(callIntent);
                    }
                });
    }
}
