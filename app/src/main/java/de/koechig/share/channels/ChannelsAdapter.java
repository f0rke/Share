package de.koechig.share.channels;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.koechig.share.base.ListAdapter;
import de.koechig.share.base.ListItem;
import de.koechig.share.R;
import de.koechig.share.base.OnItemClickListener;
import de.koechig.share.model.Channel;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */
public class ChannelsAdapter extends ListAdapter<Channel> {

    public ChannelsAdapter(List<Channel> contentItems, OnItemClickListener<Channel> listener) {
        super(contentItems, listener);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.layout_channel_list_item;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(itemView, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        ListItem<Channel> listItem = mListItems.get(position);
        holder.channelName.setText(listItem.content.getName());
    }
}