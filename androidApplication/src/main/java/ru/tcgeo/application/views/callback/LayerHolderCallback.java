package ru.tcgeo.application.views.callback;

import android.support.v7.widget.RecyclerView;

import ru.tcgeo.application.gilib.GISQLLayer;
import ru.tcgeo.application.gilib.GITuple;

/**
 * Created by artem on 14.07.17.
 */

public interface LayerHolderCallback {

    void onVisibilityCheckChanged(RecyclerView.ViewHolder holder, boolean isChecked);

    void onLayerName(RecyclerView.ViewHolder holder);

    void onScaleRange(RecyclerView.ViewHolder holder);

    void onRemove(RecyclerView.ViewHolder holder);

    void onMarkersSourceCheckChanged(RecyclerView.ViewHolder holder, boolean isChecked);

    void onMove(GITuple from, GITuple to);

    void onZoomType(RecyclerView.ViewHolder holder, GISQLLayer.GISQLiteZoomingType type);

    void onProjection(RecyclerView.ViewHolder holder, GISQLLayer.GILayerType type);

    void onRatio(RecyclerView.ViewHolder holder);

}
