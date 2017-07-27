package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.AttributesHolder;

/**
 * Created by artem on 14.07.17.
 */

public interface AttributesCallback {
    void onClick();

    void onAddClick();

    void onFieldChanged(AttributesHolder holder);
}
