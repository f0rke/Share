package de.koechig.share.model;

import java.util.List;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String uid;
    private List<String> groups;

    public User() {
    }

    public User(String email, String uid) {
        this.email = email;
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<String> getGroups() {
        return groups;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean equalsAllAttributes(User user) {
        boolean groupsEquals = true;

        return
                this.uid.equals(user.uid)
                        && this.email.equals(user.email)
                        && this.groups.containsAll(user.groups) && user.groups.containsAll(this.groups)
                        && this.firstName.equals(user.firstName)
                        && this.lastName.equals(user.lastName);
    }
}
