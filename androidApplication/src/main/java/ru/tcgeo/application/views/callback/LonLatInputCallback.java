package ru.tcgeo.application.views.callback;

import android.location.Location;

import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.wkt.GI_WktPoint;

/**
 * Created by artem on 16.04.18.
 */

public interface LonLatInputCallback {
    void onNewValue(GILonLat point);
}
