package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.ProjectHolder;

/**
 * Created by artem on 14.07.17.
 */

public abstract class ProjectsHolderCallback {
    public abstract void onClick(ProjectHolder holder);

    public abstract void onClose();
}
