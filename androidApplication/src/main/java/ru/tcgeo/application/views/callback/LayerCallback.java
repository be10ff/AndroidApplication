package ru.tcgeo.application.views.callback;

import java.io.File;

import ru.tcgeo.application.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.gilib.layer.GILayer;

/**
 * Created by artem on 14.07.17.
 */

public interface LayerCallback {
    void onMarkersSourceCheckChanged(GILayer tuple, boolean isChecked);

    void onImmediatelyChange();

    GILayer onAddLayer(File file);

//    GILayer onAddLayer(GILayer tuple);

    GILayer onAddLayer(GILayer.EditableType type, String name);


    void onRemoveLayer(GILayer tuple);

    void onMoveLayer(GILayer fromPosition, GILayer toPosition);

    void onPOILayer(GIEditableLayer layer);

}
