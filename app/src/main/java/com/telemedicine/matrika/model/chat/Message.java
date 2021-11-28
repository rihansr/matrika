package com.telemedicine.matrika.model.chat;

import com.google.firebase.firestore.ServerTimestamp;
import com.telemedicine.matrika.model.other.File;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Message implements Serializable {

    public static String    SENT_AT = "sentAt";

    private String          id;
    private String          senderRole;
    private String          senderId;
    private String          message;
    private List<File>      files;
    private List<String>    deletedBy;
    @ServerTimestamp
    private Date            sentAt;

    public Message() {}

    public Message(String id, String senderRole, String senderId, String message, Date sentAt) {
        this.id = id;
        this.senderRole = senderRole;
        this.senderId = senderId;
        this.message = message;
        this.sentAt = sentAt;
    }

    public Message(String id, String senderRole, String senderId, List<File> files, Date sentAt) {
        this.id = id;
        this.senderRole = senderRole;
        this.senderId = senderId;
        this.files = files;
        this.sentAt = sentAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<String> getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(List<String> deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}
