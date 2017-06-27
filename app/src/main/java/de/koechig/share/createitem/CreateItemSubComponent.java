package de.koechig.share.createitem;

import dagger.Subcomponent;

/**
 * Created by Mumpi_000 on 28.06.2017.
 */

@Subcomponent(modules = CreateItemModule.class)
public interface CreateItemSubComponent {
    void inject(CreateItemView obj);
}
