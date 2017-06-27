package de.koechig.share.createchannel;

import dagger.Module;
import dagger.Provides;
import de.koechig.share.control.DBController;

/**
 * Created by Mumpi_000 on 28.06.2017.
 */

@Module
public class CreateChannelModule {
    @Provides
    public CreateChannelScreen.Presenter providePresenter(DBController db) {
        return new CreateChannelPresenter(db);
    }
}
