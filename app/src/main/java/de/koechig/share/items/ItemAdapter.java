package de.koechig.share.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.koechig.share.R;
import de.koechig.share.base.ListAdapter;
import de.koechig.share.base.ListItem;
import de.koechig.share.base.OnItemClickListener;
import de.koechig.share.model.Channel;
import de.koechig.share.model.Item;

/**
 * Created by Mumpi_000 on 22.06.2017.
 */

class ItemAdapter extends ListAdapter<Item> {
    public ItemAdapter(ArrayList<Item> items, OnItemClickListener<Item> onItemClickListener) {
        super(items, onItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.layout_item_list_item;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(itemView, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        ListItem<Item> listItem = mListItems.get(position);
        holder.channelName.setText(listItem.content.getName());
    }
}
