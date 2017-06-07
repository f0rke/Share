package de.koechig.share.login;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.koechig.share.R;
import de.koechig.share.control.ShareApp;
import de.koechig.share.control.AuthController;
import de.koechig.share.util.StringHelper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Mumpi_000 on 04.05.2017.
 */

public class LoginFragment extends Fragment implements LoginScreen.View {

    private LoginScreen.Presenter mPresenter;

    //<editor-fold desc="# Click Listener #">
    private OnClickListener mOnLoginClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mPresenter.onLoginClicked(mMailInput.getText().toString(), mPasswordInput.getText().toString());
        }
    };
    private OnClickListener mOnCancelResetPasswordClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mPresenter.onCancelResetPasswordClicked();
        }
    };
    private OnClickListener mOnSwitchToForgotPasswordClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mPresenter.onForgotPasswordClicked();
        }
    };
    private OnClickListener mOnResetPasswordClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mPresenter.onResetPasswordClicked(mMailInput.getText().toString());
        }
    };
    private OnClickListener mOnOpenMailClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mPresenter.onOpenMailClicked();
        }
    };
    //</editor-fold>

    //<editor-fold desc="# Views #">
    private ProgressBar mProgressBar;
    private TextView mErrorText;
    private TextInputEditText mMailInput;
    private TextInputEditText mPasswordInput;
    private AppCompatButton mActionButton;
    private AppCompatButton mForgotPasswordButton;
    //</editor-fold>

    //<editor-fold desc="# Lifecycle #">
    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthController auth = ShareApp.getInstance().getAuthController();
        StringHelper matcher = new StringHelper();
        mPresenter = new LoginPresenter(matcher, auth);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(R.string.action_login);
        }
        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            close();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.bindView(this);
    }

    @Override
    public void onStop() {
        mPresenter.unbindView();
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        mErrorText = (TextView) root.findViewById(R.id.error_text);
        mMailInput = (TextInputEditText) root.findViewById(R.id.input_email);
        mPasswordInput = (TextInputEditText) root.findViewById(R.id.input_password);

        mActionButton = (AppCompatButton) root.findViewById(R.id.login_button);
        mForgotPasswordButton = (AppCompatButton) root.findViewById(R.id.forgot_password_button);

        showLoginAction();

        return root;
    }
    //</editor-fold>

    //<editor-fold desc="# Actions #">
    @Override
    public void showLoginAction() {
        if (isAdded()) {
            mActionButton.setText(R.string.action_login);
            mActionButton.setOnClickListener(mOnLoginClick);

            mPasswordInput.setVisibility(VISIBLE);

            mForgotPasswordButton.setVisibility(VISIBLE);
            mForgotPasswordButton.setText(R.string.action_forgot_password);
            mForgotPasswordButton.setOnClickListener(mOnSwitchToForgotPasswordClick);
        }
    }

    @Override
    public void showResetPasswordAction() {
        if (isAdded()) {
            mActionButton.setText(R.string.action_reset_password);
            mActionButton.setOnClickListener(mOnResetPasswordClick);

            mPasswordInput.setVisibility(GONE);

            mForgotPasswordButton.setText(R.string.action_cancel);
            mForgotPasswordButton.setOnClickListener(mOnCancelResetPasswordClick);
        }
    }
    //</editor-fold>

    @Override
    public void showResetPasswordSuccess() {
        if (isAdded() && getView() != null) {
            Snackbar bar = Snackbar.make(getView(), R.string.reset_password_mail_sent, Snackbar.LENGTH_INDEFINITE);
            bar.setAction(R.string.open_mail, mOnOpenMailClick);
            bar.show();
        }
    }

    //<editor-fold desc="# Errors #">
    @Override
    public void showInvalidMailFormatError() {
        if (isAdded()) {
            mErrorText.setText(R.string.not_a_mail_address);
            mErrorText.setVisibility(VISIBLE);
            mMailInput.requestFocus();
        }
    }

    @Override
    public void showUsernameOrPasswordWrong() {
        if (isAdded()) {
            mErrorText.setText(R.string.username_or_password_wrong);
            mErrorText.setVisibility(VISIBLE);
            mMailInput.requestFocus();
        }
    }

    @Override
    public void showNoPasswordError() {
        if (isAdded()) {
            mErrorText.setText(R.string.no_password_provided);
            mErrorText.setVisibility(VISIBLE);
            mPasswordInput.requestFocus();
        }
    }

    @Override
    public void showNoSuchUserError() {
        if (isAdded()) {
            mErrorText.setText(R.string.no_such_user);
            mErrorText.setVisibility(VISIBLE);
            mMailInput.requestFocus();
        }
    }

    @Override
    public void showFatalError(Exception e) {
        if (isAdded()) {
            mErrorText.setText(getString(R.string.fatal_error_occurred) + ": " + e.getLocalizedMessage() + "\n" + getString(R.string.contact_developer_with_screenshot));
            mErrorText.setVisibility(VISIBLE);
            hideKeyboard();
        }
    }

    @Override
    public void hideError() {
        if (isAdded()) {
            mErrorText.setVisibility(GONE);
        }
    }

    private void hideKeyboard() {
        if (isAdded()) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="# Progress #">
    @Override
    public void showProgress() {
        if (isAdded()) {
            this.mProgressBar.setVisibility(VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        if (isAdded()) {
            this.mProgressBar.setVisibility(GONE);
        }
    }
    //</editor-fold>

    //<editor-fold desc="# Close #">
    @Override
    public void close() {
        if (isAdded()) {
            getActivity().finish();
        }
    }

    @Override
    public void closeOnLoginSuccess() {
        if (isAdded()) {
            getActivity().setResult(LoginScreen.RESULT_LOGGED_IN);
            close();
        }
    }
    //</editor-fold>

    //<editor-fold desc="# Messages #">
    @Override
    public void showMailClientChooser() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            mPresenter.onOpenMailFailed();
        }
    }

    @Override
    public void showOpenMailFailedMessage() {
        if (isAdded() && getView() != null) {
            Snackbar.make(getView(), R.string.could_not_open_mail, Snackbar.LENGTH_LONG).show();
        }
    }
    //</editor-fold>
}
