package de.koechig.share.launch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import de.koechig.share.R;
import de.koechig.share.channels.ChannelsModule;
import de.koechig.share.control.AuthController;
import de.koechig.share.control.ShareApp;
import de.koechig.share.channels.ChannelsActivity;
import de.koechig.share.login.LoginActivity;
import de.koechig.share.login.LoginScreen;

/**
 * Created by moritzkochig on 6/13/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class LaunchActivity extends AppCompatActivity implements LaunchScreen.View {

    @Inject
    public LaunchScreen.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ShareApp) getApplicationContext()).getApplicationComponent()
                .newLaunchSubComponent(new LaunchModule())
                .inject(this);
        setContentView(R.layout.activity_launch);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.bindView(this);
    }

    @Override
    protected void onStop() {
        mPresenter.unbindView();
        super.onStop();
    }

    @Override
    public void showHomeScreen() {
        Intent intent = ChannelsActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLoginScreen() {
        Intent login = LoginActivity.getStartIntent(this);
        startActivityForResult(login, LoginScreen.REQUEST_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginScreen.REQUEST_LOGIN) {
            if (resultCode != LoginScreen.RESULT_LOGGED_IN) {
                finish();
            }
        }
    }
}
