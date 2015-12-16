package ru.tcgeo.application.gilib.parser;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.application.gilib.models.GIColor;
//import ru.tcgeo.application.gilib.models.GIIcon;


public class GIParserStyle extends GIParser
{
	GIPropertiesStyle m_root;

	public GIParserStyle(XmlPullParser parent, GIPropertiesStyle root) 
	{
		super(parent);
		section_name = "Style";
		m_root = root;		
	}

	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("type"))
			{
				m_root.m_type = m_ParserCurrent.getAttributeValue(i);
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("lineWidth"))
			{
				m_root.m_lineWidth = Double.parseDouble(m_ParserCurrent.getAttributeValue(i));
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("opacity"))
			{
				m_root.m_opacity = Double.parseDouble(m_ParserCurrent.getAttributeValue(i));
			}
		}
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		String CurrentSectionName = m_ParserCurrent.getName();
		if(CurrentSectionName.equalsIgnoreCase("Color"))
		{
			if(m_root.m_colors == null)
			{
				m_root.m_colors = new ArrayList<GIColor>();
			}
			GIParserColor parser = new GIParserColor(m_ParserCurrent, m_root.m_colors);
			m_ParserCurrent = parser.ReadSection();
		}
//		if(CurrentSectionName.equalsIgnoreCase("Icon"))
//		{
//			m_root.m_icon = new GIIcon();
//			GIParserIcon parser = new GIParserIcon(m_ParserCurrent, m_root.m_icon);
//			m_ParserCurrent = parser.ReadSection();
//		}
	}
	
	@Override
	protected void FinishSection()
	{
		return;
	}
	//-----------------------------------------------------------------

}
