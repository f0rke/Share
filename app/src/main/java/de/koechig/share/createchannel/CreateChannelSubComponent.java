package de.koechig.share.createchannel;

import dagger.Subcomponent;

/**
 * Created by Mumpi_000 on 28.06.2017.
 */

@Subcomponent(modules = CreateChannelModule.class)
public interface CreateChannelSubComponent {
    void inject(CreateChannelView obj);
}
