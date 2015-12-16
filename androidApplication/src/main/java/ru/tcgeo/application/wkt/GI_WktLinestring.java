package ru.tcgeo.application.wkt;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.planimetry.Edge;
import ru.tcgeo.application.gilib.planimetry.Vertex;


public class GI_WktLinestring extends GI_WktGeometry {

	public ArrayList<GI_WktPoint> m_points;
	public GI_WktLinestring()
	{
		m_points = new ArrayList<GI_WktPoint>();
		m_type = GIWKTGeometryType.LINE;
		m_status = GIWKTGeometryStatus.NEW;
	}
	@Override
	public String toWKT()
	{
		String res = "LINESTRING(";
		for(int i = 0; i < m_points.size(); i++)
		{
			res += m_points.get(i).m_lon + " " + m_points.get(i).m_lat;
			if(i < m_points.size() - 1)
			{
				res += ", ";
			}
		}
		res += ")";
		return res;
	}
	@Override
	public void Draw(Canvas canvas, GIBounds area, float scale, Paint paint)
	{
		if(m_points.size() > 0)
		{
			for(int i = 1; i < m_points.size(); i++)
			{
				PointF point_prev = m_points.get(i-1).MapToScreen(canvas, area);
				PointF point_current = m_points.get(i).MapToScreen(canvas, area);
				canvas.drawLine(point_prev.x, point_prev.y, point_current.x, point_current.y, paint);
			}
		}
	}

	public void AddPoint(GI_WktPoint p)
	{
		m_points.add(p);
	}
	public void AddPointToRing(GI_WktPoint point)
	{

		if(m_points.size() == 0)
		{
			m_points.add(point);
			m_points.add(point);
		}
		else
		{
			m_points.add(m_points.size() - 1, point);
		}
	}
	@Override
	public boolean isTouch(GIBounds point)
	{

		boolean all_in_bounds = true;
		for(GI_WktPoint vertex : m_points)
		{
			if( !point.ContainsPoint(new GILonLat(vertex.m_lon, vertex.m_lat)))
			{
				all_in_bounds = false;
				break;
			}
		}
		if(all_in_bounds)
		{
			return true;
		}

		RectF rect = new RectF((float)point.left(), (float)point.top(), (float)point.right(), (float)point.bottom());
		ArrayList<Vertex> geom = new ArrayList<Vertex>();
		geom.add(new Vertex(rect.left, rect.bottom));
		geom.add(new Vertex(rect.right, rect.bottom));
		geom.add(new Vertex(rect.right, rect.top));
		geom.add(new Vertex(rect.left, rect.top));
		geom.add(new Vertex(rect.left, rect.bottom));
		
		for(int i = 0; i < m_points.size() - 1; i++)
		{
			GILonLat lonlat_start = new GILonLat(m_points.get(i).m_lon, m_points.get(i).m_lat);
			GILonLat lonlat_end = new GILonLat(m_points.get(i+1).m_lon, m_points.get(i+1).m_lat);
			GILonLat lonlat_proj_start = GIProjection.ReprojectLonLat(lonlat_start, GIProjection.WGS84(), GIProjection.WorldMercator());
			GILonLat lonlat_proj_end = GIProjection.ReprojectLonLat(lonlat_end, GIProjection.WGS84(), GIProjection.WorldMercator());
			PointF pointf_start = new PointF((float)lonlat_proj_start.lon(), (float)lonlat_proj_start.lat());
			PointF pointf_end = new PointF((float)lonlat_proj_end.lon(), (float)lonlat_proj_end.lat());
			Edge current = new Edge(pointf_start, pointf_end);
			if(current.SectionByPolygon(geom) != null)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void Paint(Canvas canvas, GIVectorStyle s)
	{
		int[] offset = { 0, 0 };
		GIEditLayersKeeper.Instance().getMap().getLocationOnScreen(offset);
		if(m_points.size() > 0)
		{
			for(int i = 1; i < m_points.size(); i++)
			{
				Point first = GIEditLayersKeeper.Instance().getMap().MapToScreenTempo(new GILonLat(m_points.get(i-1).m_lon, m_points.get(i-1).m_lat));
				Point second = GIEditLayersKeeper.Instance().getMap().MapToScreenTempo(new GILonLat(m_points.get(i).m_lon, m_points.get(i).m_lat));
				canvas.drawLine(first.x - offset[0], first.y - offset[0], second.x - offset[0], second.y- offset[0], s.m_paint_pen);
			}
		}
	}
	@Override
	public boolean IsEmpty() {
		// TODO Auto-generated method stub
		return (m_points.size() == 0);
	}
	@Override
	public void Delete() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String SerializedGeometry() 
	{
		return toWKT();
	}


}
