package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.gilib.models.Marker;

/**
 * Created by artem on 14.07.17.
 */

public abstract class MarkerCallback {
    public abstract void onGoToClick(Marker marker);

    public abstract void onShowDirectiponClick(Marker marker, boolean show);
}
