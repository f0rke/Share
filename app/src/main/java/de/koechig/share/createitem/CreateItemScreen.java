package de.koechig.share.createitem;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;
import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;
import de.koechig.share.createchannel.CreateChannelScreen;

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

    /**
     * Created by Mumpi_000 on 28.06.2017.
     */

    @Subcomponent(modules = CreateItemModule.class)
    interface CreateItemSubComponent {
        void inject(CreateItemView obj);
    }

    /**
     * Created by Mumpi_000 on 28.06.2017.
     */

    @Module
    class CreateItemModule extends CreateChannelScreen.CreateChannelModule {
        @Provides
        public Presenter providePresenter(AuthController auth, DBController db) {
            return new CreateItemPresenter(auth, db);
        }
    }
}
