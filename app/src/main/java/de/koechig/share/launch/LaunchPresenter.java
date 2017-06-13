package de.koechig.share.launch;

import de.koechig.share.control.AuthController;
import de.koechig.share.model.User;

/**
 * Created by moritzkochig on 6/13/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

class LaunchPresenter implements LaunchScreen.Presenter {
    private LaunchScreen.View mView;
    private final AuthController mAuth;

    public LaunchPresenter(AuthController authController) {
        mAuth = authController;
    }

    @Override
    public void bindView(LaunchScreen.View view) {
        this.mView = view;
        update();
    }

    @Override
    public void update() {
        User user = mAuth.getCurrentUser();
        if (user != null) {
            if (mView != null) {
                mView.showHomeScreen();
            }
        } else {
            if (mView != null) {
                mView.showLoginScreen();
            }
        }
    }

    @Override
    public void unbindView() {
        this.mView = null;
    }
}
