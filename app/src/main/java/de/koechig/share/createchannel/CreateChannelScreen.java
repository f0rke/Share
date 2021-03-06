package de.koechig.share.createchannel;

import java.util.List;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;
import de.koechig.share.channels.ChannelsPresenter;
import de.koechig.share.control.DBController;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public interface CreateChannelScreen {
    interface View extends AbstractView {

        void showError(String error);

        void hideError();

        void showCreateChannel();

        void showProgress();

        void hideProgress();

        void close();
    }

    interface Presenter extends AbstractPresenter<View> {

        void onAddNewChannel();

        void onSaveClicked(String name);
    }

    /**
     * Created by Mumpi_000 on 28.06.2017.
     */

    @Subcomponent(modules = CreateChannelModule.class)
    interface CreateChannelSubComponent {
        void inject(CreateChannelView view);

        void inject(CreateChannelPresenter presenter);
    }

    /**
     * Created by Mumpi_000 on 28.06.2017.
     */

    @Module
    class CreateChannelModule {
        @Provides
        public Presenter providePresenter() {
            return new CreateChannelPresenter();
        }
    }
}
