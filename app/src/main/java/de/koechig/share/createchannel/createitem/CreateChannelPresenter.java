package de.koechig.share.createchannel.createitem;

import java.util.List;

import de.koechig.share.control.DBController;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class CreateChannelPresenter implements CreateChannelScreen.Presenter {
    private CreateChannelScreen.View mView;
    private DBController mDb;

    public CreateChannelPresenter(DBController db) {
        this.mDb = db;
    }

    @Override
    public void bindView(CreateChannelScreen.View view) {
        this.mView = view;
        update();
    }

    @Override
    public void update() {

    }

    @Override
    public void unbindView() {
        this.mView = null;
    }

    @Override
    public void onAddNewChannel() {
        if (mView != null) {
            mView.showCreateChannel();
        }
    }

    @Override
    public void onSaveClicked(final String name, List<String> users) {
        if (mView != null) {
            mView.showProgress();
        }
        mDb.createChannel(name, users, new DBController.ActionCallback() {
            @Override
            public void onSucceeded() {

            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }
}
