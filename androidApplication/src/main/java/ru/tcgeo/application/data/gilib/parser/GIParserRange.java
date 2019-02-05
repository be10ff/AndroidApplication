package ru.tcgeo.application.data.gilib.parser;

import org.xmlpull.v1.XmlPullParser;


public class GIParserRange extends GIParser {
    GIRange m_root;

    public GIParserRange(XmlPullParser parent, GIRange root) {
        super(parent);
        section_name = "Range";
        m_root = root;
    }

    @Override
    protected void ReadSectionsValues() {
        for (int i = 0; i < m_ParserCurrent.getAttributeCount(); i++) {
            if (m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("from")) {
                if (!m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase("nan")) {
                    m_root.m_from = Integer.parseInt(m_ParserCurrent.getAttributeValue(i));
                } else {
                    m_root.m_from = -1;
                }
            }
            if (m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("to")) {
                if (!m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase("nan")) {
                    m_root.m_to = Integer.parseInt(m_ParserCurrent.getAttributeValue(i));
                } else {
                    m_root.m_to = -1;
                }
            }
        }
    }

    @Override
    protected void readSectionEnties() {
        return;
    }

    @Override
    protected void FinishSection() {
        return;
    }
    //-----------------------------------------------------------------

}
