package de.koechig.share;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.ref.WeakReference;

import de.koechig.share.control.AuthController;

/**
 * Created by Mumpi_000 on 06.06.2017.
 */

public class ShareApp extends Application {
    private static WeakReference<ShareApp> ourInstance;
    private AuthController mAuthController;

    public static ShareApp getInstance() {
        return ourInstance.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (ourInstance == null) {
            ourInstance = new WeakReference<>(this);
        }
        mAuthController = new AuthController(FirebaseAuth.getInstance());
    }

    public AuthController getAuthController() {
        return mAuthController;
    }
}
