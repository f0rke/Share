package de.koechig.share.model;

import java.util.List;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class User extends DB_Item {
    private String firstName;
    private String lastName;
    private String email;
    private List<String> channels;

    public User() {
    }

    public User(String email, String key) {
        this.email = email;
        this.key = key;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<String> getChannels() {
        return channels;
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

                        && ((this.channels == null && user.channels == null) || (this.channels != null && this.channels.containsAll(user.channels) && user.channels != null && user.channels.containsAll(this.channels)))

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
