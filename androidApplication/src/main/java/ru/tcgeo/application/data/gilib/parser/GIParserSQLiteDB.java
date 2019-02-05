package ru.tcgeo.application.data.gilib.parser;

import org.xmlpull.v1.XmlPullParser;

import ru.tcgeo.application.data.gilib.layer.GISQLLayer;


public class GIParserSQLiteDB extends GIParser
{
	GISQLDB m_root;
	
	public GIParserSQLiteDB(XmlPullParser parent, GISQLDB root)
	{
		super(parent);
		section_name = "sqlitedb";
		m_root = root;		
	}
	
	@Override	
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("zoom_type"))
			{

                if (m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase(GISQLLayer.GISQLiteZoomingType.SMART.name())) {
                    m_root.m_zooming_type = GISQLLayer.GISQLiteZoomingType.SMART;
				} else if (m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase(GISQLLayer.GISQLiteZoomingType.ADAPTIVE.name())) {
					m_root.m_zooming_type = GISQLLayer.GISQLiteZoomingType.ADAPTIVE;
				} else if (m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase(GISQLLayer.GISQLiteZoomingType.AUTO.name())) {
					m_root.m_zooming_type = GISQLLayer.GISQLiteZoomingType.AUTO;
				} else {
					m_root.m_zooming_type = GISQLLayer.GISQLiteZoomingType.SMART;
				}
            }
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("min"))
			{
				m_root.m_min_z = Integer.valueOf(m_ParserCurrent.getAttributeValue(i));
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("max"))
			{
				m_root.m_max_z = Integer.valueOf(m_ParserCurrent.getAttributeValue(i));
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("ratio"))
			{
				m_root.mRatio = Integer.valueOf(m_ParserCurrent.getAttributeValue(i));
			}
		}
	}
	
	@Override
	protected void readSectionEnties() {
		return;
	}
	
	@Override
	protected void FinishSection()
	{
		return;
	}

}
