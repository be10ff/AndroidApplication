package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.AttributesCallback;

/**
 * Created by artem on 14.07.17.
 */

public class AttributesAddHolder extends RecyclerView.ViewHolder {

    private AttributesCallback callback;

    public AttributesAddHolder(View itemView, AttributesCallback callback) {
        super(itemView);
        this.callback = callback;
        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.tvAdd)
    public void onAddClick() {
        callback.onAddClick();
    }
}
