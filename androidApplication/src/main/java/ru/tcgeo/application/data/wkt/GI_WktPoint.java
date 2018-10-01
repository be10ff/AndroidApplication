package ru.tcgeo.application.wkt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import ru.tcgeo.application.App;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIVectorStyle;

public class GI_WktPoint extends GI_WktGeometry {

    //	protected static Bitmap m_bitmap = BitmapFactory.decodeResource(App.Instance().getResources(), R.drawable.measure_point);
    public double m_lon;
	public double m_lat;
	public double m_lon_in_map_projection;
	public double m_lat_in_map_projection;
	//	Bitmap m_bitmap;
	int m_TrackID;

	public GI_WktPoint() 
	{
//		m_bitmap = BitmapFactory.decodeResource(App.Instance().getResources(), R.drawable.measure_point);
//        m_bitmap = App.Instance().wktPointBitmap;
		m_type = GIWKTGeometryType.POINT;
		m_status = GIWKTGeometryStatus.NEW;
		m_lon = 0;
		m_lat = 0;
		m_TrackID = -1;
	}
	public GI_WktPoint(GILonLat point)
	{
        m_type = GIWKTGeometryType.POINT;
		m_status = GIWKTGeometryStatus.NEW;
		m_lon = point.lon();
		m_lat = point.lat();
		GILonLat in_map = GIProjection.ReprojectLonLat(point, GIProjection.WGS84(), GIProjection.WorldMercator());
		m_lon_in_map_projection = in_map.lon();
		m_lat_in_map_projection = in_map.lat();
		m_TrackID = -1;
	}
	
	public GILonLat LonLat()
	{
		return new GILonLat(m_lon, m_lat);
	}

	@Override
	public String toWKT()
	{
		String res = "POINT(" +  m_lon + " " + m_lat + ")";
		return res;
	}

	public void Set(GILonLat point)
	{
		m_lon = point.lon();
		m_lat = point.lat();
		GILonLat in_map = GIProjection.ReprojectLonLat(point, GIProjection.WGS84(), GIProjection.WorldMercator());
		m_lon_in_map_projection = in_map.lon();
		m_lat_in_map_projection = in_map.lat();
	}

	public PointF MapToScreen(Canvas canvas, GIBounds area)
	{
		GIProjection map_projection = area.projection();
		GILonLat in_map = GIProjection.ReprojectLonLat(new GILonLat(m_lon, m_lat), GIProjection.WGS84(), map_projection);
		double m_lon_in_map_projection = in_map.lon();
		double m_lat_in_map_projection = in_map.lat();
		
		float koeffX = (float) (canvas.getWidth() / (area.right() - area.left()));
		float koeffY = (float) (canvas.getHeight() / (area.top() - area.bottom()));
		float x = (float) ((m_lon_in_map_projection - area.left()) * koeffX);
		float y = (float) (canvas.getHeight() - (m_lat_in_map_projection - area.bottom()) * koeffY);
		return new PointF(x, y);
	}
	
	@Override
	public void Draw(Canvas canvas, GIBounds area, float scale, GIVectorStyle style/*Paint paint*/)
	{
		Bitmap m_bitmap = style.m_image;
		PointF point = MapToScreen(canvas, area);
		Rect src = new Rect(0, 0, m_bitmap.getWidth(), m_bitmap.getHeight());
		RectF dst = new RectF(point.x - scale*m_bitmap.getWidth()/2, point.y - scale*m_bitmap.getHeight()/2, point.x + scale*m_bitmap.getWidth()/2, point.y + scale*m_bitmap.getHeight()/2);
		canvas.drawBitmap(m_bitmap, src, dst, style.m_paint_pen);
		//todo ??
		canvas.drawBitmap(m_bitmap, src, dst, style.m_paint_brush);
	}


	@Override
	public boolean isTouch(GIBounds point) 
	{
		boolean res =  point.ContainsPoint(LonLat());
		return res;
	}

	@Override
	public void Paint(Canvas canvas, GIBounds area, GIVectorStyle style) {

	}

	public void TrackPaint(Canvas canvas, GIVectorStyle style) {
		// TODO Auto-generated method stub
		int[] offset = { 0, 0 };
		Bitmap m_bitmap = style.m_image;

        Point first = App.Instance().getMap().MercatorMapToScreen(new GILonLat(m_lon_in_map_projection, m_lat_in_map_projection));
        first.x -= m_bitmap.getWidth()/2 + offset[0];
		first.y -= m_bitmap.getHeight()/2 + offset[1];
		canvas.drawBitmap(m_bitmap, first.x, first.y, null);
		
	}
	@Override
	public boolean IsEmpty() {
		// TODO Auto-generated method stub
		return (m_lat == 0 && m_lon == 0);
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
