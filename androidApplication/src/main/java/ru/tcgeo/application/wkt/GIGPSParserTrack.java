package ru.tcgeo.application.wkt;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ru.tcgeo.application.gilib.gps.GIXMLTrack;


public class GIGPSParserTrack extends GIGPSParser
{

	GI_WktGeometry m_geometry;

	public GIGPSParserTrack(XmlPullParser parent, GI_WktGeometry geometry) 
	{
		super(parent);
		section_name = "Track";
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
				((GIXMLTrack)m_geometry).m_file = m_ParserCurrent.getAttributeValue(i);
				File m_input_file = new File(((GIXMLTrack)m_geometry).m_file);

				try 
				{
					BufferedReader reader = new BufferedReader(new FileReader(m_input_file));
					String line = "";
					while((line = reader.readLine()) != null)
					{
						GI_WktGeometry point = GIWKTParser.CreateGeometryFromWKT(line);
						((GIXMLTrack)m_geometry).m_points.add((GI_WktPoint) point);
					}
					reader.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
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
