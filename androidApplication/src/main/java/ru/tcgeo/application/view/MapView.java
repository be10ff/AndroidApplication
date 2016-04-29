package ru.tcgeo.application.view;

import ru.tcgeo.application.gilib.parser.GIProjectProperties;

/**
 * Created by abelov on 28.04.16.
 */
public interface MapView {
    void onMapLoaded(GIProjectProperties ps);
    void onError();
}
