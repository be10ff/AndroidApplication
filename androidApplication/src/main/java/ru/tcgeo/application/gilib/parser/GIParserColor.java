package ru.tcgeo.application.gilib.parser;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.application.gilib.models.GIColor;


public class GIParserColor extends GIParser
{
	GIColor m_root;
	GIColor m_current;
	ArrayList<GIColor> m_colors;
	
	public GIParserColor(XmlPullParser parent, GIColor root) 
	{
		super(parent);
		section_name = "Color";
		m_root = root;
		m_colors = null;
		m_current = new GIColor();
	}
	
	public GIParserColor(XmlPullParser parent, ArrayList<GIColor> colors) 
	{
		super(parent);
		section_name = "Color";
		m_root = null;	
		m_colors = colors;
		m_current = new GIColor();
	}
	
	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("description"))
			{
				m_current.m_description = m_ParserCurrent.getAttributeValue(i);
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("name"))
			{
				m_current.m_name = m_ParserCurrent.getAttributeValue(i);
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("r"))
			{
				m_current.m_red = Integer.parseInt(m_ParserCurrent.getAttributeValue(i));
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("g"))
			{
				m_current.m_green = Integer.parseInt(m_ParserCurrent.getAttributeValue(i));
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("b"))
			{
				m_current.m_blue = Integer.parseInt(m_ParserCurrent.getAttributeValue(i));
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("a"))
			{
				m_current.m_alpha = Integer.parseInt(m_ParserCurrent.getAttributeValue(i));
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
		if(m_root != null)
		{
			//m_root = m_current;
			m_root.m_alpha = m_current.m_alpha;
			m_root.m_blue = m_current.m_blue;
			m_root.m_description = m_current.m_description;
			m_root.m_green = m_current.m_green;
			m_root.m_name = m_current.m_name;
			m_root.m_red = m_current.m_red;
		}
		else if(m_colors != null)
		{
			m_colors.add(m_current);
		}
	}
	//-----------------------------------------------------------------
}
