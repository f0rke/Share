package de.koechig.share.createchannel.createitem;

import java.util.List;

import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public interface CreateChannelScreen {
    interface View extends AbstractView {

        void showCreateChannel();

        void showProgress();

        void hideProgress();
    }

    interface Presenter extends AbstractPresenter<View> {

        void onAddNewChannel();

        void onSaveClicked(String name, List<String> users);
    }
}
