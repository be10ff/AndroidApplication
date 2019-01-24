package ru.tcgeo.application.views.callback;

import android.support.v7.widget.RecyclerView;

import ru.tcgeo.application.data.gilib.layer.GILayer;
import ru.tcgeo.application.data.gilib.layer.GISQLLayer;

/**
 * Created by artem on 14.07.17.
 */

public interface LayerHolderCallback {

    void onVisibilityCheckChanged(RecyclerView.ViewHolder holder, boolean isChecked);

    void onLayerName(RecyclerView.ViewHolder holder);

    void onScaleRange(RecyclerView.ViewHolder holder);

    void onRemove(GILayer layer);

    void onMarkersSourceCheckChanged(RecyclerView.ViewHolder holder, boolean isChecked);

    void onMove(GILayer from, GILayer to);

    void onZoomType(RecyclerView.ViewHolder holder, GISQLLayer.GISQLiteZoomingType type);

    void onProjection(RecyclerView.ViewHolder holder, GISQLLayer.GILayerType type);

    void onRatio(RecyclerView.ViewHolder holder);

    void onEditable(RecyclerView.ViewHolder holder, GISQLLayer.EditableType type);

    void onSetPoiLayer(RecyclerView.ViewHolder holder, boolean active);

    void onFillColor(RecyclerView.ViewHolder holder);

    void onStrokeColor(RecyclerView.ViewHolder holder);

    void onWidth(RecyclerView.ViewHolder holder);


}
