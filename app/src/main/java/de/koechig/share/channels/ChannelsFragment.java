package de.koechig.share.channels;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.koechig.share.R;
import de.koechig.share.base.ListAdapter;
import de.koechig.share.base.OnItemClickListener;
import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;
import de.koechig.share.control.ShareApp;
import de.koechig.share.createchannel.CreateChannelView;
import de.koechig.share.items.ItemsActivity;
import de.koechig.share.login.LoginActivity;
import de.koechig.share.login.LoginScreen;
import de.koechig.share.model.Channel;
import de.koechig.share.util.ColorHelper;

import static android.support.v4.view.GravityCompat.START;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class ChannelsFragment extends Fragment implements ChannelsScreen.View {

    //<editor-fold desc="# Objects #">
    private ChannelsScreen.Presenter mPresenter;
    private View.OnClickListener mOnFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mCreateChannelView != null) {
                mCreateChannelView.getPresenter().onAddNewChannel();
            }
        }
    };
    private OnItemClickListener<Channel> mOnItemClickListener = new OnItemClickListener<Channel>() {
        @Override
        public void onItemClick(Channel item) {
            mPresenter.onChannelClicked(item);
        }
    };
    private ColorHelper mColorHelper;
    private ListAdapter<Channel> mAdapter;
    //</editor-fold>

    //<editor-fold desc="# Views #">
    private DrawerLayout mDrawer;
    private AppCompatImageView mHeaderIcon;
    private TextView mUsernameText;
    private TextView mUserMailText;
    private MenuItem mLoginLogoutView;
    private FloatingActionButton mFAB;
    private CreateChannelView mCreateChannelView;
    private RecyclerView mRecyclerView;
    //</editor-fold>

    //<editor-fold desc="# Lifecycle #">
    public static ChannelsFragment newInstance() {
        return new ChannelsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mColorHelper = new ColorHelper(getContext());
        AuthController auth = ShareApp.getInstance().getAuthController();
        DBController db = ShareApp.getInstance().getDb();
        ChannelsPresenter.HomeResourceProvider provider = new ChannelsPresenter.HomeResourceProvider() {
            @Override
            public String getNoUserMailErrorString() {
                return getString(R.string.no_user_mail_available);
            }

            @Override
            public String getNotLoggedInUsername() {
                return getString(R.string.not_logged_in_username);
            }

            @Override
            public String getNotLoggedInUserMail() {
                return getString(R.string.not_logged_in_user_mail);
            }
        };
        mPresenter = new ChannelsPresenter(auth, db, provider);
        mCreateChannelView = new CreateChannelView(this);
        mCreateChannelView.onCreate();
        mAdapter = new ChannelsAdapter(new ArrayList<Channel>(0), mOnItemClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.bindView(this);
        mCreateChannelView.onResume();
    }

    @Override
    public void onStop() {
        mCreateChannelView.onStop();
        mPresenter.unbindView();
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //ActionBar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Navigation
        mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        NavigationView nav = (NavigationView) getActivity().findViewById(R.id.navigation_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mHeaderIcon = (AppCompatImageView) nav.getHeaderView(0).findViewById(R.id.image_view);
        mUsernameText = (TextView) nav.getHeaderView(0).findViewById(R.id.username);
        mUserMailText = (TextView) nav.getHeaderView(0).findViewById(R.id.user_mail);

        mFAB = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        mFAB.setOnClickListener(mOnFabClickListener);

        for (int i = 0; i < nav.getMenu().size(); i++) {
            MenuItem item = nav.getMenu().getItem(i);
            int id = item.getItemId();
            if (id == R.id.action_login) {
                this.mLoginLogoutView = item;
                this.mLoginLogoutView.setVisible(false);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_channels, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onDestroy() {
        mCreateChannelView.onDestroy();
        super.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="# Side menu #">
    @Override
    public void displayLoginAction() {
        if (isAdded()) {
            mLoginLogoutView.setVisible(true);
            mLoginLogoutView.setTitle(R.string.action_login);
            mLoginLogoutView.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mPresenter.onLoginClicked();
                    mDrawer.closeDrawer(START);
                    return true;
                }
            });
        }
    }

    @Override
    public void displayLogoutAction() {
        if (isAdded()) {
            mLoginLogoutView.setVisible(true);
            mLoginLogoutView.setTitle(R.string.action_logout);
            mLoginLogoutView.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mPresenter.onLogoutClicked();
                    mDrawer.closeDrawer(START);
                    return true;
                }
            });
        }
    }

    @Override
    public void setUsername(String username) {
        if (isAdded()) {
            mUsernameText.setText(username);
        }
    }

    @Override
    public void setUserMail(String mail) {
        if (isAdded()) {
            mUserMailText.setText(mail);
        }
    }

    @Override
    public void setUserImage(Uri photoUrl) {
        //TODO
    }
    //</editor-fold>

    //<editor-fold desc="# Open other screens #">


    @Override
    public void showLoginScreen() {
        Intent login = LoginActivity.getStartIntent(getContext());
        startActivityForResult(login, LoginScreen.REQUEST_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.bindView(this);
        if (requestCode == LoginScreen.REQUEST_LOGIN) {
            if (resultCode == LoginScreen.RESULT_LOGGED_IN) {
                mPresenter.onLoggedIn();
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="# Messages #">
    @Override
    public void showLoginSuccessfulMessage() {
        if (isAdded() && getView() != null) {
            Snackbar.make(getView(), R.string.successfully_logged_in, Snackbar.LENGTH_LONG).show();
        }
    }
    //</editor-fold>

    //<editor-fold desc="# View #">
    @Override
    public void showChannels(List<Channel> result) {
        if (isAdded()) {
            mAdapter.replaceList(result);
        }
    }

    @Override
    public void showItemsScreen(Channel channel) {
        Intent intent = ItemsActivity.getStartIntent(getContext(), channel.getKey());
        startActivity(intent);
    }
    //</editor-fold>
}
