package de.koechig.share.dagger;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.koechig.share.control.DBController;
import de.koechig.share.model.MyDatabaseStringsProvider;
import de.koechig.share.util.StringHelper;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public DBController provideDatabase(DatabaseReference db, StringHelper helper, DBController.DatabaseStringsProvider provider) {
        return new DBController(db, helper, provider);
    }

    @Provides
    @Singleton
    public DatabaseReference provideReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Provides
    @Singleton
    public DBController.DatabaseStringsProvider provideStringProvider(Context context) {
        return new MyDatabaseStringsProvider(context);
    }
}
