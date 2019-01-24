package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.data.gilib.models.GILonLat;

/**
 * Created by artem on 16.04.18.
 */

public interface LonLatInputCallback {
    void onNewValue(GILonLat point);
}
