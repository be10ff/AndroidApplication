package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.EditableLayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class EdiableLayerHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvEditableLayerName)
    public TextView tvEditableLayerName;

    private EditableLayerHolderCallback callback;

    public EdiableLayerHolder(View itemView, EditableLayerHolderCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.callback = callback;
    }

    @OnClick(R.id.tvEditableLayerName)
    public void onStartEdit() {
        callback.onStartEdit(this);
    }


}
