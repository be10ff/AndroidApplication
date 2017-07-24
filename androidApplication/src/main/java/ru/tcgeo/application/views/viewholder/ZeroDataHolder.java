package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.ZeroDataHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class ZeroDataHolder extends RecyclerView.ViewHolder {
    private ZeroDataHolderCallback callback;

    public ZeroDataHolder(View itemView, ZeroDataHolderCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.callback = callback;
    }

    @OnClick(R.id.tvEmpty)
    public void onClose() {
        callback.onClick(this);
    }


}