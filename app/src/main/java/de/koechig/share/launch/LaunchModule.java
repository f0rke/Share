package de.koechig.share.launch;

import dagger.Module;
import dagger.Provides;
import de.koechig.share.control.AuthController;

/**
 * Created by Mumpi_000 on 28.06.2017.
 */

@Module
public class LaunchModule {
    @Provides
    public LaunchScreen.Presenter providePrestenter(AuthController mAuth) {
        return new LaunchPresenter(mAuth);
    }
}
