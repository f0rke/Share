package de.koechig.share.items;

import dagger.Subcomponent;

/**
 * Created by Mumpi_000 on 28.06.2017.
 */

@Subcomponent(modules = ItemsModule.class)
public interface ItemsSubComponent {
    void inject(ItemsFragment obj);

    void inject(ItemAdapter mAdapter);
}
