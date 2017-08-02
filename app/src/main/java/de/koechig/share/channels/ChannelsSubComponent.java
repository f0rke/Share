package de.koechig.share.channels;

import dagger.Subcomponent;
/**
 * Created by Mumpi_000 on 27.06.2017.
 */
@Subcomponent(modules = ChannelsModule.class)
public interface ChannelsSubComponent {
    void inject(ChannelsFragment obj);

    void inject(ChannelsAdapter mAdapter);
}
