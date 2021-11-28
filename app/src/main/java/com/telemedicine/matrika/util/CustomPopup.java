package com.telemedicine.matrika.util;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.telemedicine.matrika.R;
import com.telemedicine.matrika.adapter.DistrictsAdapter;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.util.extensions.AppExtensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.telemedicine.matrika.util.extensions.AppExtensions.getDrawable;

public class CustomPopup {

    public enum Popup{ WINDOW, MENU }

    private Context             mContext;
    private View                anchor;
    private View                input;
    private Integer             height;
    private ArrayAdapter        adapter;
    private List<String>        items;
    private Popup               type;
    private OnPopupListener     mOnPopupListener;
    private OnItemClickListener mOnItemClickListener;

    public CustomPopup(View anchor, View input, ArrayAdapter adapter, Integer height, Popup type, OnPopupListener listener) {
        init(anchor, input, adapter, null, height, type, listener);
    }

    public CustomPopup(View anchor, View input, ArrayAdapter adapter, Integer height, Popup type, OnItemClickListener listener) {
        init(anchor, input, adapter, null, height, type, listener);
    }

    public CustomPopup(View anchor, View input, ArrayAdapter adapter, Integer height, Popup type) {
        init(anchor, input, adapter, null, height, type, null);
    }


    public CustomPopup(View anchor, View input, List<String> items, Integer height, Popup type, OnPopupListener listener) {
        init(anchor, input, null, items, height, type, listener);
    }

    public CustomPopup(View anchor, View input, List<String> items, Integer height, Popup type, OnItemClickListener listener) {
        init(anchor, input, null, items, height, type, listener);
    }

    public CustomPopup(View anchor, View input, List<String> items, Integer height, Popup type) {
        init(anchor, input, null, items, height, type, null);
    }


    public CustomPopup(View anchor, View input, String[] items, Integer height, Popup type, OnPopupListener listener) {
        init(anchor, input, null, Arrays.asList(items), height, type, listener);
    }

    public CustomPopup(View anchor, View input, String[] items, Integer height, Popup type, OnItemClickListener listener) {
        init(anchor, input, null, Arrays.asList(items), height, type, listener);
    }

    public CustomPopup(View anchor, View input, String[] items, Integer height, Popup type) {
        init(anchor, input, null, Arrays.asList(items), height, type, null);
    }


    public CustomPopup(View anchor, ArrayAdapter adapter, Integer height, Popup type, OnPopupListener listener) {
        init(null, anchor, adapter, null, height, type, listener);
    }

    public CustomPopup(View anchor, ArrayAdapter adapter, Integer height, Popup type, OnItemClickListener listener) {
        init(null, anchor, adapter, null, height, type, listener);
    }

    public CustomPopup(View anchor, ArrayAdapter adapter, Integer height, Popup type) {
        init(null, anchor, adapter, null, height, type, null);
    }


    public CustomPopup(View anchor, List<String> items, Integer height, Popup type, OnPopupListener listener) {
        init(null, anchor, null, items, height, type, listener);
    }

    public CustomPopup(View anchor, List<String> items, Integer height, Popup type, OnItemClickListener listener) {
        init(null, anchor, null, items, height, type, listener);
    }

    public CustomPopup(View anchor, List<String> items, Integer height, Popup type) {
        init(null, anchor, null, items, height, type, null);
    }


    public CustomPopup(View anchor, String[] items, Integer height, Popup type, OnPopupListener listener) {
        init(null, anchor, null, Arrays.asList(items), height, type, listener);
    }

    public CustomPopup(View anchor, String[] items, Integer height, Popup type, OnItemClickListener listener) {
        init(null, anchor, null, Arrays.asList(items), height, type, listener);
    }

    public CustomPopup(View anchor, String[] items, Integer height, Popup type) {
        init(null, anchor, null, Arrays.asList(items), height, type, null);
    }


    public CustomPopup(View anchor, View input, ArrayAdapter adapter, Popup type, OnPopupListener listener) {
        init(anchor, input, adapter, null, null, type, listener);
    }

    public CustomPopup(View anchor, View input, ArrayAdapter adapter, Popup type, OnItemClickListener listener) {
        init(anchor, input, adapter, null, null, type, listener);
    }

    public CustomPopup(View anchor, View input, ArrayAdapter adapter, Popup type) {
        init(anchor, input, adapter, null, null, type, null);
    }


    public CustomPopup(View anchor, View input, List<String> items, Popup type, OnPopupListener listener) {
        init(anchor, input, null, items, null, type, listener);
    }

    public CustomPopup(View anchor, View input, List<String> items, Popup type, OnItemClickListener listener) {
        init(anchor, input, null, items, null, type, listener);
    }

    public CustomPopup(View anchor, View input, List<String> items, Popup type) {
        init(anchor, input, null, items, null, type, null);
    }


    public CustomPopup(View anchor, View input, String[] items, Popup type, OnPopupListener listener) {
        init(anchor, input, null, Arrays.asList(items), null, type, listener);
    }

    public CustomPopup(View anchor, View input, String[] items, Popup type, OnItemClickListener listener) {
        init(anchor, input, null, Arrays.asList(items), null, type, listener);
    }

    public CustomPopup(View anchor, View input, String[] items, Popup type) {
        init(anchor, input, null, Arrays.asList(items), null, type, null);
    }


    public CustomPopup(View anchor, ArrayAdapter adapter, Popup type, OnPopupListener listener) {
        init(null, anchor, adapter, null, null, type, listener);
    }

    public CustomPopup(View anchor, ArrayAdapter adapter, Popup type, OnItemClickListener listener) {
        init(null, anchor, adapter, null, null, type, listener);
    }

    public CustomPopup(View anchor, ArrayAdapter adapter, Popup type) {
        init(null, anchor, adapter, null, null, type, null);
    }


    public CustomPopup(View anchor, List<String> items, Popup type, OnPopupListener listener) {
        init(null, anchor, null, items, null, type, listener);
    }

    public CustomPopup(View anchor, List<String> items, Popup type, OnItemClickListener listener) {
        init(null, anchor, null, items, null, type, listener);
    }

    public CustomPopup(View anchor, List<String> items, Popup type) {
        init(null, anchor, null, items, null, type, null);
    }


    public CustomPopup(View anchor, String[] items, Popup type, OnPopupListener listener) {
        init(null, anchor, null, Arrays.asList(items), null, type, listener);
    }

    public CustomPopup(View anchor, String[] items, Popup type, OnItemClickListener listener) {
        init(null, anchor, null, Arrays.asList(items), null, type, listener);
    }

    public CustomPopup(View anchor, String[] items, Popup type) {
        init(null, anchor, null, Arrays.asList(items), null, type, null);
    }


    private void init(View anchor, View input, ArrayAdapter adapter, List<String> items, Integer height, Popup type, Object listener){
        this.mContext = AppController.getContext();
        this.anchor = anchor != null ? anchor : input;
        this.input = input;
        this.adapter = adapter;
        this.items = items != null && !items.isEmpty() ? items : new ArrayList<>();
        this.height = height != null ? height : WindowManager.LayoutParams.WRAP_CONTENT;
        this.type = type;
        if (listener != null) {
            if(listener instanceof OnItemClickListener) this.mOnItemClickListener = (OnItemClickListener) listener;
            else this.mOnPopupListener = (OnPopupListener) listener;
        }
        else show();
    }

    public void show() {
        switch (type){
            case WINDOW: showPopupWindow(); break;
            case MENU: showPopupMenu(); break;
        }
    }

    private void showPopupMenu() {
        final PopupMenu mPopup = new PopupMenu(mContext, anchor);

        for (String item : items) {
            if(item == null || item.trim().isEmpty()) continue;
            mPopup.getMenu().add(item);
        }

        mPopup.setOnMenuItemClickListener(item -> {
            if(mOnPopupListener != null) mOnPopupListener.onItemClick(items.indexOf(item.getTitle().toString()), item.getTitle().toString());
            else {
                if(input instanceof EditText) ((EditText) input).setText(item.getTitle());
                else if(input instanceof TextView) ((TextView) input).setText(item.getTitle());
            }
            return true;
        });

        mPopup.show();
    }

    private void showPopupWindow() {
        ListPopupWindow popupWindow = new ListPopupWindow(mContext);
        popupWindow.setAdapter(adapter != null ? adapter : new ArrayAdapter<>(mContext, R.layout.sample_spinner, items));

        popupWindow.setAnchorView(anchor);
        popupWindow.setHeight(height);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setOnItemClickListener((parent, view, position, id) -> {
            if(mOnPopupListener != null) {
                mOnPopupListener.onItemClick(position, items.get(position));
                popupWindow.dismiss();
                return;
            }
            if(mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(parent, view, position, id);
                popupWindow.dismiss();
                return;
            }
            if(input instanceof EditText) ((EditText) input).setText(items.get(position));
            else if(input instanceof TextView) ((TextView) input).setText(items.get(position));
            popupWindow.dismiss();
        });

        popupWindow.setBackgroundDrawable(getDrawable(R.drawable.shape_popup));
        popupWindow.setModal(true);
        popupWindow.show();
    }

    public void setOnPopupListener(OnPopupListener onPopupListener) {
        this.mOnPopupListener = onPopupListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnPopupListener {
        void onItemClick(int position, String item);
    }
}
