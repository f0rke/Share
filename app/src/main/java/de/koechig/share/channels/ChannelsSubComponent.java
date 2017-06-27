package de.koechig.share.channels;

import dagger.Subcomponent;
import de.koechig.share.base.ListAdapter;
import de.koechig.share.createchannel.CreateChannelView;
import de.koechig.share.dagger.AppComponent;
import de.koechig.share.model.Channel;

/**
 * Created by Mumpi_000 on 27.06.2017.
 */
//@ChannelsScope
@Subcomponent(modules = ChannelsModule.class)
public interface ChannelsSubComponent {
    void inject(ChannelsFragment obj);
//
//    ChannelsPresenter.ResourceProvider provider();
//
//    ChannelsScreen.Presenter presenter();
//
//    ListAdapter<Channel> adapter();
//
//    CreateChannelView createChannelView();
}
