package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class GIParserMap extends GIParser {

	public GIParserMap(XmlPullParser parent, GIProjectProperties ps)
	{
		super(parent, ps);
		section_name = "map";
	}
	@Override
	protected void ReadSectionsValues()
	{
		return;
	}
	@Override
	protected void readSectionEnties() throws XmlPullParserException
	{
		if(m_ParserCurrent.getName().equalsIgnoreCase("group"))
		{
			GIParserGroup parser = new GIParserGroup(m_ParserCurrent, null, m_ps);
			m_ParserCurrent = parser.ReadSection();
		}
	}	

}
