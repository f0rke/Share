package de.koechig.share.items;

import java.util.List;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;
import de.koechig.share.control.DBController;
import de.koechig.share.createitem.CreateItemView;
import de.koechig.share.model.Channel;
import de.koechig.share.model.Item;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */

public interface ItemsScreen {
    interface View extends AbstractView {

        void close();

        void displayChannel(Channel channel);

        void displayItems(List<Item> items);

        void showProgress();

        void hideProgress();
    }

    interface Presenter extends AbstractPresenter<View> {

        void onBackClicked();

        void onItemClicked(Item item);
    }

    /**
     * Created by Mumpi_000 on 28.06.2017.
     */

    @Module
    class ItemsModule {

        private String mChannel;
        private ItemsFragment mFragment;

        public ItemsModule(ItemsFragment fragment, String mChannel) {
            this.mFragment = fragment;
            this.mChannel = mChannel;
        }

        @Provides
        public Presenter providePresenter(DBController db) {
            return new ItemsPresenter(db, mChannel);
        }

        @Provides
        public CreateItemView provideCreateItemView() {
            return new CreateItemView(mFragment);
        }
    }

    /**
     * Created by Mumpi_000 on 28.06.2017.
     */

    @Subcomponent(modules = ItemsModule.class)
    interface ItemsSubComponent {
        void inject(ItemsFragment obj);

        void inject(ItemAdapter mAdapter);
    }
}
