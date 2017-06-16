package de.koechig.share.createitem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.koechig.share.control.AuthController;
import de.koechig.share.control.DBController;
import de.koechig.share.model.Item;

/**
 * Created by moritzkochig on 6/16/17.
 *
 * @author Moritz Köchig
 *         © mobile concepts GmbH 2016
 */

public class CreateItemPresenter implements CreateItemScreen.Presenter {
    private CreateItemScreen.View mView;
    private AuthController mAuth;
    private DBController mDb;

    public CreateItemPresenter(AuthController auth, DBController db) {
        this.mAuth = auth;
        this.mDb = db;
    }

    @Override
    public void bindView(CreateItemScreen.View view) {
        this.mView = view;
        update();
    }

    @Override
    public void update() {

    }

    @Override
    public void unbindView() {
        this.mView = null;
    }

    @Override
    public void onAddNewItem() {
        if (mView != null) {
            mView.showCreateItem();
        }
    }

    @Override
    public void onSaveClicked(final String name, String description) {
        final Item item = new Item(name, description, mAuth.getCurrentUser());
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
                            if (mView != null) {
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
}
