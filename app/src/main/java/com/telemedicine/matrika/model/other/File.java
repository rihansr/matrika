package com.telemedicine.matrika.model.other;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class File implements Serializable {

    public static String  ID = "id";
    public static String  TITLE = "title";
    public static String  DESC = "description";
    public static String  TYPE = "fileType";
    public static String  SIZE = "size";
    public static String  PATH = "path";
    public static String  SENT_AT = "sentAt";

    private String  id;
    private String  title;
    private String  description;
    private String  fileType;
    private Long    size;
    private String  path;
    @ServerTimestamp
    private Date    sentAt;

    public File() {}

    public File(String id, String title, String fileType, Long size, String path) {
        this.id = id;
        this.title = title;
        this.fileType = fileType;
        this.size = size;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String extension) {
        this.fileType = extension;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getPath() {
        return (path == null) ? null : path.replaceAll(",",".");
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void sentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}
