package ru.tcgeo.application.data.wkt;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import ru.tcgeo.application.utils.MapUtils;


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
                        ((GIXMLTrack) m_geometry).m_points.add(point);
                    }

					int  iiiii = 0;

                    ArrayList<GI_WktGeometry> points = ((GIXMLTrack)m_geometry).m_points;

                    if(points.size() > 2) {
                        ArrayList<GI_WktGeometry> filtered = new ArrayList<GI_WktGeometry>();
                        filtered.add(points.get(0));
                        for (int k = 1; k < points.size()- 2; k++){
                            GI_WktPoint a = (GI_WktPoint)points.get(k-1);
                            GI_WktPoint b = (GI_WktPoint)points.get(k);
                            GI_WktPoint c = (GI_WktPoint)points.get(k+1);
                            double ab = MapUtils.GetDistance(a.LonLat(), b.LonLat());
                            double bc = MapUtils.GetDistance(b.LonLat(), c.LonLat());
                            double ac = MapUtils.GetDistance(a.LonLat(), c.LonLat());
							//!(ac < ab && ac < bc)
                            if(ac > ab ||  ac > bc){
                                filtered.add(points.get(k));
                            } else {
                                int j = 0;
                            }

                        }
                        filtered.add(points.get(points.size()- 1));

                        ((GIXMLTrack)m_geometry).m_points = filtered;
                    }
					reader.close();
				} 
				catch (Exception e)
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
