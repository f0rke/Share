package de.koechig.share.util;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Mumpi_000 on 27.06.2017.
 */

@Module
public class HelperModule {
    @Provides
    @Singleton
    public StringHelper provideStringHelper() {
        return new StringHelper();
    }

    @Provides
    @Singleton
    public ColorHelper provideColorHelper(Context context) {
        return new ColorHelper(context);
    }

    @Provides
    @Singleton
    public DateHelper provideTimestampHelper(Context context) {
        return new DateHelper(context);
    }
}
