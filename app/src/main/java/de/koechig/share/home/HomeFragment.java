package de.koechig.share.home;

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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.koechig.share.R;
import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;
import de.koechig.share.control.ShareApp;
import de.koechig.share.createchannel.CreateChannelView;
import de.koechig.share.login.LoginActivity;
import de.koechig.share.login.LoginScreen;
import de.koechig.share.util.ColorHelper;

import static android.support.v4.view.GravityCompat.START;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class HomeFragment extends Fragment implements HomeScreen.View {

    //<editor-fold desc="# Objects #">
    private HomeScreen.Presenter mPresenter;
    private View.OnClickListener mOnFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mCreateChannelView != null) {
                mCreateChannelView.getPresenter().onAddNewChannel();
            }
        }
    };
    private ColorHelper mColorHelper;
    //</editor-fold>

    //<editor-fold desc="# Views #">
    private DrawerLayout mDrawer;

    private AppCompatImageView mHeaderIcon;
    private TextView mUsernameText;
    private TextView mUserMailText;
    private MenuItem mLoginLogoutView;
    private FloatingActionButton mFAB;
    private CreateChannelView mCreateChannelView;
    //</editor-fold>

    //<editor-fold desc="# Lifecycle #">
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mColorHelper = new ColorHelper(getContext());
        AuthController auth = ShareApp.getInstance().getAuthController();
        DBController db = ShareApp.getInstance().getDb();
        HomePresenter.HomeResourceProvider provider = new HomePresenter.HomeResourceProvider() {
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
        mPresenter = new HomePresenter(auth, db, provider);
        mCreateChannelView = new CreateChannelView(this);
        mCreateChannelView.onCreate();
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
        View root = inflater.inflate(R.layout.fragment_home, container, false);
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
}
