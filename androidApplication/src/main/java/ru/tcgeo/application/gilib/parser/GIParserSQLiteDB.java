package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.application.gilib.GISQLLayer;


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
                } else {
                    m_root.m_zooming_type = GISQLLayer.GISQLiteZoomingType.AUTO;
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
	protected void readSectionEnties() throws XmlPullParserException 
	{
		return;
	}
	
	@Override
	protected void FinishSection()
	{
		return;
	}

}
