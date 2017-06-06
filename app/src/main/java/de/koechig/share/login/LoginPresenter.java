package de.koechig.share.login;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import de.koechig.share.control.AuthController;
import de.koechig.share.util.StringHelper;

/**
 * Created by Mumpi_000 on 04.05.2017.
 */

class LoginPresenter implements LoginScreen.Presenter {
    private final AuthController mAuth;
    private final StringHelper mStringHelper;
    private LoginScreen.View mView;

    public LoginPresenter(StringHelper matcher, AuthController auth) {
        this.mStringHelper = matcher;
        this.mAuth = auth;
    }

    //<editor-fold desc="# View #">
    @Override
    public void bindView(LoginScreen.View view) {
        this.mView = view;
        update();
    }

    @Override
    public void update() {

    }

    @Override
    public void unbindView() {
        this.mView = null;
    }
    //</editor-fold>

    //<editor-fold desc="# User interaction #">
    @Override
    public void onLoginClicked(String mail, String password) {
        if (mView != null) {
            mView.hideError();
        }
        if (mStringHelper.isMail(mail) && mStringHelper.isValidPassword(password)) {
            if (mView != null) {
                mView.showProgress();
            }
            mAuth.login(mail, password, new AuthController.Callback() {
                @Override
                public void onSuccess() {
                    if (mView != null) {
                        mView.hideProgress();
                        mView.closeOnLoginSuccess();
                    }
                }

                @Override
                public void onFailure(Exception reason) {
                    if (mView != null) {
                        mView.hideProgress();
                        if (reason == null) {
                            mView.showFatalError(null);
                        } else if (reason instanceof FirebaseAuthInvalidUserException) {
                            mView.showNoSuchUserError();
                        } else if (reason instanceof FirebaseAuthInvalidCredentialsException) {
                            mView.showUsernameOrPasswordWrong();
                        } else {
                            mView.showFatalError(reason);
                        }
                    }
                }
            });
        } else {
            if (mView != null) {
                if (!mStringHelper.isMail(mail)) {
                    mView.showInvalidMailFormatError();
                } else if (!mStringHelper.isValidPassword(password)) {
                    mView.showNoPasswordError();
                }
            }
        }
    }

    @Override
    public void onForgotPasswordClicked() {
        if (mView != null) {
            mView.showResetPasswordAction();
        }
    }

    @Override
    public void onCancelResetPasswordClicked() {
        if (mView != null) {
            mView.hideError();
            mView.showLoginAction();
        }
    }

    @Override
    public void onResetPasswordClicked(String mail) {
        if (mView != null) {
            mView.hideError();
            mView.showProgress();
        }
        try {
            mAuth.sendPasswordResetMail(mail, new AuthController.Callback() {
                @Override
                public void onSuccess() {
                    onSendPasswordResetEmailSuccess();
                }

                @Override
                public void onFailure(Exception reason) {
                    onSendPasswordResetEmailFail(reason);
                }
            });
        } catch (IllegalArgumentException e) {
            onSendPasswordResetEmailFail(e);
        }
    }

    @Override
    public void onOpenMailClicked() {
        if (mView != null) {
            mView.showMailClientChooser();
        }
    }
    //</editor-fold>

    //<editor-fold desc="# Result handling #">
    @Override
    public void onOpenMailFailed() {
        if (mView != null) {
            mView.showOpenMailFailedMessage();
        }
    }

    private void onSendPasswordResetEmailFail(Exception exception) {
        if (mView != null) {
            mView.hideProgress();
            mView.showFatalError(exception);
        }
    }

    private void onSendPasswordResetEmailSuccess() {
        if (mView != null) {
            mView.hideProgress();
            mView.showResetPasswordSuccess();
            mView.showLoginAction();
        }
    }
    //</editor-fold>
}
