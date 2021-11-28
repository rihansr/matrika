package com.telemedicine.matrika.util.extensions;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DateExtensions {

    private Context mContext;
    private long    longTime;
    private String  date;
    private String  time;

    public DateExtensions(String date) {
        this.mContext = AppController.getContext();
        this.date = date;
    }

    public DateExtensions(long longTime) {
        this.mContext = AppController.getContext();
        this.longTime = longTime;
    }

    public static long currentTime(){
        return System.currentTimeMillis();
    }

    public long longFormat() {
        /**
         * new DateExtensions("8 August 2020", "02:30 AM").longFormat()
         **/

        SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy,hh:mm aa", Locale.getDefault());

        long totalTime = 0;

        try {
            if(date == null || time == null) return 0;
            Date d = format.parse(date + "," + time);
            long loadTime = d != null ? d.getTime() : 0;
            long currentTime = System.currentTimeMillis();

            if(currentTime < loadTime){
                totalTime = loadTime;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return totalTime;
    }

    public long getBirthDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        long finalTime = 0;

        try {
            if(date == null) return 0;
            Date d = format.parse(date);
            finalTime = d != null ? d.getTime() : 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return finalTime;
    }

    public String dateFormat() {
        return new SimpleDateFormat("d MMM", Locale.getDefault()).format(longTime);
    }

    public String defaultDateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(longTime);
    }

    public String defaultTimeFormat() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(longTime);
    }

    public String defaultDateTimeFormat() {
        return new SimpleDateFormat("d/M/yy, h:mm a", Locale.getDefault()).format(longTime);
    }

    public Integer getAge() {
        if(date == null) return 0;
        int age = 0;
        try {
            Date birthDate = new Date();
            Date currentDate = new Date(System.currentTimeMillis());

            try {
                birthDate = new SimpleDateFormat("MM/DD/YYYY", Locale.getDefault()).parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (birthDate != null) {
                DateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                int d1 = Integer.parseInt(formatter.format(birthDate));
                int d2 = Integer.parseInt(formatter.format(currentDate));
                age = (d2 - d1) / 10000;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return age == 0 ? null : age;
    }

    public static void setDatePicker(final TextView date_Input) {
        final DatePicker datePicker = new DatePicker(AppController.getActivity());
        final int currentDay = datePicker.getDayOfMonth();
        final int currentMonth = datePicker.getMonth();
        final int currentYear = datePicker.getYear();

        @SuppressLint("SetTextI18n")
        DatePickerDialog datePickerDialog = new DatePickerDialog(AppController.getActivity(), (datePicker1, year, monthOfYear, dayOfMonth) ->
                date_Input.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), currentYear, currentMonth, currentDay);

        /**
         * Set Old Date
         **/
        if (!Objects.requireNonNull(date_Input.getText()).toString().isEmpty()) {
            String[] oldDate = Objects.requireNonNull(date_Input.getText()).toString().split("/");
            int d = Integer.parseInt(oldDate[0].trim());
            int m = Integer.parseInt(oldDate[1].trim()) - 1;
            int y = Integer.parseInt(oldDate[2].trim());

            datePickerDialog.updateDate(y, m, d);
        }

        Calendar minDate = new GregorianCalendar(currentYear-125, currentMonth, currentDay);
        Calendar maxDate = new GregorianCalendar(currentYear-12, currentMonth, currentDay);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private final int SECOND_MILLIS = 1000;
    private final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public String getTimeAgo() {
        if (longTime < 1000000000000L) {
            longTime *= 1000;
        }

        long now = System.currentTimeMillis();
        if (longTime > now || longTime <= 0) {
            return null;
        }

        final long diff = now - longTime;
        if (diff < MINUTE_MILLIS) {
            return "Active now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Active 1 minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "Active" + " " + (diff / MINUTE_MILLIS) + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "Active 1 hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "Active" + " " + (diff / HOUR_MILLIS) + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Active yesterday";
        } else {
            return "Active" + " " + (diff / DAY_MILLIS) + " day ago";
        }
    }
}
