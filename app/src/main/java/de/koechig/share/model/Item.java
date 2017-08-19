package de.koechig.share.model;

import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import de.koechig.share.util.StringHelper;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class Item extends DB_Item {
    private String creator;
    private String creationDate;
    private String name;
    private String creatorFirstName;

    public Item(String name,
                User creator) {
        this.name = name;
        this.creator = creator.getKey();
        this.creatorFirstName = creator.getFirstName();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.creationDate = format.format(Calendar.getInstance().getTime());
    }

    public Item() {
    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getCreatorFirstName() {
        return creatorFirstName;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
