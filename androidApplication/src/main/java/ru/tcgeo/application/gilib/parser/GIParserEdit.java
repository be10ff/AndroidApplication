package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class GIParserEdit extends GIParser
{
	//GIPropertiesEdit m_current;
	
	public GIParserEdit(XmlPullParser parent, GIProjectProperties ps)
	{
		super(parent, ps);
		section_name = "Edit";
		//m_current = new GIPropertiesEdit();
	}
	@Override
	protected void ReadSectionsValues()
	{
		m_ps.m_Edit = new GIPropertiesEdit();
		return;
	}
	@Override
	protected void readSectionEnties() throws XmlPullParserException
	{
		String CurrentSectionName = m_ParserCurrent.getName();
		if(CurrentSectionName.equalsIgnoreCase("LayerRef"))
		{
			GIParserLayerRef parser = new GIParserLayerRef(m_ParserCurrent, m_ps.m_Edit);
			m_ParserCurrent = parser.ReadSection();
		}

	}
	@Override
	protected void FinishSection()
	{
			//m_ps.m_Entries.add(m_current);

	}
}
