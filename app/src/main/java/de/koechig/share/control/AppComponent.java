package de.koechig.share.control;

import javax.inject.Singleton;

import dagger.Component;
import de.koechig.share.channels.ChannelsModule;
import de.koechig.share.channels.ChannelsSubComponent;
import de.koechig.share.createchannel.CreateChannelModule;
import de.koechig.share.createchannel.CreateChannelSubComponent;
import de.koechig.share.createitem.CreateItemModule;
import de.koechig.share.createitem.CreateItemSubComponent;
import de.koechig.share.util.HelperModule;
import de.koechig.share.items.ItemsModule;
import de.koechig.share.items.ItemsSubComponent;
import de.koechig.share.launch.LaunchModule;
import de.koechig.share.launch.LaunchSubComponent;
import de.koechig.share.login.LoginModule;
import de.koechig.share.login.LoginSubComponent;

/**
 * Created by Mumpi_000 on 27.06.2017.
 */

@Singleton
@Component(modules = {AuthModule.class, AppModule.class, DatabaseModule.class, HelperModule.class})
public interface AppComponent {
    ChannelsSubComponent newChannelsSubComponent(ChannelsModule module);

    ItemsSubComponent newItemsSubComponent(ItemsModule module);

    CreateChannelSubComponent newCreateChannelSubComponent(CreateChannelModule module);

    CreateItemSubComponent newCreateChannelSubComponent(CreateItemModule module);

    LaunchSubComponent newLaunchSubComponent(LaunchModule launchModule);

    LoginSubComponent newLoginSubComponent(LoginModule loginModule);
}
