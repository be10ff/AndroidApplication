package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class GIParserSource extends GIParser {
	GISource m_root;
	//SourceProperties m_current;

	public GIParserSource(XmlPullParser parent, GISource root)
	{
		super(parent);
		section_name = "Source";
		m_root = root;		
		//m_current = new IconProperties();
	}
	
	@Override	
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("location"))
			{
				m_root.m_location = m_ParserCurrent.getAttributeValue(i);
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("name"))
			{
				m_root.m_name = m_ParserCurrent.getAttributeValue(i);
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
		//m_root.m_icon = m_current;
		return;
	}
	//-----------------------------------------------------------------

}
