package de.koechig.share.items;

import java.util.List;

import de.koechig.share.control.DBController;
import de.koechig.share.model.Channel;
import de.koechig.share.model.Item;

/**
 * Created by Mumpi_000 on 22.06.2017.
 */
public class ItemsPresenter implements ItemsScreen.Presenter {
    private ItemsScreen.View mView;
    private final DBController mDb;
    private final String mChannelKey;
    private DBController.RetrieveCallback<Channel> mChannelFetchCallback = new DBController.RetrieveCallback<Channel>() {
        @Override
        public void onSuccess(Channel result) {
            onChannelFetched(result);
        }

        @Override
        public void onError(Exception e) {

        }
    };
    private DBController.RetrieveCallback<List<Item>> mItemsFetchCallback = new DBController.RetrieveCallback<List<Item>>() {
        @Override
        public void onSuccess(List<Item> result) {
            onItemsFetched(result);
        }

        @Override
        public void onError(Exception e) {
            onItemsFetchFailed(e);
        }
    };

    private Channel mChannel;

    ItemsPresenter(DBController db, String channel) {
        this.mDb = db;
        this.mChannelKey = channel;
    }

    @Override
    public void bindView(ItemsScreen.View view) {
        this.mView = view;
        update();
    }

    @Override
    public void update() {
        if (mChannel == null) {
            updateChannel();
        } else {
            updateItems();
        }
    }

    private void updateChannel() {
        mDb.fetchChannel(mChannelKey, mChannelFetchCallback);
    }

    private void onChannelFetched(Channel channel) {
        mChannel = channel;
        if (mView != null) {
            mView.displayChannel(channel);
        }
        updateItems();
    }

    private void updateItems() {
        if (mView != null) {
            mView.showProgress();
        }
        mDb.listenForItems(mChannel,mItemsFetchCallback);
        mDb.fetchItems(mChannel, mItemsFetchCallback);
    }

    private void onChannelFetchFailed(Exception e) {

    }

    private void onItemsFetched(List<Item> items) {
        if (mView != null) {
            mView.displayItems(items);
        }
        itemFetchFinished();
    }

    private void onItemsFetchFailed(Exception e) {

        itemFetchFinished();
    }

    private void itemFetchFinished() {
        if (mView != null) {
            mView.hideProgress();
        }
    }

    private void onChannelUpdate(Channel channel) {

    }

    private void onItemsUpdate(List<Item> items) {

    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void onBackClicked() {
        if (mView != null) {
            mView.close();
        }
    }

    @Override
    public void onItemClicked(Item item) {
        if (mView != null) {
            //No functionality yet
        }
    }
}
