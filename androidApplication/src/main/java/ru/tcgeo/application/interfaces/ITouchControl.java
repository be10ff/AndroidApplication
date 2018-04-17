package ru.tcgeo.application.interfaces;

import ru.tcgeo.application.data.GIEditingStatus;

/**
 * Created by artem on 17.04.18.
 */

public interface ITouchControl {
    GIEditingStatus getState();

    void setState(GIEditingStatus status);
}
