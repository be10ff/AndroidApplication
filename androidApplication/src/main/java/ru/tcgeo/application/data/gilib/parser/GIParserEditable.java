package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.application.gilib.layer.GILayer;


public class GIParserEditable extends GIParser {
    GIEditable m_root;

    public GIParserEditable(XmlPullParser parent, GIEditable root) {
        super(parent);
        section_name = "Editable";
        m_root = root;
    }

    @Override
    protected void ReadSectionsValues() {
        for (int i = 0; i < m_ParserCurrent.getAttributeCount(); i++) {
            if (m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("type")) {
                if (m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase(GILayer.EditableType.LINE.name())) {
                    m_root.enumType = GILayer.EditableType.LINE;
                } else if (m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase(GILayer.EditableType.POI.name())) {
                    m_root.enumType = GILayer.EditableType.POI;
                } else if (m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase(GILayer.EditableType.POLYGON.name())) {
                    m_root.enumType = GILayer.EditableType.POLYGON;
                } else if (m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase(GILayer.EditableType.TRACK.name())) {
                    m_root.enumType = GILayer.EditableType.TRACK;
                } else {
                    m_root.enumType = GILayer.EditableType.UNSET;
                }
            }
            if (m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("active")) {
                m_root.active = Boolean.getBoolean(m_ParserCurrent.getAttributeValue(i));
            }
        }
    }

    @Override
    protected void readSectionEnties() throws XmlPullParserException {
        return;
    }

    @Override
    protected void FinishSection() {
        return;
    }

}
