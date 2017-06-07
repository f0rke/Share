package de.koechig.share.home;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.koechig.share.control.AuthController;
import de.koechig.share.home.HomeScreen.View;
import de.koechig.share.model.Item;
import de.koechig.share.model.User;

/**
 * Created by Mumpi_000 on 07.06.2017.
 */

public class HomePresenter implements HomeScreen.Presenter {

    //Member variables
    private View mView;
    private final AuthController mAuth;
    private AuthController.UserListener mUserListener = new AuthController.UserListener() {
        @Override
        public void onUpdated() {
            updateSideMenu();
        }
    };
    private HomeResourceProvider mProvider;

    public HomePresenter(AuthController auth, HomeResourceProvider provider) {
        mAuth = auth;
        mProvider = provider;
    }

    @Override
    public void onLoginClicked() {
        if (mView != null) {
            mView.showLoginScreen();
        }
    }

    @Override
    public void onLogoutClicked() {
        mAuth.logout();
        updateSideMenu();
    }

    @Override
    public void onLoggedIn() {
        if (mView != null) {
            mView.showLoginSuccessfulMessage();
        }
    }

    @Override
    public void onAddNewItem() {
        if (mView != null) {
            mView.showCreateItem();
        }
    }

    @Override
    public void onSaveClicked(final String name, String description) {
        final Item item = new Item(name, description, mAuth.getUser());
        if (mView != null) {
            mView.showProgress();
        }
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("items");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(name)) {
                    //exists
                    //TODO: Notify duplicate error
                    if (mView != null) {
                        mView.hideProgress();
                    }
                } else {
                    ref.child(item.getName()).setValue(item, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //TODO handle
                            if(mView!=null){
                                mView.hideProgress();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
                if (mView != null) {
                    mView.hideProgress();
                }
            }
        });
    }

    @Override
    public void bindView(View view) {
        mView = view;
        update();
        mAuth.addListener(mUserListener);
    }

    @Override
    public void update() {
        updateSideMenu();
        handleAuthState();
    }

    private void handleAuthState() {
        User user = mAuth.getUser();
        if (user != null) {
            loadData();
        } else {
            if (mView != null) {
                mView.showLoginScreen();
            }
        }
    }

    private void loadData() {

    }

    private void updateSideMenu() {
        if (mView != null) {
            User user = mAuth.getUser();
            if (user != null) {
                String username = user.getFirstName() != null ? user.getFirstName() : "";
                mView.setUsername(username);
                String userMail = user.getEmail() != null ? user.getEmail() : mProvider.getNoUserMailErrorString();
                mView.setUserMail(userMail);
            } else {
                mView.setUsername(mProvider.getNotLoggedInUsername());
                mView.setUserMail(mProvider.getNotLoggedInUserMail());
            }
            if (user != null) {
                mView.displayLogoutAction();
            } else {
                mView.displayLoginAction();
            }
        }
    }

    @Override
    public void unbindView() {
        mAuth.removeListener(mUserListener);
        mView = null;
    }

    interface HomeResourceProvider {
        String getNoUserMailErrorString();

        String getNotLoggedInUsername();

        String getNotLoggedInUserMail();
    }
}
