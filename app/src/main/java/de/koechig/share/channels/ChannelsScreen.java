package de.koechig.share.channels;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import de.koechig.share.R;
import de.koechig.share.base.AbstractPresenter;
import de.koechig.share.base.AbstractView;
import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;
import de.koechig.share.createchannel.CreateChannelView;
import de.koechig.share.model.Channel;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public interface ChannelsScreen {
    interface View extends AbstractView {
        void displayLoginAction();

        void displayLogoutAction();

        void setUsername(String username);

        void setUserMail(String mail);

        void setUserImage(Uri photoUrl);

        void showLoginScreen();

        void showLoginSuccessfulMessage();

        void showChannels(List<Channel> result);

        void showItemsScreen(Channel channel);
    }

    interface Presenter extends AbstractPresenter<View> {
        void onLoginClicked();

        void onLogoutClicked();

        void onLoggedIn();

        void onChannelClicked(Channel item);
    }

    /**
     * Created by Mumpi_000 on 27.06.2017.
     */
    @Module
    class ChannelsModule {
        private final ChannelsFragment mFragment;
        private final ChannelsAdapter.OnItemClickListener mClickListener;

        public ChannelsModule(ChannelsFragment fragment, ChannelsAdapter.OnItemClickListener clickListener) {
            mFragment = fragment;
            mClickListener = clickListener;
        }

        @Provides
        public ChannelsPresenter.ResourceProvider provideResourceProvider(final Context context) {
            return new ChannelsPresenter.ResourceProvider() {
                @Override
                public String getNoUserMailErrorString() {
                    return context.getString(R.string.no_user_mail_available);
                }

                @Override
                public String getNotLoggedInUsername() {
                    return context.getString(R.string.not_logged_in_username);
                }

                @Override
                public String getNotLoggedInUserMail() {
                    return context.getString(R.string.not_logged_in_user_mail);
                }
            };
        }

        @Provides
        public Presenter providePresenter(AuthController auth, DBController db, ChannelsPresenter.ResourceProvider provider) {
            return new ChannelsPresenter(auth, db, provider);
        }

        @Provides
        public ChannelsAdapter provideAdapter() {
            return new ChannelsAdapter(new ArrayList<Channel>(0), mClickListener);
        }

        @Provides
        public CreateChannelView provideCreateChannelView() {
            return new CreateChannelView(mFragment);
        }
    }

    /**
     * Created by Mumpi_000 on 27.06.2017.
     */
    @Subcomponent(modules = ChannelsModule.class)
    interface ChannelsSubComponent {
        void inject(ChannelsFragment obj);

        void inject(ChannelsAdapter mAdapter);
    }
}
