package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.tcgeo.application.gilib.models.GIEncoding;
//import ru.tcgeo.application.gilib.models.GIIcon;
import ru.tcgeo.application.gilib.GILayer;


public class GIParserLayer extends GIParser
{
	GIPropertiesLayer m_root;
	GIPropertiesLayer m_current;

	public GIParserLayer(XmlPullParser parent, GIPropertiesLayer root)
	{
		super(parent);
		section_name = "Layer";
		m_root = root;
		m_current = new GIPropertiesLayer();
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
				m_current.m_strType = m_ParserCurrent.getAttributeValue(i);
				if(m_current.m_strType.equalsIgnoreCase("vector"))
				{
					m_current.m_type = GILayer.GILayerType.VECTOR_LAYER;
				}
				if(m_current.m_strType.equalsIgnoreCase("raster"))
				{
					m_current.m_type = GILayer.GILayerType.RASTER_LAYER;
				}
				if(m_current.m_strType.equalsIgnoreCase("TILEINDEX"))
				{
					m_current.m_type = GILayer.GILayerType.TILE_LAYER;
				}
				if(m_current.m_strType.equalsIgnoreCase("ON_LINE"))
				{
					m_current.m_type = GILayer.GILayerType.ON_LINE;
				}
				if(m_current.m_strType.equalsIgnoreCase("SQL_LAYER"))
				{
					m_current.m_type = GILayer.GILayerType.SQL_LAYER;
				}
				if(m_current.m_strType.equalsIgnoreCase("SQL_YANDEX_LAYER"))
				{
					m_current.m_type = GILayer.GILayerType.SQL_YANDEX_LAYER;
				}
				if(m_current.m_strType.equalsIgnoreCase("DBASE"))
				{
					m_current.m_type = GILayer.GILayerType.DBASE;
				}
				if(m_current.m_strType.equalsIgnoreCase("XML"))
				{
					m_current.m_type = GILayer.GILayerType.XML;
				}
				if(m_current.m_strType.equalsIgnoreCase("PLIST"))
				{
					m_current.m_type = GILayer.GILayerType.PLIST;
				}
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("enabled"))
			{
				m_current.m_enabled = Boolean.getBoolean(m_ParserCurrent.getAttributeValue(i));
			}
		}
	}

	@Override
	protected void readSectionEnties() throws XmlPullParserException
	{
		String CurrentSectionName = m_ParserCurrent.getName();
		if(CurrentSectionName.equalsIgnoreCase("Source"))
		{
			m_current.m_source = new GISource();
			GIParserSource parser = new GIParserSource(m_ParserCurrent, m_current.m_source);
			m_ParserCurrent = parser.ReadSection();
		}
		if(CurrentSectionName.equalsIgnoreCase("sqlitedb"))
		{
			m_current.m_sqldb = new GISQLDB();
			GIParserSQLiteDB parser = new GIParserSQLiteDB(m_ParserCurrent, m_current.m_sqldb);
			m_ParserCurrent = parser.ReadSection();
		}
		if(CurrentSectionName.equalsIgnoreCase("Style"))
		{
			m_current.m_style = new GIPropertiesStyle();
			GIParserStyle parser = new GIParserStyle(m_ParserCurrent, m_current.m_style);
			m_ParserCurrent = parser.ReadSection();
		}
		if(CurrentSectionName.equalsIgnoreCase("Encoding"))
		{
			m_current.m_encoding = new GIEncoding("CP1251");
			GIParserEncoding parser = new GIParserEncoding(m_ParserCurrent, m_current.m_encoding);
			m_ParserCurrent = parser.ReadSection();	
			m_current.m_encoding = parser.m_root;
		}
		if(CurrentSectionName.equalsIgnoreCase("Range"))
		{
			m_current.m_range = new GIRange();
			GIParserRange parser = new GIParserRange(m_ParserCurrent, m_current.m_range);
			m_ParserCurrent = parser.ReadSection();	
		}
//		if(CurrentSectionName.equalsIgnoreCase("Icon"))
//		{
//			m_current.m_icon = new GIIcon();
//			GIParserIcon parser = new GIParserIcon(m_ParserCurrent, m_current.m_icon);
//			m_ParserCurrent = parser.ReadSection();
//		}

	}
	
	@Override
	protected void FinishSection()
	{
		//m_root.m_Entries.add(m_current);
		m_root.addEntry(m_current);
	}
	//-----------------------------------------------------------------
}
