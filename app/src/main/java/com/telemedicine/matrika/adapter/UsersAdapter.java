package com.telemedicine.matrika.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.fragment.ChatMessagesFragment;
import com.telemedicine.matrika.fragment.DoctorProfileFragment;
import com.telemedicine.matrika.fragment.LoadingFragment;
import com.telemedicine.matrika.fragment.PatientProfileFragment;
import com.telemedicine.matrika.fragment.PhotoViewFragment;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.model.other.Request;
import com.telemedicine.matrika.model.other.Status;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.telemedicine.matrika.wiget.CircleImageView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final int               viewType;
    private List<User>              users = new ArrayList<>();
    private List<User>              all_Users = new ArrayList<>();
    private final FirebaseHelper    firebaseHelper = new FirebaseHelper();
    private OnUserSelectListener    mOnUserSelectListener;
    private LoadingFragment         loading;

    public UsersAdapter(int viewType) {
        this.viewType = viewType;
    }

    public void setUsers(List<User> users) {
        this.users = users != null && !users.isEmpty() ? users : new ArrayList<>();
        this.all_Users = new ArrayList<>(this.users);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    private User user(int pos){
        return users.get(pos);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case 0: return new RequestViewHolder(layoutInflater.inflate(R.layout.sample_user_request, parent, false));
            default:
            case 1: return new UserViewHolder(layoutInflater.inflate(R.layout.sample_user, parent, false));
            case 2: return new DoctorViewHolder(layoutInflater.inflate(R.layout.sample_doctor, parent, false));
        }
    }

    private static class RequestViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView     user_Photo;
        private final AppCompatTextView   user_Name;
        private final AppCompatTextView   user_Email;
        private final AppCompatTextView   add_Button;
        private final AppCompatImageView  remove_Button;

        private RequestViewHolder(View v) {
            super(v);
            user_Photo = v.findViewById(R.id.request_UserPhoto_Iv);
            user_Name = v.findViewById(R.id.request_UserName_Tv);
            user_Email = v.findViewById(R.id.request_UserMail_Tv);
            add_Button = v.findViewById(R.id.request_Add_Button);
            remove_Button = v.findViewById(R.id.request_Remove_Button);
        }
    }

    private static class UserViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView     user_Photo;
        private final AppCompatImageView  user_ActiveIcon;
        private final AppCompatTextView   user_Name;
        private final AppCompatTextView   user_Email;
        private final AppCompatTextView   chat_Button;
        private final AppCompatImageView  remove_Button;

        private UserViewHolder(View v) {
            super(v);
            user_Photo = v.findViewById(R.id.user_Photo_Iv);
            user_ActiveIcon = v.findViewById(R.id.user_Active_Icon);
            user_Name = v.findViewById(R.id.user_Name_Tv);
            user_Email = v.findViewById(R.id.user_Email_Tv);
            chat_Button = v.findViewById(R.id.user_Chat_Button);
            remove_Button = v.findViewById(R.id.user_Remove_Button);
        }
    }

    private static class DoctorViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView     doctor_Photo;
        private final AppCompatImageView  doctor_ActiveIcon;
        private final AppCompatTextView   doctor_Name;
        private final AppCompatTextView   doctor_Specialty;
        private final AppCompatTextView   patient_AdditionalInfo;

        private DoctorViewHolder(View v) {
            super(v);
            doctor_Photo = v.findViewById(R.id.doctor_Photo_Iv);
            doctor_ActiveIcon = v.findViewById(R.id.doctor_Active_Icon);
            doctor_Name = v.findViewById(R.id.doctor_Name_Tv);
            doctor_Specialty = v.findViewById(R.id.doctor_Specialty_Tv);
            patient_AdditionalInfo = v.findViewById(R.id.doctor_AdditionalInfo_Tv);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int pos) {
        if(viewHolder instanceof RequestViewHolder){
            RequestViewHolder holder = (RequestViewHolder) viewHolder;
            AppExtensions.loadPhoto(holder.user_Photo, user(pos).getPhoto(), R.dimen.icon_Size_X_Large, R.drawable.ic_avatar);

            holder.user_Photo.setOnClickListener(view -> {
                if(user(pos).getRole().equals(Role.PATIENT.getId())) PatientProfileFragment.show(user(pos));
                else if(user(pos).getRole().equals(Role.DOCTOR.getId())) DoctorProfileFragment.show(user(pos));
            });

            holder.user_Photo.setOnLongClickListener(view -> {
                PhotoViewFragment.show(user(pos).getPhoto());
                return false;
            });

            holder.user_Name.setText(user(pos).getName());
            holder.user_Email.setText(user(pos).getEmail());

            holder.add_Button.setOnClickListener(view -> {
                loading = LoadingFragment.show();
                firebaseHelper.setDocumentData(FirebaseFirestore.getInstance()
                                .collection(FirebaseHelper.USER_REQUESTS_TABLE)
                                .document(user(pos).getRequestId())
                                .update(Request.ACCEPTED, new Status(true, new Date())),
                                new FirebaseHelper.OnFirebaseUpdateListener() {
                                    @Override
                                    public void onSuccess() { AppExtensions.dismissLoading(loading); }

                                    @Override
                                    public void onFailure() { AppExtensions.dismissLoading(loading); }

                                    @Override
                                    public void onCancelled() { AppExtensions.dismissLoading(loading); }
                                });
                    }
            );

            holder.remove_Button.setOnClickListener(view -> {
                loading = LoadingFragment.show();
                firebaseHelper.setDocumentData(FirebaseFirestore.getInstance()
                                .collection(FirebaseHelper.USER_REQUESTS_TABLE)
                                .document(user(pos).getRequestId())
                                .update(Request.REJECTED, new Status(true, new Date())),
                                new FirebaseHelper.OnFirebaseUpdateListener() {
                                    @Override
                                    public void onSuccess() { AppExtensions.dismissLoading(loading); }

                                    @Override
                                    public void onFailure() { AppExtensions.dismissLoading(loading); }

                                    @Override
                                    public void onCancelled() { AppExtensions.dismissLoading(loading); }
                                });
                    }
            );
        }
        else if(viewHolder instanceof UserViewHolder){
            UserViewHolder holder = (UserViewHolder) viewHolder;
            AppExtensions.loadPhoto(holder.user_Photo, user(pos).getPhoto(), R.dimen.icon_Size_XXXX_Large, R.drawable.ic_avatar);

            holder.user_Photo.setOnClickListener(view -> {
                if(user(pos).getRole().equals(Role.PATIENT.getId())) PatientProfileFragment.show(user(pos));
                else if(user(pos).getRole().equals(Role.DOCTOR.getId())) DoctorProfileFragment.show(user(pos));
            });

            holder.user_Photo.setOnLongClickListener(view -> {
                PhotoViewFragment.show(user(pos).getPhoto());
                return false;
            });

            holder.user_ActiveIcon.setVisibility(user(pos).getActive().getStatus() ? View.VISIBLE : View.GONE);
            holder.user_Name.setText(user(pos).getName());
            holder.user_Email.setText(user(pos).getEmail());

            holder.chat_Button.setOnClickListener(view -> ChatMessagesFragment.show(user(pos), null, null));

            holder.remove_Button.setOnClickListener(view -> {
                        loading = LoadingFragment.show();
                        firebaseHelper.setDocumentData(FirebaseFirestore.getInstance()
                                        .collection(FirebaseHelper.USER_REQUESTS_TABLE)
                                        .document(user(pos).getRequestId())
                                        .update(Request.REJECTED, new Status(true, new Date())),
                                        new FirebaseHelper.OnFirebaseUpdateListener() {
                                            @Override
                                            public void onSuccess() { AppExtensions.dismissLoading(loading); }

                                            @Override
                                            public void onFailure() { AppExtensions.dismissLoading(loading); }

                                            @Override
                                            public void onCancelled() { AppExtensions.dismissLoading(loading); }
                                        });
                    }
            );

            holder.itemView.setOnClickListener(view -> {
                if(mOnUserSelectListener != null) mOnUserSelectListener.onSelect(user(pos));
            });
        }
        else if(viewHolder instanceof DoctorViewHolder){
            DoctorViewHolder holder = (DoctorViewHolder) viewHolder;
            AppExtensions.loadPhoto(holder.doctor_Photo, user(pos).getPhoto(), R.dimen.icon_Size_XXXX_Large, R.drawable.ic_avatar);
            holder.doctor_ActiveIcon.setVisibility(user(pos).getActive().getStatus() ? View.VISIBLE : View.GONE);
            holder.doctor_Name.setText(user(pos).getName());
            holder.doctor_Specialty.setText(user(pos).getSpecialty().getTitle());
            holder.patient_AdditionalInfo.setText(
                    String.format("%s " + AppExtensions.getString(R.string.bullet) + " %d " + AppExtensions.getString(R.string.yrs) + " " + AppExtensions.getString(R.string.bullet) + " %s",
                            AppExtensions.decimalFormat(user(pos).getRating(), "0.0", true), user(pos).getExperienceYears(), user(pos).getGender()
                    ));
            holder.itemView.setOnClickListener(view -> {
                if(mOnUserSelectListener != null) mOnUserSelectListener.onSelect(user(pos));
            });
        }
    }

    @Override
    public Filter getFilter() {
        return SearchFilter;
    }

    private final Filter SearchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<User> filtered_Users = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filtered_Users.addAll(all_Users);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (User user : all_Users){
                    if(user.getName().toLowerCase().contains(filterPattern)
                            ||
                            (user.getSpecialty() != null && user.getSpecialty().getSpecialty().toLowerCase().contains(filterPattern))){
                        filtered_Users.add(user);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filtered_Users;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            users.clear();
            users.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public interface OnUserSelectListener{
        void onSelect(User user);
    }

    public void setOnUserSelectListener(OnUserSelectListener mOnUserSelectListener) {
        this.mOnUserSelectListener = mOnUserSelectListener;
    }
}
