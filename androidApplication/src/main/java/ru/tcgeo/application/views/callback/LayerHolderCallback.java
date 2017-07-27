package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.LayerHolder;

/**
 * Created by artem on 14.07.17.
 */

public interface LayerHolderCallback {
    void onMarkersSourceCheckChanged(LayerHolder holder, boolean isChecked);

    void onVisibilityCheckChanged(LayerHolder holder, boolean isChecked);

    void onSettings(LayerHolder holder);
}
