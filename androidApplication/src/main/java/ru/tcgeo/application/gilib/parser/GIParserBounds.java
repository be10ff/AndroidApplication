package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.application.gilib.models.GIProjection;


public class GIParserBounds extends GIParser
{

	GIParserBounds(XmlPullParser parent, GIProjectProperties ps)
	{
		super(parent, ps);
		section_name = "bounds";
	}
	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("projection"))
			{
				m_ps.m_str_projection = m_ParserCurrent.getAttributeValue(i);
				if(m_ps.m_str_projection.equalsIgnoreCase("WGS84"))
				{
					m_ps.m_projection = GIProjection.WGS84();
				}
				if(m_ps.m_str_projection.equalsIgnoreCase("World Mercator"))
				{
					m_ps.m_projection = GIProjection.WorldMercator(); 
				}
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("top"))
			{
				m_ps.m_top = Double.valueOf(m_ParserCurrent.getAttributeValue(i));
			}
			else if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("bottom"))
			{
				m_ps.m_bottom = Double.valueOf(m_ParserCurrent.getAttributeValue(i));
			}
			else if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("left"))
			{
				m_ps.m_left = Double.valueOf(m_ParserCurrent.getAttributeValue(i));
			}
			else if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("right"))
			{
				m_ps.m_right = Double.valueOf(m_ParserCurrent.getAttributeValue(i));
			}
		}
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		return;
	}	

}
