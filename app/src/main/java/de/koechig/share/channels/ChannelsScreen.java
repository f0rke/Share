package de.koechig.share.channels;

import android.net.Uri;

import java.util.List;

import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;
import de.koechig.share.model.Channel;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public interface ChannelsScreen {
    interface View extends AbstractView {
        void displayLoginAction();

        void displayLogoutAction();

        void setUsername(String username);

        void setUserMail(String mail);

        void setUserImage(Uri photoUrl);

        void showLoginScreen();

        void showLoginSuccessfulMessage();

        void showChannels(List<Channel> result);

        void showItemsScreen(Channel channel);
    }

    interface Presenter extends AbstractPresenter<View> {
        void onLoginClicked();

        void onLogoutClicked();

        void onLoggedIn();

        void onChannelClicked(Channel item);
    }
}
