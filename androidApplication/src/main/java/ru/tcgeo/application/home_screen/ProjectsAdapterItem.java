package ru.tcgeo.application.home_screen;

import ru.tcgeo.application.gilib.parser.GIProjectProperties;

/**
 * Created by a_belov on 06.07.15.
 */
public class ProjectsAdapterItem {
    final public GIProjectProperties m_project_settings;

    public ProjectsAdapterItem(GIProjectProperties proj) {
        m_project_settings = proj;
    }

    @Override
    public String toString() {
        return m_project_settings.m_name;
    }
}
