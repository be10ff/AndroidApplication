package ru.tcgeo.application.wkt;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.planimetry.GIGeometryPolygon;

public class GI_WktPolygon extends GI_WktGeometry
{
	public ArrayList<GI_WktLinestring> m_rings;
	//TODO for SPECS
	public boolean m_isTought;
	Paint m_paint;
	public GI_WktPolygon() 
	{
		m_rings = new ArrayList<GI_WktLinestring>();
		m_type = GIWKTGeometryType.POLYGON;
		m_status = GIWKTGeometryStatus.NEW;
		//TODO for SPECS
		m_paint = new Paint();
		m_paint.setARGB(127, 0, 255, 0);
		m_paint.setStyle(Style.STROKE);
		m_paint.setStrokeWidth(1);
		//TODO for SPECS
	}
	@Override
	public String toWKT()
	{
		String res = "POLYGON(";
		for(int i = 0; i < m_rings.size(); i++)
		{
			res += "(";
			GI_WktLinestring ring = m_rings.get(i);
			for(int j = 0; j < ring.m_points.size(); j++)
			{
				res += ring.m_points.get(j).m_lon + " " + ring.m_points.get(j).m_lat;
				if(j < ring.m_points.size() - 1)
				{
					res += ", ";
				}
			}
			res += ")";
			if(i < m_rings.size() - 1)
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
		Path polygon = new Path();
		polygon.setFillType(FillType.EVEN_ODD);

		for(int i = 0; i < m_rings.size(); i++)
		{
			GI_WktLinestring ring = m_rings.get(i);
			if(ring.m_points.size() > 0)
			{
				PointF point_first = ring.m_points.get(0).MapToScreen(canvas, area);
				polygon.moveTo(point_first.x, point_first.y);
				for(int j = 1; j < ring.m_points.size(); j++)
				{
					PointF point_current = ring.m_points.get(j).MapToScreen(canvas, area);
					polygon.lineTo( point_current.x, point_current.y);
				}

				polygon.close();
			}
			
		}
		//TODO for SPECS 
		if(!m_isTought)
		{
			canvas.drawPath(polygon, m_paint);
		}
		else
		{
			canvas.drawPath(polygon, paint);
		}
	}

	@Override
	public void Paint(Canvas canvas, GIVectorStyle s)
	{
		Path polygon = new Path();
		int[] offset = { 0, 0 };
		GIEditLayersKeeper.Instance().getMap().getLocationOnScreen(offset);
		polygon.setFillType(FillType.EVEN_ODD);
		for(int i = 0; i < m_rings.size(); i++)
		{
			GI_WktLinestring ring = m_rings.get(i);
			if(ring.m_points.size() > 0)
			{
				Point point_first =  GIEditLayersKeeper.Instance().getMap().MapToScreenTempo(new GILonLat(ring.m_points.get(0).m_lon, ring.m_points.get(0).m_lat));
				polygon.moveTo(point_first.x - offset[0], point_first.y - offset[1]);
				for(int j = 1; j < ring.m_points.size() - 1; j++)
				{
					Point point_current = GIEditLayersKeeper.Instance().getMap().MapToScreenTempo(new GILonLat(ring.m_points.get(j).m_lon, ring.m_points.get(j).m_lat));
					polygon.lineTo( point_current.x - offset[0], point_current.y - offset[1]);
				}
				polygon.close();
			}
		}
		canvas.drawPath(polygon, s.m_paint_brush);
		canvas.drawPath(polygon, s.m_paint_pen);
	}
	@Override
	public boolean isTouch(GIBounds point) 
	{
		GIGeometryPolygon polygon = new GIGeometryPolygon();
		
		boolean all_in_bounds = true;
		for(GI_WktPoint vertex : m_rings.get(0).m_points)
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
		if(m_rings.get(0).m_points.size() < 4)
		{
			return m_rings.get(0).isTouch(point);
		}
		
		GILonLat center_point = point.Center();
		
		for(int i = 0; i < m_rings.get(0).m_points.size(); i++)
		{
			GILonLat lonlat = new GILonLat(m_rings.get(0).m_points.get(i).m_lon, m_rings.get(0).m_points.get(i).m_lat);
			GILonLat lonlat_proj = GIProjection.ReprojectLonLat(lonlat, GIProjection.WGS84(), GIProjection.WorldMercator());
			PointF pointf = new PointF((float)lonlat_proj.lon(), (float)lonlat_proj.lat());
			polygon.add(pointf);
		}
		for(int k = 1; k < m_rings.size(); k++)
		{
			GIGeometryPolygon ring = new GIGeometryPolygon();
			for(int i = 0; i < m_rings.get(k).m_points.size(); i++)
			{
				GILonLat lonlat = new GILonLat(m_rings.get(k).m_points.get(i).m_lon, m_rings.get(k).m_points.get(i).m_lat);
				GILonLat lonlat_proj = GIProjection.ReprojectLonLat(lonlat, GIProjection.WGS84(), GIProjection.WorldMercator());
				PointF pointf = new PointF((float)lonlat_proj.lon(), (float)lonlat_proj.lat());
				ring.add(pointf);
			}
			polygon.addRing(ring);
		}
		
		return polygon.IsPointInsidePolygon(new PointF((float)center_point.lon(), (float)center_point.lat()));
	}
	public void AddPoint(GI_WktPoint p)
	{
		GI_WktLinestring current_ring = m_rings.get(m_rings.size() - 1);
		current_ring.AddPointToRing(p);
	}
	public void AddRing(GI_WktLinestring ring)
	{
		m_rings.add(ring);
	}

	@Override
	public boolean IsEmpty() 
	{
		if(m_rings != null)
		{
			if(m_rings.size() > 0)
			{
				if(m_rings.get(0).m_points.size() > 0)
				{
					return false;
				}
			}
		}
		return true;
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
