package de.koechig.share.channels;

import java.util.List;

import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;
import de.koechig.share.channels.ChannelsScreen.View;
import de.koechig.share.model.Channel;
import de.koechig.share.model.User;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class ChannelsPresenter implements ChannelsScreen.Presenter {

    //Member variables
    private View mView;
    private final AuthController mAuth;
    private final DBController mDb;
    private final AuthController.UserListener mUserListener = new AuthController.UserListener() {
        @Override
        public void onUpdated() {
            updateSideMenu();
            loadData();
        }
    };
    private final ResourceProvider mProvider;
    private boolean mSingleChannelShortCutUsed = false;
    private DBController.RetrieveCallback<List<Channel>> mRecurringUpdateListener = new DBController.RetrieveCallback<List<Channel>>() {
        @Override
        public void onSuccess(List<Channel> result) {
            if (mView != null) {
                mView.showChannels(result);
            }
        }

        @Override
        public void onError(Exception e) {
            if (mView != null) {
                mView.showError(e);
            }
        }
    };

    public ChannelsPresenter(AuthController auth, DBController db, ResourceProvider provider) {
        mAuth = auth;
        mDb = db;
        mProvider = provider;
    }

    @Override
    public void onLoginClicked() {
        if (mView != null) {
            mView.showLoginScreen();
        }
    }

    @Override
    public void onLogoutClicked() {
        mAuth.logout();
        updateSideMenu();
    }

    @Override
    public void onLoggedIn() {
        if (mView != null) {
            mView.showLoginSuccessfulMessage();
        }
    }

    @Override
    public void onChannelClicked(Channel channel) {
        if (mView != null) {
            mView.showItemsScreen(channel);
        }
    }

    @Override
    public void bindView(View view) {
        mView = view;
        update();
        mAuth.addListener(mUserListener);
        if (mAuth.getCurrentUser() != null) {
            mDb.registerForFutureChannelListChanges(mAuth.getCurrentUser(), mRecurringUpdateListener);
        }
    }

    @Override
    public void update() {
        updateSideMenu();
        handleAuthState();
    }

    @Override
    public void unbindView() {
        if (mAuth.getCurrentUser() != null) {
            mDb.unregisterFromChannelListChanges(mAuth.getCurrentUser(), mRecurringUpdateListener);
        }
        mAuth.removeListener(mUserListener);
        mView = null;
    }

    private void handleAuthState() {
        User user = mAuth.getCurrentUser();
        if (user != null) {
            loadData();
        } else {
            if (mView != null) {
                mView.showLoginScreen();
            }
        }
    }

    private void loadData() {
        User user = mAuth.getCurrentUser();
        if (user != null) {
            mDb.fetchChannelsForUser(mAuth.getCurrentUser(), new DBController.RetrieveCallback<List<Channel>>() {
                @Override
                public void onSuccess(List<Channel> result) {
                    if (mView != null) {
                        mView.showChannels(result);
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (mView != null) {
                        mView.showError(e);
                    }
                }
            });
        } else {
            if (mView != null) {
                mView.showChannels(null);
            }
        }
    }

    private void updateSideMenu() {
        if (mView != null) {
            User user = mAuth.getCurrentUser();
            if (user != null) {
                String username = "";
                if (user.getFirstName() != null) {
                    username = username.concat(user.getFirstName());
                }
                if (user.getFirstName() != null) {
                    username = username.concat(" " + user.getLastName());
                }
                mView.setUsername(username);
                String userMail = user.getEmail() != null ? user.getEmail() : mProvider.getNoUserMailErrorString();
                mView.setUserMail(userMail);
            } else {
                mView.setUsername(mProvider.getNotLoggedInUsername());
                mView.setUserMail(mProvider.getNotLoggedInUserMail());
            }
            if (user != null) {
                mView.displayLogoutAction();
            } else {
                mView.displayLoginAction();
            }
        }
    }

    public interface ResourceProvider {
        String getNoUserMailErrorString();

        String getNotLoggedInUsername();

        String getNotLoggedInUserMail();
    }
}
