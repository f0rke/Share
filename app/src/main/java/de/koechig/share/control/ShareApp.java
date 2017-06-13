package de.koechig.share.control;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;

import de.koechig.share.util.StringHelper;

/**
 * Created by Mumpi_000 on 06.06.2017.
 */

public class ShareApp extends Application {
    private static WeakReference<ShareApp> ourInstance;
    private AuthController mAuthController;
    private DBController mDB;

    public static ShareApp getInstance() {
        return ourInstance.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (ourInstance == null) {
            ourInstance = new WeakReference<>(this);
        }
        mDB = new DBController(FirebaseDatabase.getInstance().getReference(), new StringHelper());
        mAuthController = new AuthController(FirebaseAuth.getInstance(), mDB);
    }

    public AuthController getAuthController() {
        return mAuthController;
    }
}
