package de.koechig.share.notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import de.koechig.share.control.ShareApp;

/**
 * Created by Mumpi_000 on 27.07.2017.
 */

public class InstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = InstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        ((ShareApp)getApplicationContext()).sendRegistrationToServer(refreshedToken);
    }
}
