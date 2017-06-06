package de.koechig.share.login;

import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;

/**
 * Created by Mumpi_000 on 04.05.2017.
 */

public interface LoginScreen {
    int REQUEST_LOGIN = 1;
    int RESULT_LOGGED_IN = 100;

    interface View extends AbstractView {

        //Actions
        void showLoginAction();

        void showResetPasswordAction();

        //Messages
        void showResetPasswordSuccess();

        //Errors
        void showInvalidMailFormatError();

        void showUsernameOrPasswordWrong();

        void showNoPasswordError();

        void showNoSuchUserError();

        void showFatalError(Exception e);

        void hideError();

        //Progress
        void showProgress();

        void hideProgress();

        //Close
        void close();

        void closeOnLoginSuccess();

        void showMailClientChooser();

        void showOpenMailFailedMessage();
    }

    interface Presenter extends AbstractPresenter<View> {
        void onLoginClicked(String email, String password);

        void onForgotPasswordClicked();

        void onCancelResetPasswordClicked();

        void onResetPasswordClicked(String mail);

        void onOpenMailClicked();

        void onOpenMailFailed();
    }
}
