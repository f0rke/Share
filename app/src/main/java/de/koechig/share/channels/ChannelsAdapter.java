package de.koechig.share.channels;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.koechig.share.R;
import de.koechig.share.base.OnItemClickListener;
import de.koechig.share.model.Channel;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */
public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ViewHolder> {
    private List<Item> mItems;
    private OnItemClickListener<Channel> mItemClickListener;

    public ChannelsAdapter(List<Channel> channels, OnItemClickListener<Channel> listener) {
        mItemClickListener = listener;
        setList(channels);
    }

    public void replaceList(List<Channel> list) {
        setList(list);
        notifyDataSetChanged();
    }

    private void setList(List<Channel> list) {
        this.mItems = new ArrayList<>();
        for (Channel channel : list) {
            mItems.add(new Item(channel));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.layout_channel_list_item;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(itemView, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mItems.get(position);
        holder.channelName.setText(item.channel.getName());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class Item {
        public final Channel channel;

        public Item(Channel channel) {
            this.channel = channel;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView channelName;

        private OnItemClickListener<Channel> mItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener<Channel> listener) {
            super(itemView);
            this.mItemClickListener = listener;
            channelName = (TextView) itemView.findViewById(R.id.channel_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.mItemClickListener.onItemClick(mItems.get(getAdapterPosition()).channel);
        }
    }
}
