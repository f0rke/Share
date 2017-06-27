package de.koechig.share.items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.koechig.share.R;
import de.koechig.share.base.ListAdapter;
import de.koechig.share.base.OnItemClickListener;
import de.koechig.share.control.DBController;
import de.koechig.share.control.ShareApp;
import de.koechig.share.createitem.CreateItemView;
import de.koechig.share.model.Channel;
import de.koechig.share.model.Item;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */

public class ItemsFragment extends Fragment implements ItemsScreen.View {
    private static final String CHANNEL_IDENTIFIER = "channel";
    @Inject
    public ItemsScreen.Presenter mPresenter;
    private String mChannelKey;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecycler;
    private ListAdapter<Item> mAdapter;
    private OnItemClickListener<Item> mOnItemClickListener = new OnItemClickListener<Item>() {
        @Override
        public void onItemClick(Item item) {
            mPresenter.onItemClicked(item);
        }
    };
    private ActionBar mActionBar;
    private FloatingActionButton mFab;
    private View.OnClickListener mOnFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mCreateItemView != null) {
                mCreateItemView.getPresenter().onAddNewItem(mChannelKey);
            }
        }
    };
    @Inject
    public CreateItemView mCreateItemView;
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mPresenter.update();
        }
    };

    public static ItemsFragment newInstance(String channelKey) {

        Bundle args = new Bundle();
        args.putString(CHANNEL_IDENTIFIER, channelKey);
        ItemsFragment fragment = new ItemsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChannelKey = getArguments().getString(CHANNEL_IDENTIFIER);
        ((ShareApp) getContext().getApplicationContext()).getApplicationComponent()
                .newItemsSubComponent(new ItemsModule(this, mChannelKey))
                .inject(this);
        mCreateItemView.onCreate();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_items, container, false);

        mAdapter = new ItemAdapter(new ArrayList<Item>(0), mOnItemClickListener);

        mRecycler = (RecyclerView) root.findViewById(R.id.recycler_view);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecycler.setAdapter(mAdapter);

        mSwipeLayout = (SwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        mSwipeLayout.setOnRefreshListener(mOnRefreshListener);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //ActionBar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle(mChannelKey);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        mFab.setOnClickListener(mOnFabClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.bindView(this);
        mCreateItemView.onResume();
    }

    @Override
    public void onStop() {
        mCreateItemView.onStop();
        mPresenter.unbindView();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mCreateItemView.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mPresenter.onBackClicked();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void close() {
        if (isAdded()) {
            getActivity().finish();
        }
    }

    @Override
    public void displayChannel(Channel channel) {
        if (isAdded()) {
            if (mActionBar != null) {
                mActionBar.setTitle(channel.getName());
            }
            mFab.setEnabled(true);
        }
    }

    @Override
    public void displayItems(List<Item> items) {
        if (isAdded()) {
            mSwipeLayout.setRefreshing(false);
            mAdapter.replaceList(items);
        }
    }

    @Override
    public void showProgress() {
        if (isAdded()) {
            mSwipeLayout.setRefreshing(true);
        }
    }

    @Override
    public void hideProgress() {
        if (isAdded()) {
            mSwipeLayout.setRefreshing(false);
        }
    }
}