package de.koechig.share.createitem;

import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public interface CreateItemScreen {
    interface View extends AbstractView {

        void showCreateItem();

        void showProgress();

        void hideProgress();
    }

    interface Presenter extends AbstractPresenter<View> {

        void onAddNewItem();

        void onSaveClicked(String name, String description);
    }
}
