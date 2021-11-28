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
import com.telemedicine.matrika.model.specialty.Specialty;

import java.util.LinkedList;
import java.util.List;

public class SpecialtyAdapter extends ArrayAdapter<Specialty> {

    private final List<Specialty> specialties;

    public SpecialtyAdapter(@NonNull Context context, List<Specialty> specialties) {
        super(context, 0, specialties);
        this.specialties = new LinkedList<>(specialties);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return specialtyFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull final ViewGroup parent) {
        if(view == null) view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_spinner, parent, false);

        AppCompatTextView itemName = view.findViewById(R.id.itemTv);

        final Specialty upazila = getItem(position);

        if(upazila != null) itemName.setText(upazila.getSpecialty());

        return view;
    }

    private final Filter specialtyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Specialty> filtered_Specialties = new LinkedList<>();

            if(charSequence == null || charSequence.length() == 0){
                filtered_Specialties.addAll(specialties);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Specialty specialty : specialties){
                    if(specialty.getSpecialty().toLowerCase().contains(filterPattern) || specialty.getTitle().contains(filterPattern)){
                        filtered_Specialties.add(specialty);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filtered_Specialties;

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
            return ((Specialty) resultValue).getSpecialty();
        }
    };
}
