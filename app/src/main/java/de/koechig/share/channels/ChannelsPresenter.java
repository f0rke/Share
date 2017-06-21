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
    private DBController mDb;
    private AuthController.UserListener mUserListener = new AuthController.UserListener() {
        @Override
        public void onUpdated() {
            updateSideMenu();
        }
    };
    private HomeResourceProvider mProvider;

    public ChannelsPresenter(AuthController auth, DBController db, HomeResourceProvider provider) {
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
    public void onChannelClicked(Channel item) {
        if(mView!=null){
            mView.showItemsScreen(item);
        }
    }

    @Override
    public void bindView(View view) {
        mView = view;
        update();
        mAuth.addListener(mUserListener);
    }

    @Override
    public void update() {
        updateSideMenu();
        handleAuthState();
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
        mDb.fetchChannels(new DBController.RetrieveCallback<List<Channel>>() {
            @Override
            public void onSuccess(List<Channel> result) {
                mView.showChannels(result);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void updateSideMenu() {
        if (mView != null) {
            User user = mAuth.getCurrentUser();
            if (user != null) {
                String username = user.getFirstName() != null ? user.getFirstName() : "";
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

    @Override
    public void unbindView() {
        mAuth.removeListener(mUserListener);
        mView = null;
    }

    interface HomeResourceProvider {
        String getNoUserMailErrorString();

        String getNotLoggedInUsername();

        String getNotLoggedInUserMail();
    }
}
