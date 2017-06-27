package de.koechig.share.createitem;

import dagger.Module;
import dagger.Provides;
import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;
import de.koechig.share.createchannel.CreateChannelModule;

/**
 * Created by Mumpi_000 on 28.06.2017.
 */

@Module
public class CreateItemModule extends CreateChannelModule {
    @Provides
    public CreateItemScreen.Presenter providePresenter(AuthController auth, DBController db) {
        return new CreateItemPresenter(auth, db);
    }
}
