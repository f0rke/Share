package de.koechig.share.model;

import com.google.firebase.database.Exclude;

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
    private boolean deleted;
    private Map<String, Boolean> channels;
    private String currentPushToken;

    public User() {
    }

    public User(String uid, String email) {
        this.key = uid;
        this.email = email;
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

    @Exclude
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

    public String getCurrentPushToken() {
        return currentPushToken;
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
                                && user.channels.keySet().containsAll(this.channels.keySet())))

                        && ((this.firstName == null && user.firstName == null) || (this.firstName != null && this.firstName.equals(user.firstName)))

                        && ((this.lastName == null && user.lastName == null) || (this.lastName != null && this.lastName.equals(user.lastName)));
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setPushToken(String pushToken) {
        this.currentPushToken = pushToken;
    }

    public String getUid() {
        return key;
    }

    public void setUid(String uid) {
        this.key = uid;
    }
}
