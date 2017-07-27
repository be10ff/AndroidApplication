package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.ProjectHolder;

/**
 * Created by artem on 14.07.17.
 */

public interface ProjectsHolderCallback {
    void onClick(ProjectHolder holder);

    void onClose();
}
