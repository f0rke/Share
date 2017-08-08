package de.koechig.share.channels;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.koechig.share.base.ListItem;
import de.koechig.share.R;
import de.koechig.share.items.ItemAdapter;
import de.koechig.share.model.Channel;
import de.koechig.share.util.StringHelper;
import de.koechig.share.util.TimestampHelper;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */
public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ChannelViewHolder> {

    @Inject
    TimestampHelper mTimestampHelper;
    @Inject
    StringHelper mStringHelper;
    protected List<ListItem> mListItems;
    protected OnItemClickListener mItemClickListener;

    //<editor-fold desc="# Setup #">
    public ChannelsAdapter(List<Channel> contentItems, OnItemClickListener listener) {
        mItemClickListener = listener;
        populateList(contentItems);
    }

    public void replaceList(List<Channel> list) {
        populateList(list);
        notifyDataSetChanged();
    }

    protected void populateList(List<Channel> list) {
        this.mListItems = new ArrayList<>();
        for (Channel contentItem : list) {
            mListItems.add(new ChannelListItem(contentItem));
        }
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mListItems.get(position).getViewType();
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        switch (viewType) {
            case R.layout.layout_channel_list_item:
                return new ChannelViewHolder(itemView, mItemClickListener);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="# Binding #">
    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        ListItem<Channel> listItem = mListItems.get(position);
        Channel item = listItem.content;
        bind(holder, item);
    }

    private void bind(ChannelViewHolder holder, Channel channel) {
        holder.name.setText(channel.getName());
        String name = channel.getName();
        if (name != null && name.length() > 0) {
            holder.channelIcon.setText(String.valueOf(mStringHelper.capitalizeFirstLetter(name).charAt(0)));
        } else {
            holder.channelIcon.setText("");
        }
        String lastName = channel.getLastContributorFirstName();
        if (lastName != null && lastName.length() > 0) {
            holder.lastEntryContributor.setText(lastName);
        } else {
            holder.lastEntryContributor.setText("");
        }
        String lastText = channel.getLastEntry();
        if (lastText != null && !lastText.isEmpty()) {
            holder.lastEntry.setText(lastText);
        } else {
            holder.lastEntry.setText("");
        }
        Long lastTimestamp = channel.getLastEntryTimestamp();
        if (lastText != null && !lastText.isEmpty()) {
            holder.lastEntryTimestamp.setText(mTimestampHelper.toSmartDisplayTime(lastTimestamp));
        } else {
            holder.lastEntryTimestamp.setText("");
        }
    }
    //</editor-fold>

    //<editor-fold desc="# Inner classes #">
    abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View rootView;

        ViewHolder(View root) {
            super(root);
            this.rootView = root;
            rootView.setOnClickListener(this);
        }
    }

    public class ChannelViewHolder extends ViewHolder {

        @BindView(R.id.name)
        public TextView name;
        @BindView(R.id.channelIcon)
        public TextView channelIcon;
        @BindView(R.id.lastEntryContributor)
        public TextView lastEntryContributor;
        @BindView(R.id.lastEntry)
        public TextView lastEntry;
        @BindView(R.id.unreadEntriesCount)
        public TextView unreadEntriesCount;
        @BindView(R.id.lastEntryTimestamp)
        public TextView lastEntryTimestamp;

        private OnItemClickListener mItemClickListener;

        public ChannelViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mItemClickListener = listener;
        }

        @Override
        public void onClick(View view) {
            this.mItemClickListener.onItemClick((Channel) mListItems.get(getAdapterPosition()).content);
        }
    }

    interface OnItemClickListener extends de.koechig.share.base.OnItemClickListener<Channel> {
        @Override
        void onItemClick(Channel item);
    }

    private class ChannelListItem extends ListItem<Channel> {
        ChannelListItem(Channel channel) {
            super(channel);
        }

        @Override
        public int getViewType() {
            return R.layout.layout_channel_list_item;
        }
    }
    //</editor-fold>
}
