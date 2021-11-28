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
import com.telemedicine.matrika.model.specialty.Field;

import java.util.LinkedList;
import java.util.List;

public class SpecialistAdapter extends ArrayAdapter<Field> {

    private final List<Field> fields;

    public SpecialistAdapter(@NonNull Context context, List<Field> fields) {
        super(context, 0, fields);
        this.fields = new LinkedList<>(fields);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return specialistFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull final ViewGroup parent) {
        if(view == null) view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_spinner, parent, false);

        AppCompatTextView itemName = view.findViewById(R.id.itemTv);

        final Field field = getItem(position);

        if(field != null) itemName.setText(field.getTitle());

        return view;
    }

    private final Filter specialistFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Field> filtered_Fields = new LinkedList<>();

            if(charSequence == null || charSequence.length() == 0){
                filtered_Fields.addAll(fields);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Field field : fields){
                    if(field.getTitle().toLowerCase().contains(filterPattern) || field.getTitle().contains(filterPattern)){
                        filtered_Fields.add(field);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filtered_Fields;

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
            return ((Field) resultValue).getTitle();
        }
    };
}
