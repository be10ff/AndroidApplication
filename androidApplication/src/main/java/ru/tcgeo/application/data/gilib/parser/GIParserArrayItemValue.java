package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class GIParserArrayItemValue extends GIParser {
	StringBuffer m_value;
	String m_key;
	public GIParserArrayItemValue(XmlPullParser parent, String key, StringBuffer value) 
	{
		super(parent);
		section_name = key;
		m_value = value;
	}
	@Override
	protected void ReadSectionsValues()
	{
		return;
	}
	
	@Override
	protected void ReadSectionText()
	{
		m_value.append(m_ParserCurrent.getText());
		return;
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
