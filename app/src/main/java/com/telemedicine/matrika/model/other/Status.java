package com.telemedicine.matrika.model.other;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class Status implements Serializable {

    public static String STATUS = "status";
    public static String DATE = "date";

    private boolean     status;
    @ServerTimestamp
    private Date        date;

    public Status() {}

    public Status(boolean status, Date date) {
        this.status = status;
        this.date = date;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
