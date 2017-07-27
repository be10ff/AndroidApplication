package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.MarkerHolder;

/**
 * Created by artem on 14.07.17.
 */

public interface MarkerHolderCallback {
    void onGoToClick(MarkerHolder holder);

    void onShowDirectiponClick(MarkerHolder holder);

    void onClose();
}
