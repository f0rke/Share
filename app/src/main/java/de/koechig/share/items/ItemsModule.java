package de.koechig.share.items;

import android.support.v4.app.Fragment;

import dagger.Module;
import dagger.Provides;
import de.koechig.share.control.DBController;
import de.koechig.share.createitem.CreateItemView;

/**
 * Created by Mumpi_000 on 28.06.2017.
 */

@Module
public class ItemsModule {

    private String mChannel;
    private ItemsFragment mFragment;

    public ItemsModule(ItemsFragment fragment, String mChannel) {
        this.mFragment = fragment;
        this.mChannel = mChannel;
    }

    @Provides
    public ItemsScreen.Presenter providePresenter(DBController db) {
        return new ItemsPresenter(db, mChannel);
    }

    @Provides
    public CreateItemView provideCreateItemView() {
        return new CreateItemView(mFragment);
    }
}
