package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.MarkerHolder;

/**
 * Created by artem on 14.07.17.
 */

public abstract class MarkerHolderCallback {
    public abstract void onGoToClick(MarkerHolder holder);

    public abstract void onShowDirectiponClick(MarkerHolder holder);
}
