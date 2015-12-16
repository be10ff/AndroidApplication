package ru.tcgeo.application.wkt;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import ru.tcgeo.application.gilib.GIEditableLayer;
import ru.tcgeo.application.gilib.gps.GIXMLTrack;


public class GIGPSParser {

	public final String LOG_TAG = "myLogsParser";
	protected boolean SectionReading;
	protected XmlPullParser m_ParserCurrent;
	protected String section_name;
	GIEditableLayer m_layer;

	public GIGPSParser (XmlPullParser parent)
	{
		SectionReading = true;
		m_ParserCurrent = parent;
		section_name = "Geometries";
		System.setProperty("true", "true");
	}
	public GIGPSParser (XmlPullParser parent, GIEditableLayer layer)
	{
		this(parent);
		m_layer = layer;

	}

	//--------------------------------------------------
	protected void ReadSectionText()
	{
		return;
	}
	protected void ReadSectionsValues()
	{

	}
	protected void readSectionEnties() throws XmlPullParserException 
	{
		String CurrentSectionName = m_ParserCurrent.getName();
		if(CurrentSectionName.equalsIgnoreCase("Point"))
		{
			GI_WktPoint point = new GI_WktPoint();
			point.m_attributes = new HashMap<String, GIDBaseField>();
			GIGPSParserPoint parser = new GIGPSParserPoint(m_ParserCurrent, point);
			m_ParserCurrent = parser.ReadSection();
			m_layer.m_shapes.add(point);
		}
		if(CurrentSectionName.equalsIgnoreCase("Track"))
		{
			GIXMLTrack track = new GIXMLTrack();
			track.m_attributes = new HashMap<String, GIDBaseField>();
			GIGPSParserTrack parser = new GIGPSParserTrack(m_ParserCurrent, track);
			m_ParserCurrent = parser.ReadSection();
			m_layer.m_shapes.add(track);
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
