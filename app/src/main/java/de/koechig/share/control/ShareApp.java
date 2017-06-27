package de.koechig.share.control;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import de.koechig.share.dagger.AppComponent;
import de.koechig.share.dagger.AppModule;
import de.koechig.share.dagger.AuthModule;
import de.koechig.share.dagger.DaggerAppComponent;
import de.koechig.share.dagger.DatabaseModule;
import de.koechig.share.dagger.HelperModule;


/**
 * Created by Mumpi_000 on 06.06.2017.
 */

public class ShareApp extends Application {
    private AppComponent mApplicationComponent;

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
    }

    public AppComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}
