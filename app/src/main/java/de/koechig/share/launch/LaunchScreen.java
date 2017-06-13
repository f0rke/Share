package de.koechig.share.launch;

import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;

/**
 * Created by moritzkochig on 6/13/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public interface LaunchScreen {
    interface View extends AbstractView {
        void showHomeScreen();

        void showLoginScreen();
    }

    interface Presenter extends AbstractPresenter<View> {
    }
}
