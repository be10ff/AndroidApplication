package ru.tcgeo.application.wkt;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class GIGPSParserPoint extends GIGPSParser
{
	GI_WktGeometry m_geometry;

	public GIGPSParserPoint(XmlPullParser parent, GI_WktGeometry geometry) 
	{
		super(parent);
		section_name = "Point";
		m_geometry = geometry;
	}
	@Override
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("id"))
			{
				m_geometry.m_ID = Integer.valueOf(m_ParserCurrent.getAttributeValue(i));
			}
			else if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("Geometry"))
			{
				GIWKTParser.ReadGeometryFromWKT(m_geometry, m_ParserCurrent.getAttributeValue(i));
			}
			else
			{
				GIDBaseField field = new GIDBaseField();
				field.m_name = m_ParserCurrent.getAttributeName(i);
				field.m_value = m_ParserCurrent.getAttributeValue(i);
				m_geometry.m_attributes.put(m_ParserCurrent.getAttributeName(i), field);
			}
		}
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		return;
	}
}
