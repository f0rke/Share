package de.koechig.share.home;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.koechig.share.R;
import de.koechig.share.ShareApp;
import de.koechig.share.control.AuthController;
import de.koechig.share.login.LoginActivity;
import de.koechig.share.login.LoginScreen;
import de.koechig.share.util.ColorHelper;

import static android.support.v4.view.GravityCompat.START;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class HomeFragment extends Fragment implements HomeScreen.View {
    private HomeScreen.Presenter mPresenter;
    private ColorHelper mColorHelper;

    //<editor-fold desc="# Views #">
    private DrawerLayout mDrawer;
    private AppCompatImageView mHeaderIcon;
    private TextView mUsernameText;

    private TextView mUserMailText;
    private MenuItem mLoginLogoutView;
    private MenuItem mSettingsView;
    private MenuItem mFeedbackView;
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
        mPresenter = new HomePresenter(auth, provider);
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