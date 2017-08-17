package ru.tcgeo.application.views.callback;

import java.io.File;

import ru.tcgeo.application.gilib.GIEditableLayer;
import ru.tcgeo.application.gilib.GITuple;

/**
 * Created by artem on 14.07.17.
 */

public interface LayerCallback {
    void onMarkersSourceCheckChanged(GITuple tuple, boolean isChecked);

    void onImmediatelyChange();

//    void onLaterChange();

    GITuple onAddLayer(File file);

    void onRemoveLayer(GITuple tuple);

    void onMoveLayer(GITuple fromPosition, GITuple toPosition);

    void onPOILayer(GIEditableLayer layer);

//    void onLayerChanged(GITuple fromPosition, GITuple toPosition);
}
