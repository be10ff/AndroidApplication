package ru.tcgeo.application.wkt;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIVectorStyle;


public class GI_WktBounds extends GI_WktGeometry {

	public ArrayList<GI_WktPoint> m_points;
	public GI_WktBounds()
	{
		m_points = new ArrayList<GI_WktPoint>();
		m_type = GIWKTGeometryType.BOUNDS;
		m_status = GIWKTGeometryStatus.CONSTANT;
	}
	@Override
	public String toWKT()
	{
		String res = "BOUNDS(";
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

	@Override
	public boolean isTouch(GIBounds point)
	{
		return false;
	}

	@Override
	public void Paint(Canvas canvas, GIVectorStyle s)
	{

	}
	@Override
	public boolean IsEmpty() {
		// TODO Auto-generated method stub
		return false;
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
