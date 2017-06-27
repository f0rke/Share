package de.koechig.share.login;

import dagger.Module;
import dagger.Provides;
import de.koechig.share.control.AuthController;
import de.koechig.share.util.StringHelper;

/**
 * Created by Mumpi_000 on 28.06.2017.
 */
@Module
public class LoginModule {
    @Provides
    public LoginScreen.Presenter providePresenter(StringHelper matcher, AuthController auth) {
        return new LoginPresenter(matcher, auth);
    }
}
