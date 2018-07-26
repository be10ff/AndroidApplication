package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class ProjectLayerHeaderHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvFilePath)
    public TextView tvFilePath;

    @BindView(R.id.etProjectName)
    public EditText etProjectName;

    @BindView(R.id.etDescription)
    public EditText etDescription;

    private LayerHolderCallback callback;

    public ProjectLayerHeaderHolder(View itemView, LayerHolderCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.callback = callback;
    }
}
