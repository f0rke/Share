package de.koechig.share.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.koechig.share.R;

/**
 * Created by Mumpi_000 on 21.06.2017.
 */
public abstract class ListAdapter<T> extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    protected List<ListItem<T>> mListItems;
    protected OnItemClickListener<T> mItemClickListener;

    public ListAdapter(List<T> contentItems, OnItemClickListener<T> listener) {
        mItemClickListener = listener;
        populateList(contentItems);
    }

    public void replaceList(List<T> list) {
        populateList(list);
        notifyDataSetChanged();
    }

    protected void populateList(List<T> list) {
        this.mListItems = new ArrayList<>();
        for (T contentItems : list) {
            mListItems.add(new ListItem<T>(contentItems) {
                @Override
                public int getViewType() {
                    return R.layout.default_list_item;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View rootView;
        @BindView(R.id.name)
        public TextView name;

        private OnItemClickListener<T> mItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener<T> listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.rootView = itemView;
            this.mItemClickListener = listener;
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.mItemClickListener.onItemClick(mListItems.get(getAdapterPosition()).content);
        }
    }
}
