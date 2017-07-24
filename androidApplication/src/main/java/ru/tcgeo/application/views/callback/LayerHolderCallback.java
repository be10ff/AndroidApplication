package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.LayerHolder;

/**
 * Created by artem on 14.07.17.
 */

public abstract class LayerHolderCallback {
    public abstract void onMarkersSourceCheckChanged(LayerHolder holder, boolean isChecked);

    public abstract void onVisibilityCheckChanged(LayerHolder holder, boolean isChecked);

    public abstract void onSettings(LayerHolder holder);
}
