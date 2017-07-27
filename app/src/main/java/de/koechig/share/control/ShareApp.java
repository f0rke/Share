package de.koechig.share.control;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import javax.inject.Inject;

import de.koechig.share.model.User;
import de.koechig.share.util.HelperModule;


/**
 * Created by Mumpi_000 on 06.06.2017.
 */

public class ShareApp extends Application {
    private AppComponent mApplicationComponent;

    @Inject
    DBController database;
    @Inject
    AuthController auth;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        mApplicationComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .authModule(new AuthModule())
                .helperModule(new HelperModule())
                .databaseModule(new DatabaseModule())
                .build();
        mApplicationComponent.inject(this);
    }

    public AppComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    public void sendRegistrationToServer(String refreshedToken) {
        User onUser = auth.getCurrentUser();
        if (onUser != null) {
            database.registerPushToken(refreshedToken, onUser);
        }
    }
}
