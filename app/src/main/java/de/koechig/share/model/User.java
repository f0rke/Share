package de.koechig.share.model;

import java.util.List;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class User extends DB_Item {
    private String firstName;
    private String lastName;
    private String email;
    private List<String> rooms;

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

    public List<String> getRooms() {
        return rooms;
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

                        && ((this.rooms == null && user.rooms == null) || (this.rooms != null && this.rooms.containsAll(user.rooms) && user.rooms != null && user.rooms.containsAll(this.rooms)))

                        && ((this.firstName == null && user.firstName == null) || (this.firstName != null && this.firstName.equals(user.firstName)))

                        && ((this.lastName == null && user.lastName == null) || (this.lastName != null && this.lastName.equals(user.lastName)));
    }
}
