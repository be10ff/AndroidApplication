package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;

public class GIParserMarkers extends GIParser
{
	GIParserMarkers(XmlPullParser parent, GIProjectProperties ps)
	{
		super(parent, ps);
		section_name = "markers";
	}
	
	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("file"))
			{
				m_ps.m_markers = m_ParserCurrent.getAttributeValue(i);
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("source"))
			{
				m_ps.m_markers_source = m_ParserCurrent.getAttributeValue(i);
			}
		}
	}
}
