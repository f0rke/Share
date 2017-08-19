package de.koechig.share.items;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.koechig.share.R;
import de.koechig.share.base.ListItem;
import de.koechig.share.control.AuthController;
import de.koechig.share.model.Item;
import de.koechig.share.util.DateHelper;

/**
 * Created by Mumpi_000 on 22.06.2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    @Inject
    DateHelper mHelper;
    @Inject
    AuthController mAuth;
    protected List<ListItem> mListItems;
    protected OnItemClickListener mItemClickListener;

    public ItemAdapter(List<Item> contentItems, OnItemClickListener listener) {
        mItemClickListener = listener;
        populateList(contentItems);
    }

    public void replaceList(List<Item> list) {
        populateList(list);
        notifyDataSetChanged();
    }

    protected void populateList(List<Item> list) {
        this.mListItems = new ArrayList<>();
        for (Item contentItem : list) {
            mListItems.add(new ItemListItem(contentItem));
        }
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.layout_item_list_item:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), mItemClickListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Item item = ((ItemListItem) mListItems.get(position)).content;
            bindItem((ItemViewHolder) holder, item);
        }
    }

    private void bindItem(ItemViewHolder holder, Item item) {
        holder.time.setText(mHelper.toSmartDisplayTime(item.getCreationDate()));
        holder.username.setText(item.getCreatorFirstName());
        holder.item_text.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mListItems.get(position).getViewType();
    }

    //<editor-fold desc="# Inner classes #">
    abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        final View rootView;

        ViewHolder(View root) {
            super(root);
            this.rootView = root;
            rootView.setOnLongClickListener(this);
        }
    }

    class ItemViewHolder extends ItemAdapter.ViewHolder {
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.item_text)
        TextView item_text;

        private OnItemClickListener mItemClickListener;

        public ItemViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mItemClickListener = listener;
        }

        @Override
        public boolean onLongClick(View view) {
            this.mItemClickListener.onItemClick((Item) mListItems.get(getAdapterPosition()).content);
            return true;
        }
    }

    interface OnItemClickListener extends de.koechig.share.base.OnItemClickListener<Item> {
        @Override
        void onItemClick(Item item);
    }

    private class ItemListItem extends ListItem<Item> {
        ItemListItem(Item item) {
            super(item);
        }

        @Override
        public int getViewType() {
            return R.layout.layout_item_list_item;
        }
    }
    //</editor-fold>
}
