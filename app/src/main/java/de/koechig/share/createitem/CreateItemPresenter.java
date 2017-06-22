package de.koechig.share.createitem;

import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;
import de.koechig.share.model.Channel;
import de.koechig.share.model.Item;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class CreateItemPresenter implements CreateItemScreen.Presenter {

    private CreateItemScreen.View mView;
    private AuthController mAuth;
    private DBController mDb;
    private String mChannelKey;

    public CreateItemPresenter(AuthController auth, DBController db) {
        this.mAuth = auth;
        this.mDb = db;
    }

    @Override
    public void bindView(CreateItemScreen.View view) {
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
    public void onAddNewItem(String key) {
        mChannelKey = key;
        if (mView != null) {
            mView.show();
        }
    }

    @Override
    public void onSaveClicked(final String name, String description) {
        if (name != null && !name.isEmpty()) {
            final Item item = new Item(name, description != null && !description.isEmpty() ? description : null, mAuth.getCurrentUser());
            if (mChannelKey != null && mAuth.getCurrentUser() != null) {
                if (mView != null) {
                    mView.showProgress();
                }
                mDb.submitNewItemToChannel(item, mChannelKey, mAuth.getCurrentUser(), new DBController.ActionCallback() {
                    @Override
                    public void onSucceeded() {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.hideError();
                            mView.hide();
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        if (mView != null) {
                            mView.hideProgress();
                            mView.showError(e.getMessage());
                        }
                    }
                });
            }
        } else {
            if (mView != null) {
                mView.showError("No name given");
            }
        }
    }
}
