package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class GIParserDescription extends GIParser
{

	GIParserDescription(XmlPullParser parent, GIProjectProperties ps)
	{
		super(parent, ps);
		section_name = "description";
	}
	
	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("text"))
			{
				m_ps.m_decription = m_ParserCurrent.getAttributeValue(i);
			}
		}
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		return;
	}
}
