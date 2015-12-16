package ru.tcgeo.application.home_screen;

import ru.tcgeo.application.gilib.GITuple;

/**
 * Created by a_belov on 06.07.15.
 * Class is a wrapper for GITuple to use in ArrayAdapter
 */
public class LayersAdapterItem {
    final public GITuple m_tuple;

    public LayersAdapterItem(GITuple tuple) {
        m_tuple = tuple;
    }

    @Override
    public String toString() {
        return m_tuple.layer.getName();
    }
}
