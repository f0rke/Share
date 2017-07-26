package de.koechig.share.control;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.koechig.share.model.Channel;
import de.koechig.share.model.Item;
import de.koechig.share.model.User;
import de.koechig.share.util.StringHelper;

/**
 * Created by moritzkochig on 6/9/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class DBController {

    private DatabaseReference mDatabase;
    private StringHelper mStringHelper;
    private final DatabaseStringsProvider mProvider;

    private static final String USERS_NODE = "users";
    private static final String MEMBERS_NODE = "members";
    private static final String CHANNELS_NODE = "channels";
    private static final String ITEMS_NODE = "items";

    public DBController(DatabaseReference mDatabase, StringHelper stringHelper, DatabaseStringsProvider provider) {
        this.mDatabase = mDatabase;
        this.mStringHelper = stringHelper;
        this.mProvider = provider;
    }

    //<editor-fold desc="# Users #">
    public void fetchUser(String key, final RetrieveCallback<User> callback) {
        mDatabase.child(USERS_NODE).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User tmpUser = dataSnapshot.getValue(User.class);
                callback.onSuccess(tmpUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void unsubscribeFromUserChanges(String key, ValueEventListener listener) {
        mDatabase.child(USERS_NODE).child(key).removeEventListener(listener);
    }

    public void subscribeToUserChanges(String key, ValueEventListener listener) {
        mDatabase.child(USERS_NODE).child(key).addValueEventListener(listener);
    }

    public void createUser(String mail, final RetrieveCallback<User> callback) {
        String key = mStringHelper.getIdFromMail(mail);
        if (key != null) {
            final User user = new User(mail, key);
            String first = mStringHelper.getFirstNameFromMail(mail);
            if (first != null) {
                user.setFirstName(first);
            }
            String last = mStringHelper.getLastNameFromMail(mail);
            if (last != null) {
                user.setLastName(last);
            }
            //TODO check if user already exists
            mDatabase.child(USERS_NODE).child(key).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        callback.onSuccess(user);
                    } else {
                        callback.onError(task.getException());
                    }
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold desc="# Channels #">
    public void fetchChannels(final RetrieveCallback<List<Channel>> callback) {
        mDatabase.child(CHANNELS_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                List<Channel> channels;
                if (dataSnapshot.getValue() != null) {
                    channels = new ArrayList<Channel>() {{
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Channel channel = child.getValue(Channel.class);
                            add(channel);
                        }
                    }};
                } else {
                    channels = new ArrayList<>(0);
                }
                callback.onSuccess(channels);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void createChannel(
            final String name,
            final List<String> memberNames,
            final ActionCallback callback) {
        final Channel channel = new Channel(name, memberNames);
        mDatabase.child(CHANNELS_NODE).child(channel.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String message = String.format(mProvider.getChannelAlreadyExistingMessage(), name);
                    callback.onFailed(new EntryAlreadyExistsException(message));
                } else {
                    //Start transaction, create channel, link users to channel
                    Map<String, Object> update = new HashMap<>();
                    update.put(CHANNELS_NODE + "/" + channel.getKey(), channel);
                    for (String memberName : memberNames) {
                        update.put(MEMBERS_NODE + "/" + channel.getKey() + "/" + memberName, true);
                        update.put(USERS_NODE + "/" + memberName + "/channels/" + channel.getKey(), true);
                    }
                    mDatabase.updateChildren(update, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                callback.onFailed(databaseError.toException());
                            } else {
                                callback.onSucceeded();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailed(databaseError.toException());
            }
        });
    }

    public void subscribeToChannel(Channel channel, User newMember) {

    }

    public void fetchChannel(String mChannelKey, final RetrieveCallback<Channel> callback) {
        mDatabase.child(CHANNELS_NODE).child(mChannelKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onSuccess(dataSnapshot.getValue(Channel.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="# Items #">
    public void fetchItems(Channel channel, final RetrieveCallback<List<Item>> callback) {
        mDatabase.child(ITEMS_NODE).child(channel.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                List<Item> items;
                if (dataSnapshot.getValue() != null) {
                    items = new ArrayList<Item>() {{
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Item item = child.getValue(Item.class);
                            add(item);
                        }
                    }};
                } else {
                    items = new ArrayList<>(0);
                }
                callback.onSuccess(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void submitNewItemToChannel(
            @NonNull final Item item,
            @NonNull final String channelKey,
            @NonNull final User creator,
            @NonNull final ActionCallback callback) {
        mDatabase.child(ITEMS_NODE).child(channelKey).child(item.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String message = String.format(mProvider.getItemAlreadyExistingMessage(), item.getName());
                    callback.onFailed(new EntryAlreadyExistsException(message));
                } else {
                    //Start transaction, create channel, link users to channel
                    Map<String, Object> update = new HashMap<>();
                    update.put(CHANNELS_NODE + "/" + channelKey + "/lastEntryTimestamp", item.getCreationDate());
                    update.put(CHANNELS_NODE + "/" + channelKey + "/lastEntry", item.getKey());
                    update.put(CHANNELS_NODE + "/" + channelKey + "/lastContributor", creator.getKey());
                    update.put(ITEMS_NODE + "/" + channelKey + "/" + item.getKey(), item);
                    mDatabase.updateChildren(update, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                callback.onFailed(databaseError.toException());
                            } else {
//                                mDatabase.child(ITEMS_NODE).child(channelKey).orderByChild("creationDate");
                                callback.onSucceeded();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailed(databaseError.toException());
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="# Inner classes #">
    public interface RetrieveCallback<T> {
        void onSuccess(T result);

        void onError(Exception e);
    }

    public interface ActionCallback {
        void onSucceeded();

        void onFailed(Exception e);
    }

    public class EntryAlreadyExistsException extends Exception {

        public EntryAlreadyExistsException(String message) {
            super(message);
        }
    }

    public interface DatabaseStringsProvider {
        String getChannelAlreadyExistingMessage();

        String getItemAlreadyExistingMessage();
    }
    //</editor-fold>
}
