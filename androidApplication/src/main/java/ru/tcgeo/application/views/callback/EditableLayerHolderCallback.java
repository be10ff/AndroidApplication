package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.EdiableLayerHolder;

/**
 * Created by artem on 14.07.17.
 */

public abstract class EditableLayerHolderCallback {
    public abstract void onStartEdit(EdiableLayerHolder holder);

    public abstract void onClose();
}
