package de.koechig.share.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.koechig.share.util.ColorHelper;
import de.koechig.share.util.StringHelper;

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
}
