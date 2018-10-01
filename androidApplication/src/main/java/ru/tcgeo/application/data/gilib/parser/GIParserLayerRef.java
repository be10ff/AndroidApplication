package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;

public class GIParserLayerRef extends GIParser
{
	ru.tcgeo.application.gilib.parser.GIPropertiesEdit m_root;
	GIPropertiesLayerRef m_current;

	public GIParserLayerRef(XmlPullParser parent, ru.tcgeo.application.gilib.parser.GIPropertiesEdit root)
	{
		super(parent);
		section_name = "LayerRef";
		m_root = root;		
		m_current = new GIPropertiesLayerRef();
	}

	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("name"))
			{
				m_current.m_name = m_ParserCurrent.getAttributeValue(i);
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("type"))
			{
				m_current.m_type = m_ParserCurrent.getAttributeValue(i);
			}
		}
	}
	

	
	@Override
	protected void FinishSection()
	{
		m_root.m_Entries.add(m_current);
	}
	//-----------------------------------------------------------------
}