package ru.tcgeo.application.views.callback;

import android.location.Location;

/**
 * Created by artem on 16.04.18.
 */

public interface LocationCallback {
    void onLocationChanged(Location location);
}
