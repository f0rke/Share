package de.koechig.share.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class User extends DB_Item {
    private String firstName;
    private String lastName;
    private String email;
    private Map<String, Boolean> channels;

    public User() {
    }

    public User(String email, String key) {
        this.email = email;
        this.key = key;
    }

    public Map<String, Boolean> getChannels() {
        return channels;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<String> getChannelList() {
        List<String> arr = new ArrayList<>();
        for (String channel : channels.keySet()) {
            arr.add(channel);
        }
        return arr;
    }

    public String getEmail() {
        return email;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean equalsAllAttributes(User user) {
        return
                user != null

                        && ((this.key == null && user.key == null) || (this.key != null && this.key.equals(user.key)))

                        && ((this.email == null && user.email == null) || (this.email != null && this.email.equals(user.email)))

                        && (
                        (this.channels == null && user.channels == null)
                                || ((this.channels != null && user.channels != null)
                                && this.channels.keySet().containsAll(user.channels.keySet())
                                && user.channels.keySet().containsAll(this.channels.keySet()))
                )

                        && ((this.firstName == null && user.firstName == null) || (this.firstName != null && this.firstName.equals(user.firstName)))

                        && ((this.lastName == null && user.lastName == null) || (this.lastName != null && this.lastName.equals(user.lastName)));
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
