package de.koechig.share.createchannel;

import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.koechig.share.R;
import de.koechig.share.control.ShareApp;

import static android.view.View.*;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class CreateChannelView implements CreateChannelScreen.View {

    //<editor-fold desc="# Dependencies #">
    protected final Fragment mStub;
    @Inject
    public CreateChannelScreen.Presenter mPresenter;
    //</editor-fold>

    //<editor-fold desc="# Views #">
    private AlertDialog mCreateItemDialog;
    @BindView(R.id.input_item_name)
    TextInputEditText mItemName;
    @BindView(R.id.error_text)
    TextView mErrorText;
    //</editor-fold>

    private OnClickListener mOnSaveClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mItemName != null) {
                mPresenter.onSaveClicked(mItemName.getText().toString());
            }
        }
    };
    private DialogInterface.OnClickListener mEmptyClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    };

    public CreateChannelView(Fragment parent) {
        mStub = parent;
    }

    //<editor-fold desc="# Lifecycle #">
    public void onCreate() {
        CreateChannelScreen.CreateChannelSubComponent component = ((ShareApp) mStub.getContext().getApplicationContext()).getApplicationComponent()
                .newCreateChannelSubComponent(new CreateChannelScreen.CreateChannelModule());
        component.inject(this);
        component.inject((CreateChannelPresenter) mPresenter);
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
    public void close() {
        if (mCreateItemDialog != null && mCreateItemDialog.isShowing()) {
            mCreateItemDialog.dismiss();
        }
    }

    @Override
    public void showError(String error) {
        if (mStub.isAdded() && mErrorText != null) {
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(error);
        }
    }

    @Override
    public void hideError() {
        if (mStub.isAdded() && mErrorText != null) {
            mErrorText.setVisibility(View.GONE);
        }
    }

    @Override
    public void showCreateChannel() {
        if (mStub.isAdded()) {
            if (mCreateItemDialog == null || !mCreateItemDialog.isShowing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mStub.getContext());
                builder.setTitle(R.string.new_channel);
                View body = LayoutInflater.from(mStub.getContext()).inflate(R.layout.layout_create_channel, null, false);
                ButterKnife.bind(this, body);
                builder.setView(body);
                builder.setPositiveButton(R.string.save, mEmptyClick);
                builder.setNegativeButton(R.string.cancel, null);
                mCreateItemDialog = builder.show();
                mCreateItemDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(mOnSaveClickListener);
            }
        }
    }

    public CreateChannelScreen.Presenter getPresenter() {
        return mPresenter;
    }
}
