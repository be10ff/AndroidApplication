package ru.tcgeo.application.home_screen;

import ru.tcgeo.application.gilib.GIPList;

/**
 * Created by a_belov on 06.07.15.
 */
public class MarkersAdapterItem {
    final public GIPList.GIMarker m_marker;

    public MarkersAdapterItem(GIPList.GIMarker marker) {
        m_marker = marker;
    }

    @Override
    public String toString() {
        return m_marker.m_name + " " + m_marker.m_lon + ":"
                + m_marker.m_lat;
    }
}
