package ru.tcgeo.application.views.callback;

import android.support.v7.widget.RecyclerView;

/**
 * Created by artem on 14.07.17.
 */

public interface LayerHolderCallback {

    void onVisibilityCheckChanged(RecyclerView.ViewHolder holder, boolean isChecked);

    void onLayerName(RecyclerView.ViewHolder holder);

    void onScaleRange(RecyclerView.ViewHolder holder);

    void onMoveUp(RecyclerView.ViewHolder holder);

    void onMoveDown(RecyclerView.ViewHolder holder);

    void onMoveRemove(RecyclerView.ViewHolder holder);

    void onMarkersSourceCheckChanged(RecyclerView.ViewHolder holder, boolean isChecked);

}
