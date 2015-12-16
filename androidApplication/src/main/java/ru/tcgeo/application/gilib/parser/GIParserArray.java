package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.application.gilib.GIPList;

public class GIParserArray extends GIParser
{
	protected GIPList m_List;
	public GIParserArray(XmlPullParser parent, GIPList list)
	{
		super(parent);
		m_List = list;
		section_name = "array";
	}

	@Override
	protected void ReadSectionsValues()
	{
		/*for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("file"))
			{
				m_ps.m_search_file = m_ParserCurrent.getAttributeValue(i);
			}
		}*/
		return;
	}

	@Override
	protected void ReadSectionText()
	{
		/*m_ps.m_search_body = m_ParserCurrent.getText();*/
		return;
	}

	@Override
	protected void readSectionEnties() throws XmlPullParserException
	{
		if(m_ParserCurrent.getName().equalsIgnoreCase("dict"))
		{
			GIParserArrayItem parser = new GIParserArrayItem(m_ParserCurrent, m_List);
			m_ParserCurrent = parser.ReadSection();
		}
	}	
	
	protected void FinishSection()
	{
		return;
	}
}
