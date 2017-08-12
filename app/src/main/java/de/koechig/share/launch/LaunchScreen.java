package de.koechig.share.launch;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;
import de.koechig.share.control.AuthController;

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

    /**
     * Created by Mumpi_000 on 28.06.2017.
     */

    @Subcomponent(modules = LaunchModule.class)
    interface LaunchSubComponent {
        void inject(LaunchActivity obj);
    }

    /**
     * Created by Mumpi_000 on 28.06.2017.
     */

    @Module
    class LaunchModule {
        @Provides
        public Presenter providePrestenter(AuthController mAuth) {
            return new LaunchPresenter(mAuth);
        }
    }
}
