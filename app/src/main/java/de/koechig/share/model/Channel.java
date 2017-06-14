package de.koechig.share.model;

import java.util.List;

/**
 * Created by Mumpi_000 on 08.06.2017.
 */

public class Channel extends DB_Item {
    private List<String> members;
    private String lastEntry;
    private String lastContributor;
    private long lastEntryTimestamp;
    private String name;

    public Channel() {
    }

    public List<String> getMembers() {
        return members;
    }

    public String getLastEntry() {
        return lastEntry;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public long getLastEntryTimestamp() {
        return lastEntryTimestamp;
    }

    public String getName() {
        return name;
    }
}
