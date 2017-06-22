package de.koechig.share.model;

import android.support.annotation.Nullable;

import de.koechig.share.util.StringHelper;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class Item extends DB_Item {
    private String creator;
    private long creationDate;
    private String name;
    private String description;

    public Item(String name,
                @Nullable String description,
                User creator) {
        StringHelper helper = new StringHelper();
        this.key = helper.convertToId(name);
        this.name = name;
        this.description = description;
        this.creator = creator.getKey();
        this.creationDate = System.currentTimeMillis();
    }

    public Item() {
    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getCreationDate() {
        return creationDate;
    }
}
