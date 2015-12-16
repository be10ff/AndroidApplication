package ru.tcgeo.application.gilib.parser;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class GIParserArrayItemKey extends GIParser {
	StringBuffer m_item;
	String m_key;
	public GIParserArrayItemKey(XmlPullParser parent, StringBuffer item) 
	{
		super(parent);
		section_name = "key";
		m_item = item;
	}
	@Override
	protected void ReadSectionsValues()
	{
		return;
	}
	
	@Override
	protected void ReadSectionText()
	{
		m_item.append(m_ParserCurrent.getText());
		
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
