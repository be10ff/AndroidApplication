package ru.tcgeo.application.views.callback;

import android.support.v7.widget.RecyclerView;

/**
 * Created by artem on 14.07.17.
 */

public interface LayerHolderCallback {
    void onMarkersSourceCheckChanged(RecyclerView.ViewHolder holder, boolean isChecked);

    void onVisibilityCheckChanged(RecyclerView.ViewHolder holder, boolean isChecked);

    void onSettings(RecyclerView.ViewHolder holder);
}
