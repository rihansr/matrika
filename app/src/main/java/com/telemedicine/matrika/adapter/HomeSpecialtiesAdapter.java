package com.telemedicine.matrika.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.telemedicine.matrika.R;
import com.telemedicine.matrika.model.specialty.Specialty;
import com.telemedicine.matrika.util.extensions.AppExtensions;

import java.util.ArrayList;
import java.util.List;

public class HomeSpecialtiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Specialty>     specialties = new ArrayList<>();
    private OnSpecialtyListener mOnSpecialtyListener;

    public HomeSpecialtiesAdapter() {}

    public void setSpecialties(List<Specialty> specialties) {
        this.specialties = specialties == null || specialties.isEmpty() ? new ArrayList<>() : specialties;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private Specialty specialty(int position){
        return specialties.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new SpecialtyViewHolder(layoutInflater.inflate(R.layout.sample_specialty, parent, false));
    }

    private static class SpecialtyViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView  icon;
        private final AppCompatTextView   name;

        private SpecialtyViewHolder(View v) {
            super(v);
            icon = v.findViewById(R.id.specialty_Icon_Iv);
            name = v.findViewById(R.id.specialty_Name_Tv);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof SpecialtyViewHolder){
            SpecialtyViewHolder viewHolder = (SpecialtyViewHolder) holder;
            AppExtensions.loadPhoto(viewHolder.icon, specialty(position).getIcon(), R.dimen.icon_Size_XX_Large, R.drawable.ic_placeholder);
            viewHolder.name.setText(specialty(position).getTitle());
            viewHolder.itemView.setOnClickListener(view -> {
                if(mOnSpecialtyListener!= null) mOnSpecialtyListener.onSelect(specialty(position));
            });
        }
    }

    @Override
    public int getItemCount() {
        return specialties.size();
    }

    public void setOnSpecialtySelectListener(OnSpecialtyListener onSpecialtyListener) {
        this.mOnSpecialtyListener = onSpecialtyListener;
    }

    public interface OnSpecialtyListener {
        void onSelect(Specialty specialty);
    }
}
