package de.koechig.share.items;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import de.koechig.share.R;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */

public class ItemsActivity extends AppCompatActivity {
    private static final String EXTRA_CHANNEL = "channel";

    public static Intent getStartIntent(Context c, String channelKey) {
        Intent intent = new Intent(c, ItemsActivity.class);
        intent.putExtra(EXTRA_CHANNEL, channelKey);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        if (savedInstanceState == null) {
            initFragment(ItemsFragment.newInstance(getIntent().getStringExtra(EXTRA_CHANNEL)));
        }
    }

    private void initFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.content_container, fragment);
        transaction.commit();
    }
}
