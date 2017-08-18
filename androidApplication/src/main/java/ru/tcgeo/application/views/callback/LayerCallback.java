package ru.tcgeo.application.views.callback;

import java.io.File;

import ru.tcgeo.application.gilib.GIEditableLayer;
import ru.tcgeo.application.gilib.GILayer;

/**
 * Created by artem on 14.07.17.
 */

public interface LayerCallback {
    void onMarkersSourceCheckChanged(GILayer tuple, boolean isChecked);

    void onImmediatelyChange();

    GILayer onAddLayer(File file);

    void onRemoveLayer(GILayer tuple);

    void onMoveLayer(GILayer fromPosition, GILayer toPosition);

    void onPOILayer(GIEditableLayer layer);

}
