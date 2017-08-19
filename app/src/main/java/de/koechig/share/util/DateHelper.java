package de.koechig.share.util;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.koechig.share.R;

/**
 * Created by Mumpi_000 on 02.08.2017.
 */

public class DateHelper {

    private final Context mContext;
    private final SimpleDateFormat mDatabaseFormat;

    public DateHelper(Context c) {
        this.mContext = c;
        this.mDatabaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.mDatabaseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public String toSmartDisplayTime(String date) {
        Calendar that = Calendar.getInstance();
        try {
            that.setTime(mDatabaseFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar current = Calendar.getInstance();

        if (current.get(Calendar.DAY_OF_YEAR) == that.get(Calendar.DAY_OF_YEAR)
                && current.get(Calendar.YEAR) == that.get(Calendar.YEAR)) {
            //This is the same day
            //TODO
        } else {
            int today = current.get(Calendar.DAY_OF_YEAR);
            int thatDay = that.get(Calendar.DAY_OF_YEAR);
            if (thatDay == today - 1) {
                return mContext.getString(R.string.yesterday);
            } else if (thatDay == today + 1) {
                return mContext.getString(R.string.tomorrow);
            } else if (Math.abs(thatDay - today) > 1) {
                DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
                return format.format(that.getTime());
            }
        }
        //default
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(that.getTime());
    }
}
