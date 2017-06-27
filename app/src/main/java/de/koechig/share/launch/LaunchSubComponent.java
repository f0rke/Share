package de.koechig.share.launch;

import dagger.Subcomponent;

/**
 * Created by Mumpi_000 on 28.06.2017.
 */

@Subcomponent(modules = LaunchModule.class)
public interface LaunchSubComponent {
    void inject(LaunchActivity obj);
}
