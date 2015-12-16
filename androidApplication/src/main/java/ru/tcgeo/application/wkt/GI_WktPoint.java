package ru.tcgeo.application.wkt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import ru.tcgeo.application.App;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIVectorStyle;

public class GI_WktPoint extends GI_WktGeometry {

	public double m_lon;
	public double m_lat;
	public double m_lon_in_map_projection;
	public double m_lat_in_map_projection;
	Bitmap m_bitmap;
	int m_TrackID;
	public GI_WktPoint() 
	{
		m_bitmap = BitmapFactory.decodeResource(App.getInstance().getResources(), R.drawable.measure_point);
		m_type = GIWKTGeometryType.POINT;
		m_status = GIWKTGeometryStatus.NEW;
		m_lon = 0;
		m_lat = 0;
		m_TrackID = -1;
	}
	public GI_WktPoint(GILonLat point)
	{
		m_bitmap = BitmapFactory.decodeResource(App.getInstance().getResources(), R.drawable.measure_point);
		m_type = GIWKTGeometryType.POINT;
		m_status = GIWKTGeometryStatus.NEW;
		m_lon = point.lon();
		m_lat = point.lat();
		GIProjection map_projection = GIEditLayersKeeper.Instance().m_Map.Projection();
		GILonLat in_map = GIProjection.ReprojectLonLat(point, GIProjection.WGS84(), map_projection);
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
		GIProjection map_projection = GIEditLayersKeeper.Instance().m_Map.Projection();
		GILonLat in_map = GIProjection.ReprojectLonLat(point, GIProjection.WGS84(), map_projection);
		m_lon_in_map_projection = in_map.lon();
		m_lat_in_map_projection = in_map.lat();
	}

	public PointF MapToScreen(Canvas canvas, GIBounds area)
	{
		//TODO
		//GIBounds area = _area.Reprojected(GIProjection.WGS84());
		
		float koeffX = (float) (canvas.getWidth() / (area.right() - area.left()));
		float koeffY = (float) (canvas.getHeight() / (area.top() - area.bottom()));
		float x = (float) ((m_lon_in_map_projection - area.left()) * koeffX);
		float y = (float) (canvas.getHeight() - (m_lat_in_map_projection - area.bottom()) * koeffY);
		return new PointF(x, y);
	}
	
	@Override
	public void Draw(Canvas canvas, GIBounds area, float scale, Paint paint) 
	{
		PointF point = MapToScreen(canvas, area);
		Rect src = new Rect(0, 0, m_bitmap.getWidth(), m_bitmap.getHeight());
		RectF dst = new RectF(point.x - scale*m_bitmap.getWidth()/2, point.y - scale*m_bitmap.getHeight()/2, point.x + scale*m_bitmap.getWidth()/2, point.y + scale*m_bitmap.getHeight()/2);
		canvas.drawBitmap(m_bitmap, src, dst, paint);
	}
	@Override
	public boolean isTouch(GIBounds point) 
	{
		boolean res =  point.ContainsPoint(LonLat());
		return res;
	}

	@Override
	public void Paint(Canvas canvas, GIVectorStyle s)
	{
		
	}
	
	public void TrackPaint(Canvas canvas, GIVectorStyle s) {
		// TODO Auto-generated method stub
		int[] offset = { 0, 0 };
		GIEditLayersKeeper.Instance().getMap().getLocationOnScreen(offset);
		Point first = GIEditLayersKeeper.Instance().getMap().MercatorMapToScreen(new GILonLat(m_lon_in_map_projection, m_lat_in_map_projection));
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
