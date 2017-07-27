package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.gilib.models.Marker;

/**
 * Created by artem on 14.07.17.
 */

public interface MarkerCallback {
    void onGoToClick(Marker marker);

    void onShowDirectiponClick(Marker marker, boolean show);
}
