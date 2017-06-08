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
    private List<String> rooms;

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

    public List<String> getRooms() {
        return rooms;
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
        return
                user != null

                        && ((this.uid == null && user.uid == null) || (this.uid != null && this.uid.equals(user.uid)))

                        && ((this.email == null && user.email == null) || (this.email != null && this.email.equals(user.email)))

                        && ((this.rooms == null && user.rooms == null) || (this.rooms != null && this.rooms.containsAll(user.rooms) && user.rooms != null && user.rooms.containsAll(this.rooms)))

                        && ((this.firstName == null && user.firstName == null) || (this.firstName != null && this.firstName.equals(user.firstName)))

                        && ((this.lastName == null && user.lastName == null) || (this.lastName != null && this.lastName.equals(user.lastName)));
    }
}
