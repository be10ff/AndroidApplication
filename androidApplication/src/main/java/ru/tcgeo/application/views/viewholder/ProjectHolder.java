package ru.tcgeo.application.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.ProjectsHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class ProjectHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvFilePath)
    public TextView tvFilePath;

    @Bind(R.id.ivLoaded)
    public ImageView ivLoaded;

    @Bind(R.id.tvProjectName)
    public TextView tvProjectName;

    @Bind(R.id.rlProject)
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
