package de.koechig.share.model;

import android.support.annotation.Nullable;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class Item extends DB_Item {
    private String creator;
    private String name;
    private String description;

    public Item(String name,
                @Nullable String description,
                User creator) {
        this.name = name;
        this.description = description;
        this.creator = creator.getKey();
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
}
