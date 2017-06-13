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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.koechig.share.model.User;
import de.koechig.share.util.StringHelper;

/**
 * Created by Mumpi_000 on 06.06.2017.
 */

public class AuthController {
    private static final String TAG = AuthController.class.getSimpleName();
    private final FirebaseAuth mAuth;
    private List<UserListener> listeners;
    private final FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            onUserAuthUpdate(firebaseAuth.getCurrentUser());
        }
    };
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
    private DBController mDB;

    public AuthController(FirebaseAuth mAuth, DBController database) {
        this.mAuth = mAuth;
        this.mDB = database;
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

//    public void getUser(Callback callback){
//        mAuth.getCurrentUser()
//    }

    public User getCurrentUser() {
        return mUser;
    }

    private void updateUser(User user) {
        if (mUser != null) {
            mDB.removeUserListener(mUser.getKey(), mUserValueListener);
        }
        mUser = user;
        if (mUser == null) {
            notifyListeners();
        } else {
            mDB.addUserListener(mUser.getKey(), mUserValueListener);
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
            final String mail = user.getEmail();
            String key = new StringHelper().getIdFromMail(mail);
            mDB.getUser(key, new DBController.Callback<User>() {
                @Override
                public void onSuccess(User result) {
                    if (result == null) {
                        createUser(mail, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(Exception reason) {
                                updateUser(null);
                            }
                        });
                    } else {
                        updateUser(result);
                    }
                }

                @Override
                public void onError(Exception e) {
                    FirebaseCrash.logcat(Log.ERROR, "users", e.getMessage());
                    FirebaseCrash.report(e);
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
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDB.getUser(mail, new DBController.Callback<User>() {
                                @Override
                                public void onSuccess(User result) {
                                    if (result == null) {
                                        createUser(mail, callback);
                                    }
                                    updateUser(result);
                                    callback.onSuccess();
                                }

                                @Override
                                public void onError(Exception e) {
                                    callback.onFailure(e);
                                }
                            });
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    private void createUser(String mail, final Callback callback) {
        mDB.createUser(mail, new DBController.Callback<User>() {
            @Override
            public void onSuccess(User result) {
                updateUser(result);
                callback.onSuccess();
            }

            @Override
            public void onError(Exception e) {
                callback.onFailure(e);
            }
        });
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
