package de.koechig.share.base;

public interface AbstractPresenter<V extends AbstractView> {
    void bindView(V view);

    void update();

    void unbindView();
}
