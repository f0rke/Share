package de.koechig.share.createitem;

import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import de.koechig.share.R;
import de.koechig.share.control.ShareApp;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class CreateItemView implements CreateItemScreen.View {
    protected final Fragment mStub;
    @Inject
    public CreateItemScreen.Presenter mPresenter;
    protected android.support.v7.app.AlertDialog mCreateItemDialog;

    private TextView mErrorText;
    private TextInputEditText mItemName;
    private TextInputEditText mItemDescription;
    private View.OnClickListener mOnSaveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mItemName != null) {
                mPresenter.onSaveClicked(mItemName.getText().toString(), mItemDescription != null ? mItemDescription.getText().toString() : null);
            }
        }
    };
    private DialogInterface.OnClickListener mDoNothingListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    };

    public CreateItemView(Fragment parent) {
        mStub = parent;
    }

    //<editor-fold desc="# Lifecycle #">
    public void onCreate() {
        ((ShareApp) mStub.getContext().getApplicationContext()).getApplicationComponent()
                .newCreateChannelSubComponent(new CreateItemScreen.CreateItemModule())
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
            dismissDialog();
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
    public void show() {
        if (mStub.isAdded()) {
            if (mCreateItemDialog == null || !mCreateItemDialog.isShowing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mStub.getContext());
                builder.setTitle(R.string.new_item);
                View body = LayoutInflater.from(mStub.getContext()).inflate(R.layout.layout_create_item, null, false);
                mItemName = (TextInputEditText) body.findViewById(R.id.input_item_name);
                mItemDescription = (TextInputEditText) body.findViewById(R.id.input_item_description);
                mErrorText = (TextView) body.findViewById(R.id.error_text);
                builder.setView(body);
                builder.setPositiveButton(R.string.save, mDoNothingListener);
                builder.setNegativeButton(R.string.cancel, mDoNothingListener);
                mCreateItemDialog = builder.show();
                mCreateItemDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(mOnSaveClickListener);
            }
        }
    }

    @Override
    public void hide() {
        if (mCreateItemDialog != null) {
            dismissDialog();
        }
    }

    private void dismissDialog() {
        mCreateItemDialog.dismiss();
        mCreateItemDialog = null;
        mErrorText = null;
        mItemDescription = null;
        mItemName = null;
    }

    public CreateItemScreen.Presenter getPresenter() {
        return mPresenter;
    }
}
