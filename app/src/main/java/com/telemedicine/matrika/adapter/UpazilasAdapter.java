package com.telemedicine.matrika.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.telemedicine.matrika.R;
import com.telemedicine.matrika.model.address.Upazila;

import java.util.LinkedList;
import java.util.List;

public class UpazilasAdapter extends ArrayAdapter<Upazila> {

    private final List<Upazila> all_Upazilas;

    public UpazilasAdapter(@NonNull Context context, List<Upazila> upazilas) {
        super(context, 0, upazilas);
        all_Upazilas = new LinkedList<>(upazilas);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return upazilaFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull final ViewGroup parent) {
        if(view == null) view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_spinner, parent, false);

        AppCompatTextView itemName = view.findViewById(R.id.itemTv);

        final Upazila upazila = getItem(position);

        if(upazila != null) itemName.setText(upazila.getName());

        return view;
    }

    private final Filter upazilaFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Upazila> filtered_Upazilas = new LinkedList<>();

            if(charSequence == null || charSequence.length() == 0){
                filtered_Upazilas.addAll(all_Upazilas);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Upazila upazila : all_Upazilas){
                    if(upazila.getName().toLowerCase().contains(filterPattern) || upazila.getBn_name().contains(filterPattern)){
                        filtered_Upazilas.add(upazila);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filtered_Upazilas;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Upazila) resultValue).getName();
        }
    };
}
