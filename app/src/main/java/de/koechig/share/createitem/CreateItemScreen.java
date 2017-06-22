package de.koechig.share.createitem;

import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;
import de.koechig.share.model.Channel;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public interface CreateItemScreen {
    interface View extends AbstractView {

        void show();

        void hide();

        void showProgress();

        void hideProgress();

        void showError(String error);

        void hideError();
    }

    interface Presenter extends AbstractPresenter<View> {

        void onAddNewItem(String key);

        void onSaveClicked(String name, String description);
    }
}
