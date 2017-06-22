package de.koechig.share.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
            mListItems.add(new ListItem<>(contentItems));

        }
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView channelName;
        public final View rootView;

        private OnItemClickListener<T> mItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener<T> listener) {
            super(itemView);
            this.rootView = itemView;
            this.mItemClickListener = listener;
            channelName = (TextView) itemView.findViewById(R.id.name);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.mItemClickListener.onItemClick(mListItems.get(getAdapterPosition()).content);
        }
    }
}
