package ru.tcgeo.application.data.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.application.data.gilib.models.GIPList;
import ru.tcgeo.application.data.gilib.models.Marker;

public class GIParserArrayItem extends GIParserArray {
	Marker m_item;
	String m_looking_for_key;

	GIParserArrayItem(XmlPullParser parent, GIPList list)
	{
		super(parent, list);
		section_name = "dict";
		m_item = new Marker();
		m_looking_for_key = new String();
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
		StringBuffer lookingFor = new StringBuffer();
		if(m_ParserCurrent.getName().equalsIgnoreCase("key"))
		{
			GIParserArrayItemKey parser = new GIParserArrayItemKey(m_ParserCurrent, lookingFor);
			m_ParserCurrent = parser.ReadSection();
			m_looking_for_key = new String(lookingFor);
		}
		if(m_looking_for_key.equalsIgnoreCase("name") && m_ParserCurrent.getName().equalsIgnoreCase("string"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "string", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);
			m_item.name = value;
		}
		if(m_looking_for_key.equalsIgnoreCase("image") && m_ParserCurrent.getName().equalsIgnoreCase("string"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "string", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);
			m_item.image = value;
		}
		if(m_looking_for_key.equalsIgnoreCase("description") && m_ParserCurrent.getName().equalsIgnoreCase("string"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "string", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);
			m_item.description = value;
		}
		if(m_looking_for_key.equalsIgnoreCase("lon") && m_ParserCurrent.getName().equalsIgnoreCase("real"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "real", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);
			m_item.lon = Double.valueOf(value);
		}
		if(m_looking_for_key.equalsIgnoreCase("lat") && m_ParserCurrent.getName().equalsIgnoreCase("real"))
		{
			GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "real", lookingFor);
			m_ParserCurrent = parser_value.ReadSection();
			String value = new String(lookingFor);
			m_item.lat = Double.valueOf(value);
		}
		//if(m_looking_for_key.equalsIgnoreCase("diag") && m_ParserCurrent.getName().equalsIgnoreCase("real"))
		if(m_looking_for_key.equalsIgnoreCase("diag"))
		{
			if(m_ParserCurrent.getName().equalsIgnoreCase("real"))
			{
				GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "real", lookingFor);
				m_ParserCurrent = parser_value.ReadSection();
				String value = new String(lookingFor);
				m_item.diag = Double.valueOf(value);
			}
			if(m_ParserCurrent.getName().equalsIgnoreCase("integer"))
			{
				GIParserArrayItemValue parser_value = new GIParserArrayItemValue(m_ParserCurrent, "integer", lookingFor);
				m_ParserCurrent = parser_value.ReadSection();
				String value = new String(lookingFor);
				m_item.diag = (double) Integer.valueOf(value);
			}

		}
	}

	protected void FinishSection()
	{
		m_List.m_list.add(m_item);
		return;
	}

}
