package ru.tcgeo.application.gilib.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;



public class GIParser 
{
	public final String LOG_TAG = "myLogsParser";
	protected boolean SectionReading;
	protected XmlPullParser m_ParserCurrent;
	protected String section_name;
	protected GIProjectProperties m_ps;

	public GIParser (XmlPullParser parent)
	{
		SectionReading = true;
		m_ParserCurrent = parent;
		System.setProperty("true", "true");
	}
	public GIParser (XmlPullParser parent, GIProjectProperties projectProperties)
	{
		this(parent);
		section_name = "project";
		m_ps = projectProperties;

	}
	//--------------------------------------------------
	protected void ReadSectionText()
	{
		return;
	}
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("name"))
			{
				m_ps.m_name = m_ParserCurrent.getAttributeValue(i);
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("id"))
			{
				m_ps.m_id = Integer.valueOf(m_ParserCurrent.getAttributeValue(i));
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("SaveAs"))
			{
				m_ps.m_SaveAs = m_ParserCurrent.getAttributeValue(i);
			}
		}
	}
	protected void readSectionEnties() throws XmlPullParserException
	{
		String CurrentSectionName = m_ParserCurrent.getName();
		if(CurrentSectionName.equalsIgnoreCase("Description"))
		{
			GIParserDescription parser = new GIParserDescription(m_ParserCurrent, m_ps);
			m_ParserCurrent = parser.ReadSection();
		}
		if(CurrentSectionName.equalsIgnoreCase("Bounds"))
		{
			GIParserBounds parser = new GIParserBounds(m_ParserCurrent, m_ps);
			m_ParserCurrent = parser.ReadSection();
		}
		if(CurrentSectionName.equalsIgnoreCase("Map"))
		{
			GIParserMap parser = new GIParserMap(m_ParserCurrent, m_ps);
			m_ParserCurrent = parser.ReadSection();
		}
		if(CurrentSectionName.equalsIgnoreCase("Markers"))
		{
			GIParserMarkers parser = new GIParserMarkers(m_ParserCurrent, m_ps);
			m_ParserCurrent = parser.ReadSection();
		}
		if(CurrentSectionName.equalsIgnoreCase("PointInfo"))
		{
			GIParserPointInfo parser = new GIParserPointInfo(m_ParserCurrent, m_ps);
			m_ParserCurrent = parser.ReadSection();
		}
//		if(CurrentSectionName.equalsIgnoreCase("Search"))
//		{
//			GIParserSearch parser = new GIParserSearch(m_ParserCurrent, m_ps);
//			m_ParserCurrent = parser.ReadSection();
//		}
//		if(CurrentSectionName.equalsIgnoreCase("Package"))
//		{
//			GIParserPackage parser = new GIParserPackage(m_ParserCurrent, m_ps);
//			m_ParserCurrent = parser.ReadSection();
//		}
		if(CurrentSectionName.equalsIgnoreCase("Edit"))
		{
			GIParserEdit parser = new GIParserEdit(m_ParserCurrent, m_ps);
			m_ParserCurrent = parser.ReadSection();
		}
	}
	protected void FinishSection()
	{
		return;
	}
	//-----------------------------------------------------------------
	public XmlPullParser ReadSection() throws XmlPullParserException
	{
		if(m_ParserCurrent.getEventType() == XmlPullParser.START_TAG)
		{
			if(m_ParserCurrent.getName().equalsIgnoreCase(section_name))
			{
				ReadSectionsValues();
				
				SectionReading = true;
				//Section loop
				while(SectionReading && (m_ParserCurrent.getEventType() != XmlPullParser.END_DOCUMENT))
				{
					try
					{
						m_ParserCurrent.next();
					}
					catch(IOException e){/*Log.d(LOG_TAG, "Exception in " + section_name);*/}
					if(m_ParserCurrent.getEventType() == XmlPullParser.START_TAG)
					{
						readSectionEnties();
						continue;

					}
					if(m_ParserCurrent.getEventType() == XmlPullParser.TEXT)
					{
						ReadSectionText();
						continue;
					}

					if(m_ParserCurrent.getEventType() == XmlPullParser.END_TAG)
					{
						if(m_ParserCurrent.getName().equalsIgnoreCase(section_name))
						{
							SectionReading = false;
							//TODO check here
							/*try
							{
								m_ParserCurrent.next();
							}
							catch(IOException e){};*/
						}
						continue;
					}
				}
				FinishSection();
			}
		}
		return m_ParserCurrent;
	}
	public XmlPullParser ReadInfo() throws XmlPullParserException
	{
		if(m_ParserCurrent.getEventType() == XmlPullParser.START_TAG)
		{
			if(m_ParserCurrent.getName().equalsIgnoreCase(section_name))
			{
				ReadSectionsValues();
			}
		}
		return m_ParserCurrent;
	}
}

