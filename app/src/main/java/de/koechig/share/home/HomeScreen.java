package de.koechig.share.home;

import android.net.Uri;

import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public interface HomeScreen {
    interface View extends AbstractView {
        void displayLoginAction();

        void displayLogoutAction();

        void setUsername(String username);

        void setUserMail(String mail);

        void setUserImage(Uri photoUrl);

        void showLoginScreen();

        void showLoginSuccessfulMessage();
    }

    interface Presenter extends AbstractPresenter<View> {

        void onLoginClicked();

        void onLogoutClicked();

        void onLoggedIn();
    }
}
