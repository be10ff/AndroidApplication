package ru.tcgeo.application.home_screen.adapter;

import ru.tcgeo.application.layer.GIEditableLayer;

/**
 * Created by a_belov on 06.07.15.
 */
public class EditableLayersAdapterItem {
    final public GIEditableLayer m_layer;

    public EditableLayersAdapterItem(GIEditableLayer layer) {
        m_layer = layer;
    }

    @Override
    public String toString() {
        return m_layer.getName();
    }
}
