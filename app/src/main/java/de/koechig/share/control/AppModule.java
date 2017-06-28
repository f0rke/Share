package de.koechig.share.control;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.koechig.share.util.StringHelper;

/**
 * Created by Mumpi_000 on 27.06.2017.
 */

@Module
public class AppModule {

    private final Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    public Application providesApplication() {
        return mApplication;
    }

    @Singleton
    @Provides
    public Context provideContext() {
        return mApplication;
    }
}
