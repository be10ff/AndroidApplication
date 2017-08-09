package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */
@Deprecated
public class OldXMLLayerHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.etLayerName)
    public TextView tvLayerName;

    @Bind(R.id.cbMarkersSource)
    public CheckBox cbMarkersSource;

    @Bind(R.id.cbLayerVisibility)
    public CheckBox cbLayerVisibility;

    @Bind(R.id.llLayer)
    public View llLayer;

    private LayerHolderCallback callback;

    public OldXMLLayerHolder(View itemView, LayerHolderCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.callback = callback;
    }

    @OnCheckedChanged(R.id.cbMarkersSource)
    public void onMarkersSourceCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        callback.onMarkersSourceCheckChanged(this, isChecked);
    }

    @OnCheckedChanged(R.id.cbLayerVisibility)
    public void onLayerVisibilityCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        callback.onVisibilityCheckChanged(this, isChecked);
    }
}
