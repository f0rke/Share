package de.koechig.share.items;

import java.util.List;

import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;
import de.koechig.share.model.Channel;
import de.koechig.share.model.Item;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */

interface ItemsScreen {
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
}
