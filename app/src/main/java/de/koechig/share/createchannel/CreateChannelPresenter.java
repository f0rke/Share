package de.koechig.share.createchannel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class CreateChannelPresenter implements CreateChannelScreen.Presenter {
    @Inject
    AuthController mAuth;
    @Inject
    DBController mDb;

    private CreateChannelScreen.View mView;

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
    public void onSaveClicked(final String name) {
        if (mView != null) {
            mView.showProgress();
        }
        List<String> users = new ArrayList<String>() {{
            add(mAuth.getCurrentUser().getUid());
        }};
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
