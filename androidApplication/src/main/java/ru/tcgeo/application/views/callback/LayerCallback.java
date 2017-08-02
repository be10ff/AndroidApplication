package ru.tcgeo.application.views.callback;

import java.io.File;

import ru.tcgeo.application.gilib.GITuple;

/**
 * Created by artem on 14.07.17.
 */

public interface LayerCallback {
    void onMarkersSourceCheckChanged(GITuple tuple, boolean isChecked);

    void onVisibilityCheckChanged(GITuple tuple, boolean isChecked);

    void onSettings(GITuple tuple);

    GITuple onAddLayer(File file);
}