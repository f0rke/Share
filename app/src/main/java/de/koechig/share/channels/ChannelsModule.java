package de.koechig.share.channels;

import android.content.Context;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.koechig.share.R;
import de.koechig.share.base.ListAdapter;
import de.koechig.share.base.OnItemClickListener;
import de.koechig.share.channels.ChannelsPresenter.ResourceProvider;
import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;
import de.koechig.share.createchannel.CreateChannelView;
import de.koechig.share.model.Channel;

/**
 * Created by Mumpi_000 on 27.06.2017.
 */
@Module
public class ChannelsModule {
    private final ChannelsFragment mFragment;
    private final OnItemClickListener<Channel> mClickListener;

    public ChannelsModule(ChannelsFragment fragment, OnItemClickListener<Channel> clickListener) {
        mFragment = fragment;
        mClickListener = clickListener;
    }

    @Provides
    public ResourceProvider provideResourceProvider(final Context context) {
        return new ResourceProvider() {
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
    public ChannelsScreen.Presenter providePresenter(AuthController auth, DBController db, ResourceProvider provider) {
        return new ChannelsPresenter(auth, db, provider);
    }

    @Provides
    public ListAdapter<Channel> provideAdapter() {
        return new ChannelsAdapter(new ArrayList<Channel>(0), mClickListener);
    }

    @Provides
    public CreateChannelView provideCreateChannelView() {
        return new CreateChannelView(mFragment);
    }
}
