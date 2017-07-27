package de.koechig.share.model;

import android.content.Context;

import de.koechig.share.R;
import de.koechig.share.control.DBController;
import de.koechig.share.control.ShareApp;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */
public class MyDatabaseStringsProvider implements DBController.DatabaseStringsProvider {
    private Context mContext;

    public MyDatabaseStringsProvider(Context context) {
        this.mContext = context;
    }

    @Override
    public String getChannelAlreadyExistingMessage() {
        return mContext.getString(R.string.channel_already_exists);
    }

    @Override
    public String getItemAlreadyExistingMessage() {
        return mContext.getString(R.string.item_already_exists);
    }

    @Override
    public String getUserAlreadyExistingMessage() {
        return mContext.getString(R.string.user_already_exists);
    }
}
