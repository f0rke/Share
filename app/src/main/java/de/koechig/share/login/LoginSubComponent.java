package de.koechig.share.login;

import dagger.Subcomponent;

/**
 * Created by Mumpi_000 on 28.06.2017.
 */

@Subcomponent(modules = LoginModule.class)
public interface LoginSubComponent {
    void inject(LoginFragment obj);
}
