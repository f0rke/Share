package de.koechig.share.items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import de.koechig.share.control.DBController;
import de.koechig.share.control.ShareApp;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */

public class ItemsFragment extends Fragment implements ItemsScreen.View {
    private static final String CHANNEL_IDENTIFIER = "channel";
    private ItemsScreen.Presenter mPresenter;
    private String mChannel;

    public static ItemsFragment newInstance(String channel) {

        Bundle args = new Bundle();
        args.putString(CHANNEL_IDENTIFIER, channel);
        ItemsFragment fragment = new ItemsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBController mDb = ShareApp.getInstance().getDb();
        mChannel = getArguments().getString(CHANNEL_IDENTIFIER);
        mPresenter = new ItemsPresenter(mDb, mChannel);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.bindView(this);
    }

    @Override
    public void onStop() {
        mPresenter.unbindView();
        super.onStop();
    }

    private static class ItemsPresenter implements ItemsScreen.Presenter {
        private ItemsScreen.View mView;
        private final DBController mDb;
        private final String mChannel;

        private ItemsPresenter(DBController db, String channel) {
            this.mDb = db;
            this.mChannel = channel;
        }

        @Override
        public void bindView(ItemsScreen.View view) {
            this.mView = view;
            update();
        }

        @Override
        public void update() {

        }

        @Override
        public void unbindView() {
            mView = null;
        }
    }
}
