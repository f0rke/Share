package de.koechig.share.createchannel;

import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Arrays;

import javax.inject.Inject;

import de.koechig.share.R;
import de.koechig.share.control.ShareApp;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class CreateChannelView implements CreateChannelScreen.View {
    protected final Fragment mStub;
    @Inject
    public CreateChannelScreen.Presenter mPresenter;
    protected AlertDialog mCreateItemDialog;

    private TextInputEditText mItemName;
    private DialogInterface.OnClickListener mOnSaveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (mItemName != null) {
                mPresenter.onSaveClicked(mItemName.getText().toString(), Arrays.asList("moritzkoechig", "stefanbaehr"));
            }
        }
    };

    public CreateChannelView(Fragment parent) {
        mStub = parent;
    }

    //<editor-fold desc="# Lifecycle #">
    public void onCreate() {
        ((ShareApp) mStub.getContext().getApplicationContext()).getApplicationComponent()
                .newCreateChannelSubComponent(new CreateChannelScreen.CreateChannelModule())
                .inject(this);
    }

    public void onResume() {
        mPresenter.bindView(this);
    }

    public void onStop() {
        mPresenter.unbindView();
    }

    public void onDestroy() {
        if (mCreateItemDialog != null) {
            mCreateItemDialog.dismiss();
            mCreateItemDialog = null;
        }
    }
    //</editor-fold>

    @Override
    public void showProgress() {
        //TODO
    }

    @Override
    public void hideProgress() {
        //TODO
    }

    @Override
    public void showCreateChannel() {
        if (mStub.isAdded()) {
            if (mCreateItemDialog == null || !mCreateItemDialog.isShowing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mStub.getContext());
                builder.setTitle(R.string.new_channel);
                View body = LayoutInflater.from(mStub.getContext()).inflate(R.layout.layout_create_channel, null, false);
                mItemName = (TextInputEditText) body.findViewById(R.id.input_item_name);
                builder.setView(body);
                builder.setPositiveButton(R.string.save, mOnSaveClickListener);
                builder.setNegativeButton(R.string.cancel, null);
                mCreateItemDialog = builder.show();
            }
        }
    }

    public CreateChannelScreen.Presenter getPresenter() {
        return mPresenter;
    }
}
