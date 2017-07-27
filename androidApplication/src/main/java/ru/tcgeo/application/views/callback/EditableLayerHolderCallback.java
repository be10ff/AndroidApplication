package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.EdiableLayerHolder;

/**
 * Created by artem on 14.07.17.
 */

public interface EditableLayerHolderCallback {
    void onStartEdit(EdiableLayerHolder holder);

    void onClose();
}
