package de.koechig.share.base;

/**
 * Created by Mumpi_000 on 22.06.2017.
 */
public abstract class ListItem<T> {
    public final T content;

    public ListItem(T content) {
        this.content = content;
    }

    public abstract int getViewType();
}
