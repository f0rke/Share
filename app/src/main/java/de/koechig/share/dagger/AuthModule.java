package de.koechig.share.dagger;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;
import de.koechig.share.util.StringHelper;

/**
 * Created by Mumpi_000 on 27.06.2017.
 */
@Module
public class AuthModule {

    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public AuthController provideAuth(FirebaseAuth fAuth, DBController db, StringHelper sHelper) {
        return new AuthController(fAuth, db, sHelper);
    }

}
