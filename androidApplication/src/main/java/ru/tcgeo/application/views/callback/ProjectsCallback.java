package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.gilib.parser.GIProjectProperties;

/**
 * Created by artem on 14.07.17.
 */

public interface ProjectsCallback {
    void onClick(GIProjectProperties project);

    void onNewProject();
}
