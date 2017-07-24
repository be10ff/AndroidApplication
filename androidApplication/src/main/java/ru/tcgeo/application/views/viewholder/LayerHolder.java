package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class LayerHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvLayerName)
    public TextView tvLayerName;

    @Bind(R.id.cbMarkersSource)
    public CheckBox cbMarkersSource;

    @Bind(R.id.cbLayerVisibility)
    public CheckBox cbLayerVisibility;

    @Bind(R.id.llLayer)
    public View llLayer;

    private LayerHolderCallback callback;

    public LayerHolder(View itemView, LayerHolderCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.callback = callback;
    }

    @OnCheckedChanged(R.id.cbMarkersSource)
    public void onMarkersSourceCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            callback.onMarkersSourceCheckChanged(this, isChecked);
        }
    }

    @OnCheckedChanged(R.id.cbLayerVisibility)
    public void onLayerVisibilityCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            callback.onVisibilityCheckChanged(this, isChecked);
        }
    }

    @OnClick(R.id.tvLayerName)
    public void onSettings() {
        callback.onSettings(this);
    }
}
