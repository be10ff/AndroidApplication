package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.data.gilib.layer.GIEditableLayer;

/**
 * Created by artem on 14.07.17.
 */

public interface EditableLayerCallback {
    void onStartEdit(GIEditableLayer layer);

    void onStopEdit();
}
