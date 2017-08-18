package de.koechig.share.control;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Iterables;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

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

    private static final String TAG = DBController.class.getSimpleName();

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

    public void createUser(final String uid, String mail, final RetrieveCallback<User> callback) {
//        final String key = mStringHelper.getIdFromMail(mail);
        if (uid != null && mail != null) {
            final User user = new User(uid, mail);
            String first = mStringHelper.getFirstNameFromMail(mail);
            if (first != null) {
                user.setFirstName(first);
            }
            String last = mStringHelper.getLastNameFromMail(mail);
            if (last != null) {
                user.setLastName(last);
            }
            mDatabase.child(USERS_NODE).child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                String message = String.format(
                                        mProvider.getUserAlreadyExistingMessage(),
                                        uid);
                                callback.onError(new EntryAlreadyExistsException(message));
                            } else {
                                mDatabase.child(USERS_NODE).child(uid).setValue(user).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
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

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            callback.onError(databaseError.toException());
                        }
                    });
        }
    }
    //</editor-fold>

    //<editor-fold desc="# Channels #">

    private Map<RetrieveCallback<List<Channel>>, ValueEventListener> mChannelListeners = new HashMap<>();

    public void registerForFutureChannelListChanges(final User user, @NonNull final RetrieveCallback<List<Channel>> listener) {
        ValueEventListener keyListener = new ValueEventListener() {
            boolean firstCall = true;

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (firstCall) {
                    firstCall = false;
                } else {
                    fetchChannelsForUser(user, listener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mChannelListeners.put(listener, keyListener);
        mDatabase.child(USERS_NODE).child(user.getUid()).child(CHANNELS_NODE).addValueEventListener(keyListener);
    }

    public void deregisterFromChannelListChanges(@NonNull final RetrieveCallback<List<Channel>> listener) {
        mChannelListeners.remove(listener);
    }

    public void fetchChannelsForUser(User user, final RetrieveCallback<List<Channel>> callback) {
        mDatabase.child(USERS_NODE + "/" + user.getUid() + "/" + CHANNELS_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    new AsyncTask<Void, Void, List<Channel>>() {
                        final CountDownLatch latch = new CountDownLatch(Iterables.size(dataSnapshot.getChildren()));

                        @Override
                        protected List<Channel> doInBackground(Void... voids) {
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            final List<Channel> channels = new ArrayList<>();
                            for (DataSnapshot channelIdData : children) {
                                String key = channelIdData.getKey();
                                mDatabase.child(CHANNELS_NODE + "/" + key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            Channel channel = dataSnapshot.getValue(Channel.class);
                                            channels.add(channel);
                                            latch.countDown();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        databaseError.toException().printStackTrace();
                                        latch.countDown();
                                    }
                                });
                            }
                            try {
                                latch.await();
                                return channels;
                            } catch (InterruptedException e) {
                                callback.onError(e);
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(List<Channel> channels) {
                            super.onPostExecute(channels);
                            if (channels != null) {
                                callback.onSuccess(channels);
                            }
                        }
                    }.execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void createChannel(
            final String name,
            final List<String> memberIds,
            final ActionCallback callback) {
        final String key = mDatabase.child(CHANNELS_NODE).push().getKey();
        final Channel channel = new Channel(name, memberIds, key);
        mDatabase.child(CHANNELS_NODE).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String message = String.format(mProvider.getChannelAlreadyExistingMessage(), name);
                    callback.onFailed(new EntryAlreadyExistsException(message));
                } else {
                    //Start transaction, create channel, link users to channel
                    Map<String, Object> update = new HashMap<>();
                    update.put(CHANNELS_NODE + "/" + key, channel);
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

    public void listenForItems(Channel mChannel, RetrieveCallback<List<Item>> mItemsFetchCallback) {
        mDatabase.child(ITEMS_NODE).child(mChannel.getKey()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot newChild, String previousChildKey) {
                Log.d(TAG,
                        ITEMS_NODE + ".ChildEventListener: "
                                + "onChildAdded(DataSnapshot newChild("
                                + newChild.toString() + ")"
                                + ", String previousChildKey("
                                + previousChildKey + ")"
                );
            }

            @Override
            public void onChildChanged(DataSnapshot changedChild, String previousChildKey) {
                Log.d(TAG,
                        ITEMS_NODE + ".ChildEventListener: "
                                + "onChildChanged(DataSnapshot changedChild("
                                + changedChild.toString() + ")"
                                + ", String previousChildKey("
                                + previousChildKey + ")"
                );
            }

            @Override
            public void onChildRemoved(DataSnapshot removed) {
                Log.d(TAG,
                        ITEMS_NODE + ".ChildEventListener: "
                                + "onChildRemoved(DataSnapshot removed("
                                + removed.toString() + ")"
                );
            }

            @Override
            public void onChildMoved(DataSnapshot movedChild, String previousChildKey) {
                Log.d(TAG,
                        ITEMS_NODE + ".ChildEventListener: "
                                + "onChildMoved(DataSnapshot movedChild("
                                + movedChild.toString() + ")"
                                + ", String previousChildKey("
                                + previousChildKey + ")"
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,
                        ITEMS_NODE + ".ChildEventListener: "
                                + "onCancelled(DatabaseError databaseError("
                                + databaseError.toString() + ")"
                );
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
                    update.put(CHANNELS_NODE + "/" + channelKey + "/lastContributorFirstName", creator.getFirstName());
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

    //<editor-fold desc="# Push #">
    public void updateUserEntry(final User user) {
        //Fire and forget
        mDatabase.child(USERS_NODE).child(user.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mDatabase.updateChildren(new HashMap<String, Object>() {{
                        put(USERS_NODE + "/" + user.getUid(), user);
                    }});
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ignore
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

        String getUserAlreadyExistingMessage();
    }
    //</editor-fold>
}
