package de.koechig.share.control;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.koechig.share.model.User;

/**
 * Created by moritzkochig on 6/9/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class DBController {

    private DatabaseReference mDatabase;

    private static final String USERS_NODE = "users";
    private static final String MEMBERS_NODE = "members";
    private static final String ROOMS_NODE = "rooms";
    private static final String ITEMS_NODE = "items";

    public DBController(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void getUser(String uid, ValueEventListener listener) {
        mDatabase.child(USERS_NODE).child(uid).addListenerForSingleValueEvent(listener);
    }

    public void removeUserListener(String uid, ValueEventListener listener) {
        mDatabase.child(USERS_NODE).child(uid).removeEventListener(listener);
    }

    public void addUserListener(String uid, ValueEventListener listener) {
        mDatabase.child(USERS_NODE).child(uid).addValueEventListener(listener);
    }

    public void setUser(User user) {
        mDatabase.child(USERS_NODE).child(user.getUid()).setValue(user);
    }
}
