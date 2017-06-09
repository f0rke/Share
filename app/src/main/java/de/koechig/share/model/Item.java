package de.koechig.share.model;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class Item {
    private String creator;
    private String name;
    private String description;

    public Item(String name, String description, User creator) {
        this.name = name;
        this.description = description;
        this.creator = creator.getEmail();
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