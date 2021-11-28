package com.telemedicine.matrika.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;
import com.telemedicine.matrika.activity.SignInActivity;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.enums.Photo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;
import java.util.UUID;

public class FirebaseHelper {

    private final Context       context;

    /**
     * Firestore Tables
     **/
    public static final String  ROLES_TABLE = "roles";
    public static final String  USERS_TABLE = "users";
    public static final String  USER_REPORTS_TABLE = "user_reports";
    public static final String  DEVICE_REPORTS_TABLE = "device_reports";
    public static final String  SPECIALTIES_TABLE = "Specialties";
    public static final String  USER_REQUESTS_TABLE = "requests";
    public static final String  FOLLOWERS_TABLE = "followers";
    public static final String  USER_FEEDBACKS_TABLE = "feedbacks";
    public static final String  PROFILE_REPORTS_TABLE = "reports";
    public static final String  CHAT_GROUPS_TABLE = "chat_groups";
    public static final String  CHAT_MESSAGES_TABLE = "chat_messages";

    /**
     * Firestore Collections
     **/
    public static final String  MESSAGES_COLLECTION = "messages";
    public static final String  REPORTS_COLLECTION = "reports";

    public FirebaseHelper() {
        this.context = AppController.getContext();
    }

    public void uploadPhoto(Object photoFile, Photo photoType, OnPhotoUploadListener uploadListener) {
        final StorageReference photoPath = getStoragePath(photoType);

        UploadTask task;
        if(photoFile instanceof byte[]) task = photoPath.putBytes((byte[]) photoFile);
        else if(photoFile instanceof Uri) task = photoPath.putFile((Uri) photoFile);
        else return;

        task.addOnSuccessListener(taskSnapshot ->
                photoPath.getDownloadUrl().addOnSuccessListener(uri -> {
                    String link = uri.toString();
                    if (uploadListener != null) uploadListener.onSuccess(link);
            }))
            .addOnFailureListener(ex -> {
                Log.e(Constants.TAG, "Uploading " + photoType.toString() + " Photo Failure, Reason: " + ex.getMessage());
                if (uploadListener != null) uploadListener.onFailure();
                ex.printStackTrace();
            })
            .addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if (uploadListener != null) uploadListener.onProgress(progress);
            });
    }

    public void setDocumentData(Task<Void> task, OnFirebaseUpdateListener updateListener) {
        task.addOnSuccessListener(aVoid -> {
                if (updateListener != null) updateListener.onSuccess();
            })
            .addOnFailureListener(ex -> {
                Log.e(Constants.TAG, "Task Failure, Reason: " + ex.getMessage());
                if (updateListener != null) updateListener.onFailure();
            })
            .addOnCanceledListener(() -> {
                Log.e(Constants.TAG, "Task Cancelled, Reason: " + task.getException());
                if (updateListener != null) updateListener.onCancelled();
            });
    }

    public void setCollectionData(Task<DocumentReference> task, OnFirebaseUpdateListener updateListener) {
        task.addOnSuccessListener(aVoid -> {
                if (updateListener != null) updateListener.onSuccess();
            })
            .addOnFailureListener(ex -> {
                Log.e(Constants.TAG, "Task Failure, Reason: " + ex.getMessage());
                if (updateListener != null) updateListener.onFailure();
            })
            .addOnCanceledListener(() -> {
                Log.e(Constants.TAG, "Task Cancelled, Reason: " + task.getException());
                if (updateListener != null) updateListener.onCancelled();
            });
    }

    public FirebaseUser getFirebaseUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent intent = new Intent(context, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return null;
        }
        else return user;
    }

    public DocumentReference userReference(){
        String userId = Objects.requireNonNull(getFirebaseUser()).getUid();
        return FirebaseFirestore.getInstance().collection(USERS_TABLE).document(userId);
    }

    private StorageReference getStoragePath(Photo photo){
        String userId = Objects.requireNonNull(getFirebaseUser()).getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference(Constants.roleMode.getTitle() + "/" + userId + "/");

        switch (photo) {
            case PROFILE:
            case NID:
                return reference.child(photo.getPath());

            case REPORT:
            default:
                return reference.child(photo.getPath()).child(UUID.randomUUID().toString());
        }
    }

    public interface OnPhotoUploadListener {
        void onSuccess(String photoLink);
        void onFailure();
        void onProgress(double progress);
    }

    public interface OnFirebaseUpdateListener {
        void onSuccess();
        void onFailure();
        void onCancelled();
    }
}
