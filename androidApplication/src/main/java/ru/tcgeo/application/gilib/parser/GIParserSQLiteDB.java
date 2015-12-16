package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


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
				m_root.m_zoom_type = m_ParserCurrent.getAttributeValue(i);
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
