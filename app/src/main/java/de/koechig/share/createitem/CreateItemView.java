package de.koechig.share.createitem;

import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import de.koechig.share.R;
import de.koechig.share.control.AuthController;
import de.koechig.share.control.ShareApp;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class CreateItemView implements CreateItemScreen.View {
    protected final Fragment mStub;
    protected CreateItemScreen.Presenter mPresenter;
    protected android.support.v7.app.AlertDialog mCreateItemDialog;

    private TextInputEditText mItemName;
    private TextInputEditText mItemDescription;
    private DialogInterface.OnClickListener mOnSaveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (mItemName != null) {
                mPresenter.onSaveClicked(mItemName.getText().toString(), mItemDescription != null ? mItemDescription.getText().toString() : null);
            }
        }
    };


    public CreateItemView(Fragment parent) {
        mStub = parent;
    }

    //<editor-fold desc="# Lifecycle #">
    public void onCreate() {
        AuthController auth = ShareApp.getInstance().getAuthController();
        mPresenter = new CreateItemPresenter(auth);
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
    public void showCreateItem() {
        if (mStub.isAdded()) {
            if (mCreateItemDialog == null || !mCreateItemDialog.isShowing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mStub.getContext());
                builder.setTitle(R.string.new_item);
                View body = LayoutInflater.from(mStub.getContext()).inflate(R.layout.layout_create_item, null, false);
                mItemName = (TextInputEditText) body.findViewById(R.id.input_item_name);
                mItemDescription = (TextInputEditText) body.findViewById(R.id.input_item_description);
                builder.setView(body);
                builder.setPositiveButton(R.string.save, mOnSaveClickListener);
                builder.setNegativeButton(R.string.cancel, null);
                mCreateItemDialog = builder.show();
            }
        }
    }

    public CreateItemScreen.Presenter getPresenter() {
        return mPresenter;
    }
}
