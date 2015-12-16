package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;


public class GIParserPointInfo extends GIParser {
	GIParserPointInfo(XmlPullParser parent, GIProjectProperties ps)
	{
		super(parent, ps);
		ps.m_point_info = "";
		section_name = "PointInfo";
	}
	
	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("file"))
			{
				m_ps.m_point_info = m_ParserCurrent.getAttributeValue(i);
			}
		}
	}
	@Override
	protected void ReadSectionText()
	{
		m_ps.m_point_info = m_ParserCurrent.getText();
	}	
}
