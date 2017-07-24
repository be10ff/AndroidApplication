package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.gilib.GIEditableLayer;

/**
 * Created by artem on 14.07.17.
 */

public abstract class EditableLayerCallback {
    public abstract void onStartEdit(GIEditableLayer layer);

    public abstract void onStopEdit();
}
