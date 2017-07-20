package ru.tcgeo.application.home_screen.adapter;

import ru.tcgeo.application.gilib.models.Marker;

/**
 * Created by a_belov on 06.07.15.
 */
public class MarkersAdapterItem {
    final public Marker m_marker;

    public MarkersAdapterItem(Marker marker) {
        m_marker = marker;
    }

    @Override
    public String toString() {
        return m_marker.name + " " + m_marker.lon + ":"
                + m_marker.lat;
    }
}
