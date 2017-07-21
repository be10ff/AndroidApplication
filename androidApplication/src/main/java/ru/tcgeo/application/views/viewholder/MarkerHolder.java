package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.MarkerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class MarkerHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvDescription)
    public TextView tvDescription;

    @Bind(R.id.ivDirecton)
    public ImageView ivDirecton;
    private MarkerHolderCallback callback;

    public MarkerHolder(View itemView, MarkerHolderCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.callback = callback;
    }

    @OnClick(R.id.flDirection)
    public void onDirection() {
        callback.onShowDirectiponClick(this);
    }

    @OnClick(R.id.tvDescription)
    public void onDescriptionClick() {
        callback.onGoToClick(this);
    }

}
