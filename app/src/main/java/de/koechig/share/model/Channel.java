package de.koechig.share.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.koechig.share.util.StringHelper;

/**
 * Created by Mumpi_000 on 08.06.2017.
 */

public class Channel extends DB_Item {
    private Map<String, Boolean> members;
    private String lastEntry;
    private String lastContributor;
    private Long lastEntryTimestamp;
    private String name;
    private String lastContributorFirstName;

    public Channel() {
    }

    public Channel(String name, List<String> members) {
        StringHelper helper = new StringHelper();
        this.key = helper.convertToId(name);
        this.name = name;
        this.members = new HashMap<>();
        for (String member : members) {
            this.members.put(member, true);
        }
    }

    public List<String> retrieveMemberList() {
        List<String> arr = new ArrayList<>();
        for (String member : members.keySet()) {
            arr.add(member);
        }
        return arr;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public String getLastEntry() {
        return lastEntry;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public Long getLastEntryTimestamp() {
        return lastEntryTimestamp;
    }

    public String getName() {
        return name;
    }

    public String getLastContributorFirstName() {
        return lastContributorFirstName;
    }
}
