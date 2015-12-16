package ru.tcgeo.application.wkt;

import java.util.ArrayList;

import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.script.GIScriptQueue;


public class GIWKTParser 
{
	String m_text;
	static GIWKTDescription m_geometry_description;

	public static GI_WktGeometry CreateGeometryFromWKT(String wkt_text)
	{
		m_geometry_description = new GIWKTDescription();
		GIScriptQueue queue = new GIScriptQueue(wkt_text);
		m_geometry_description.m_block =  Read(queue);
		if(m_geometry_description.m_str_type != null)
		{
			if(m_geometry_description.m_str_type.m_type.equalsIgnoreCase("POINT"))
			{
				GI_WktPoint point = new GI_WktPoint();
				point.m_status = GI_WktGeometry.GIWKTGeometryStatus.SAVED;
				GIWKTBlock block = (GIWKTBlock)m_geometry_description.m_block;
				ArrayList<GIWKTDescription> array = block.m_points;
				GIWKTVertex vertex = (GIWKTVertex)array.get(0);
				GIWKTDigit lon = (GIWKTDigit)vertex.m_data.get(0);
				GIWKTDigit lat = (GIWKTDigit)vertex.m_data.get(1);
				point.m_lon = lon.m_value;
				point.m_lat = lat.m_value;
				GILonLat in_map = GIProjection.ReprojectLonLat(point.LonLat(), GIProjection.WGS84(), GIProjection.WorldMercator());
				point.m_lon_in_map_projection = in_map.lon();
				point.m_lat_in_map_projection = in_map.lat();
				return point;
			}
			else if(m_geometry_description.m_str_type.m_type.equalsIgnoreCase("LINESTRING"))
			{
				GI_WktLinestring line = new GI_WktLinestring();
				line.m_status = GI_WktGeometry.GIWKTGeometryStatus.SAVED;
				GIWKTBlock block = (GIWKTBlock)m_geometry_description.m_block;
				ArrayList<GIWKTDescription> array = block.m_points;
				for(int i = 0; i < array.size(); i++)
				{
					GI_WktPoint point = new GI_WktPoint();
					GIWKTVertex vertex = (GIWKTVertex)array.get(i);
					GIWKTDigit lon = (GIWKTDigit)vertex.m_data.get(0);
					GIWKTDigit lat = (GIWKTDigit)vertex.m_data.get(1);
					point.m_lon = lon.m_value;
					point.m_lat = lat.m_value;
					GILonLat in_map = GIProjection.ReprojectLonLat(point.LonLat(), GIProjection.WGS84(), GIProjection.WorldMercator());
					point.m_lon_in_map_projection = in_map.lon();
					point.m_lat_in_map_projection = in_map.lat();
					line.m_points.add(point);
				}
				return line;
			}
			else if(m_geometry_description.m_str_type.m_type.equalsIgnoreCase("POLYGON"))
			{
				GI_WktPolygon polygon = new GI_WktPolygon();
				polygon.m_status = GI_WktGeometry.GIWKTGeometryStatus.SAVED;
				GIWKTBlock main = (GIWKTBlock)m_geometry_description.m_block;
				ArrayList<GIWKTDescription> blocks = main.m_points;
				for(int i = 0; i < blocks.size(); i++)
				{
					GIWKTBlock block = (GIWKTBlock)blocks.get(i);
					GI_WktLinestring ring = new GI_WktLinestring();
					ring.m_type = GI_WktGeometry.GIWKTGeometryType.RING;
					for(int j = 0; j < block.m_points.size(); j++)
					{
						GIWKTVertex vertex = (GIWKTVertex)block.m_points.get(j);
						GIWKTDigit lon = (GIWKTDigit)vertex.m_data.get(0);
						GIWKTDigit lat = (GIWKTDigit)vertex.m_data.get(1);
						GI_WktPoint point = new GI_WktPoint();
						point.m_lon = lon.m_value;
						point.m_lat = lat.m_value;
						GILonLat in_map = GIProjection.ReprojectLonLat(point.LonLat(), GIProjection.WGS84(), GIProjection.WorldMercator());
						point.m_lon_in_map_projection = in_map.lon();
						point.m_lat_in_map_projection = in_map.lat();
						ring.m_points.add(point);
					}
					polygon.m_rings.add(ring);
				}
				return polygon;
			}
			//
			else
			{
				GI_WktUserTrack track = new GI_WktUserTrack();
				track.m_status = GI_WktGeometry.GIWKTGeometryStatus.SAVED;
				track.m_file = m_geometry_description.m_file; //  "/sdcard/" +
				return track;
			}
		}
		else
		{
			GI_WktUserTrack track = new GI_WktUserTrack();
			track.m_status = GI_WktGeometry.GIWKTGeometryStatus.SAVED;
			track.m_file =m_geometry_description.m_file; //  "/sdcard/" +
			return track;
		}
	}

	public static void ReadGeometryFromWKT(GI_WktGeometry geometry, String wkt_text)
	{
		m_geometry_description = new GIWKTDescription();
		GIScriptQueue queue = new GIScriptQueue(wkt_text);
		m_geometry_description.m_block =  Read(queue);
		if(m_geometry_description.m_str_type != null)
		{
			if(m_geometry_description.m_str_type.m_type.equalsIgnoreCase("POINT"))
			{
				GI_WktPoint point = (GI_WktPoint)geometry;
				geometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.SAVED;
				GIWKTBlock block = (GIWKTBlock)m_geometry_description.m_block;
				ArrayList<GIWKTDescription> array = block.m_points;
				GIWKTVertex vertex = (GIWKTVertex)array.get(0);
				GIWKTDigit lon = (GIWKTDigit)vertex.m_data.get(0);
				GIWKTDigit lat = (GIWKTDigit)vertex.m_data.get(1);
				point.m_lon = lon.m_value;
				point.m_lat = lat.m_value;
				GILonLat in_map = GIProjection.ReprojectLonLat(point.LonLat(), GIProjection.WGS84(), GIProjection.WorldMercator());
				point.m_lon_in_map_projection = in_map.lon();
				point.m_lat_in_map_projection = in_map.lat();
			}
			else if(m_geometry_description.m_str_type.m_type.equalsIgnoreCase("LINESTRING"))
			{
				GI_WktLinestring line = (GI_WktLinestring)geometry;
				line.m_status = GI_WktGeometry.GIWKTGeometryStatus.SAVED;
				GIWKTBlock block = (GIWKTBlock)m_geometry_description.m_block;
				ArrayList<GIWKTDescription> array = block.m_points;
				for(int i = 0; i < array.size(); i++)
				{
					GI_WktPoint point = new GI_WktPoint();
					GIWKTVertex vertex = (GIWKTVertex)array.get(i);
					GIWKTDigit lon = (GIWKTDigit)vertex.m_data.get(0);
					GIWKTDigit lat = (GIWKTDigit)vertex.m_data.get(1);
					point.m_lon = lon.m_value;
					point.m_lat = lat.m_value;
					GILonLat in_map = GIProjection.ReprojectLonLat(point.LonLat(), GIProjection.WGS84(), GIProjection.WorldMercator());
					point.m_lon_in_map_projection = in_map.lon();
					point.m_lat_in_map_projection = in_map.lat();
					line.m_points.add(point);
				}

			}
			else if(m_geometry_description.m_str_type.m_type.equalsIgnoreCase("POLYGON"))
			{
				GI_WktPolygon polygon = (GI_WktPolygon)geometry;
				polygon.m_status = GI_WktGeometry.GIWKTGeometryStatus.SAVED;
				GIWKTBlock main = (GIWKTBlock)m_geometry_description.m_block;
				ArrayList<GIWKTDescription> blocks = main.m_points;
				for(int i = 0; i < blocks.size(); i++)
				{
					GIWKTBlock block = (GIWKTBlock)blocks.get(i);
					GI_WktLinestring ring = new GI_WktLinestring();
					ring.m_type = GI_WktGeometry.GIWKTGeometryType.RING;
					for(int j = 0; j < block.m_points.size(); j++)
					{
						GIWKTVertex vertex = (GIWKTVertex)block.m_points.get(j);
						GIWKTDigit lon = (GIWKTDigit)vertex.m_data.get(0);
						GIWKTDigit lat = (GIWKTDigit)vertex.m_data.get(1);
						GI_WktPoint point = new GI_WktPoint();
						point.m_lon = lon.m_value;
						point.m_lat = lat.m_value;
						GILonLat in_map = GIProjection.ReprojectLonLat(point.LonLat(), GIProjection.WGS84(), GIProjection.WorldMercator());
						point.m_lon_in_map_projection = in_map.lon();
						point.m_lat_in_map_projection = in_map.lat();
						ring.m_points.add(point);
					}
					polygon.m_rings.add(ring);
				}
			}
		}
	}

	public static GIWKTDescription Read(GIScriptQueue text)
	{
		while(!text.Empty())
		{
			char current = text.Look();
			
			if(current == '(')
			{
				text.Pop();
				
				GIWKTBlock block = new GIWKTBlock();
				while((text.Look() != ')') && (!text.Empty()) )
				{
					block.m_points.add(Read(text));
				}
				text.Pop();	
				return block;
			}
			else if(current == '\"')
			{
				text.Pop();
				GIWKTFile file = new GIWKTFile(text);
				m_geometry_description.m_file = file.m_file;
				return file;
			}
			else if(Character.isLetter(current))
			{
				m_geometry_description.m_str_type = new GIWKTType(text);
			}
			else if(Character.isDigit(current))
			{
				return new GIWKTVertex(text);
			}
			else
			{
				current = text.Pop();
			}
		}
		return null;
	}
	
}
