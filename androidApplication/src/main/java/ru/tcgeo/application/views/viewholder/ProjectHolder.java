package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.ProjectsHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class ProjectHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tvFilePath)
    public TextView tvFilePath;

    @BindView(R.id.ivLoaded)
    public ImageView ivLoaded;

    @BindView(R.id.tvProjectName)
    public TextView tvProjectName;

    @BindView(R.id.tvDescription)
    public TextView tvDescription;

    @BindView(R.id.rlProject)
    public View rlProject;

    private ProjectsHolderCallback callback;

    public ProjectHolder(View itemView, ProjectsHolderCallback callback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.callback = callback;
    }

    @OnClick(R.id.rlProject)
    public void onLoad() {
        callback.onClick(this);
    }


}
