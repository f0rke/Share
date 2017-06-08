package de.koechig.share.control;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.koechig.share.model.User;

/**
 * Created by Mumpi_000 on 06.06.2017.
 */

public class AuthController {
    private static final String USERS_KEY = "users";
    private static final String TAG = AuthController.class.getSimpleName();
    private final FirebaseAuth mAuth;
    private List<UserListener> listeners;
    private final FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            onUserAuthUpdate(firebaseAuth.getCurrentUser());
        }
    };
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private User mUser;
    private ValueEventListener mUserValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            if (!user.equalsAllAttributes(mUser)) {
                updateUser(user);
            }
            notifyListeners();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public AuthController(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
        this.mAuth.addAuthStateListener(mAuthStateListener);
        this.listeners = new ArrayList<>();
    }

    public void addListener(UserListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(UserListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public User getUser() {
        return mUser;
    }

    private void updateUser(User user) {
        if (mUser != null) {
            mDatabase.child(USERS_KEY).child(mUser.getUid()).removeEventListener(mUserValueListener);
        }
        mUser = user;
        if (mUser == null) {
            notifyListeners();
        } else {
            mDatabase.child(USERS_KEY).child(mUser.getUid()).addValueEventListener(mUserValueListener);
        }
    }

    private void notifyListeners() {
        Log.d(TAG, "notifyListeners");
        for (UserListener listener : listeners) {
            listener.onUpdated();
        }
    }

    private void onUserAuthUpdate(final FirebaseUser user) {
        if (user != null) {
            mDatabase.child(USERS_KEY).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User tmpUser = dataSnapshot.getValue(User.class);
                    if (tmpUser == null) {
                        tmpUser = createUserInDatabase(user.getUid(), user.getEmail());
                    } else if (tmpUser.getUid() == null || tmpUser.getUid().isEmpty()) {
                        tmpUser.setUid(user.getUid());
                    }
                    updateUser(tmpUser);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseCrash.logcat(Log.ERROR, USERS_KEY, databaseError.getMessage());
                    FirebaseCrash.report(databaseError.toException());
                }
            });
        } else {
            updateUser(null);
        }
    }

    public void login(final String mail, String password, final Callback callback) {
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String uid = task.getResult().getUser().getUid();
                            mDatabase.child(USERS_KEY).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user == null) {
                                        user = createUserInDatabase(uid, mail);
                                    } else if (user.getUid() == null || user.getUid().isEmpty()) {
                                        user.setUid(uid);
                                    }

                                    updateUser(user);
                                    callback.onSuccess();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    callback.onFailure(databaseError.toException());
                                }
                            });
                        } else

                        {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    private User createUserInDatabase(String uid, String mail) {
        User user = new User(mail, uid);
        mDatabase.child(USERS_KEY).child(uid).setValue(user);
        return user;
    }

    public void sendPasswordResetMail(String mail, final Callback callback) {
        mAuth.sendPasswordResetEmail(mail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void logout() {
        mAuth.signOut();
    }

    public interface Callback {
        void onSuccess();

        void onFailure(Exception reason);
    }

    public interface UserListener {
        void onUpdated();
    }
}
