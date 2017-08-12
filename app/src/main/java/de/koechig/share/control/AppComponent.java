package de.koechig.share.control;

import javax.inject.Singleton;

import dagger.Component;
import de.koechig.share.channels.ChannelsScreen;
import de.koechig.share.createchannel.CreateChannelScreen;
import de.koechig.share.createitem.CreateItemScreen;
import de.koechig.share.items.ItemsScreen;
import de.koechig.share.launch.LaunchScreen;
import de.koechig.share.login.LoginScreen;
import de.koechig.share.util.HelperModule;

/**
 * Created by Mumpi_000 on 27.06.2017.
 */

@Singleton
@Component(modules = {AuthModule.class, AppModule.class, DatabaseModule.class, HelperModule.class})
public interface AppComponent {
    ChannelsScreen.ChannelsSubComponent newChannelsSubComponent(ChannelsScreen.ChannelsModule module);

    ItemsScreen.ItemsSubComponent newItemsSubComponent(ItemsScreen.ItemsModule module);

    CreateChannelScreen.CreateChannelSubComponent newCreateChannelSubComponent(CreateChannelScreen.CreateChannelModule module);

    CreateItemScreen.CreateItemSubComponent newCreateChannelSubComponent(CreateItemScreen.CreateItemModule module);

    LaunchScreen.LaunchSubComponent newLaunchSubComponent(LaunchScreen.LaunchModule launchModule);

    LoginScreen.LoginSubComponent newLoginSubComponent(LoginScreen.LoginModule loginModule);

    void inject(ShareApp shareApp);
}
