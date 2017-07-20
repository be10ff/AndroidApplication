package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.MarkerCallback;

/**
 * Created by artem on 14.07.17.
 */

public class MarkerHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvDescription)
    public EditText tvDescription;

    @Bind(R.id.ivDirecton)
    public ImageView ivDirecton;

    private MarkerCallback callback;

    public MarkerHolder(View itemView, MarkerCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.callback = callback;
    }

}
