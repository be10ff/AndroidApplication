package ru.tcgeo.application.views.callback;

import java.io.File;

import ru.tcgeo.application.gilib.GITuple;

/**
 * Created by artem on 14.07.17.
 */

public abstract class LayerCallback {
    public abstract void onMarkersSourceCheckChanged(GITuple tuple, boolean isChecked);

    public abstract void onVisibilityCheckChanged(GITuple tuple, boolean isChecked);

    public abstract void onSettings(GITuple tuple);

    public abstract GITuple onAddLayer(File file);
}
