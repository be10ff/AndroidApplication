package ru.tcgeo.application.interfaces;

import ru.tcgeo.application.data.interactors.LoadProjectInteractor;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;

/**
 * Created by abelov on 28.04.16.
 */

public interface MapView {
    void onProject(GIProjectProperties ps);

    void onLayer(LoadProjectInteractor.Layer layer);

    void onComplited();

    void onError();
}
