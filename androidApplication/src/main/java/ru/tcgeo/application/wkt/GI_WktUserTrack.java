package ru.tcgeo.application.wkt;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

import ru.tcgeo.application.App;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.GIEditableSQLiteLayer;
import ru.tcgeo.application.gilib.models.GIEncoding;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.planimetry.Edge;
import ru.tcgeo.application.gilib.planimetry.Vertex;

//never used
public class GI_WktUserTrack extends GI_WktGeometry {

	public int m_TrackID;
	public String m_file;
	public String m_name_wo_extention;
	public GIEditableSQLiteLayer m_layer;

	public GI_WktUserTrack()
	{
		m_layer = null;
		m_type = GIWKTGeometryType.TRACK;
		m_status = GIWKTGeometryStatus.NEW;
		m_TrackID = -1;
	}

	@Override
	public String toWKT()
	{
		File f = new File(m_file);
		String res = f.getName();
		return "FILE\"" + res + "\"";
	}

	@Override
	public void Draw(Canvas canvas, GIBounds area, float scale,
			android.graphics.Paint paint)
	{
		if(m_layer == null)
		{
			return;
		}
		if(m_layer.m_shapes == null)
		{
			return;
		}
		if(m_layer.m_shapes.size() > 0)
		{
			m_layer.m_shapes.get(0).Draw(canvas, area, scale, paint);
			for(int i = 1; i < m_layer.m_shapes.size(); i++)
			{
				((GI_WktPoint)m_layer.m_shapes.get(i)).Draw(canvas, area, scale, paint);
				PointF point_prev = ((GI_WktPoint)m_layer.m_shapes.get(i-1)).MapToScreen(canvas, area);
				PointF point_current = ((GI_WktPoint)m_layer.m_shapes.get(i)).MapToScreen(canvas, area);
				canvas.drawLine(point_prev.x, point_prev.y, point_current.x, point_current.y, paint);
			}
		}

	}

	@Override
	public void Paint(Canvas canvas, GIVectorStyle style) {
//		if(m_layer.m_shapes.size() > 0)
//		{
//			for(int i = 1; i < m_layer.m_shapes.size(); i++)
//			{
//
//				Point first = GIEditLayersKeeper.Instance().getMap().MapToScreenTempo(new GILonLat(((GI_WktPoint)m_layer.m_shapes.get(i-1)).m_lon, ((GI_WktPoint)m_layer.m_shapes.get(i-1)).m_lat));
//				Point second = GIEditLayersKeeper.Instance().getMap().MapToScreenTempo(new GILonLat(((GI_WktPoint)m_layer.m_shapes.get(i)).m_lon, ((GI_WktPoint)m_layer.m_shapes.get(i)).m_lat));
//				canvas.drawLine(first.x, first.y, second.x, second.y, style.m_paint_pen);
//			}
//		}
		int[] offset = { 0, 0 };
		GIEditLayersKeeper.Instance().getMap().getLocationOnScreen(offset);
		GIBounds area = GIEditLayersKeeper.Instance().getMap().Bounds();
		if(m_layer.m_shapes.size() > 0)
		{
			m_layer.m_shapes.get(0).Draw(canvas, area, 1, style.m_paint_pen);
			for(int i = 1; i < m_layer.m_shapes.size(); i++)
			{
				((GI_WktPoint)m_layer.m_shapes.get(i)).Draw(canvas, area, 1, style.m_paint_pen);
				PointF point_prev = ((GI_WktPoint)m_layer.m_shapes.get(i-1)).MapToScreen(canvas, area);
				PointF point_current = ((GI_WktPoint)m_layer.m_shapes.get(i)).MapToScreen(canvas, area);
				canvas.drawLine(point_prev.x - offset[0], point_prev.y - offset[1], point_current.x - offset[0], point_current.y - offset[1], style.m_paint_pen);
			}
		}
	}

	@Override
	public boolean IsEmpty() {
		return (m_layer.m_shapes.size() == 0);
	}

	@Override
	public boolean isTouch(GIBounds point)
	{
		boolean all_in_bounds = true;
		for(GI_WktGeometry g : m_layer.m_shapes)
		{
			GI_WktPoint vertex = (GI_WktPoint)g;
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

		for(int i = 0; i < m_layer.m_shapes.size() - 1; i++)
		{
			GILonLat lonlat_start = new GILonLat(((GI_WktPoint)m_layer.m_shapes.get(i)).m_lon, ((GI_WktPoint)m_layer.m_shapes.get(i)).m_lat);
			GILonLat lonlat_end  = new GILonLat(((GI_WktPoint)m_layer.m_shapes.get(i+1)).m_lon, ((GI_WktPoint)m_layer.m_shapes.get(i+1)).m_lat);
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
	
	public void Create(String name, GIVectorStyle style, GIEncoding encoding)
	{
		m_name_wo_extention = name;
		m_file = name  + ".db";
		DBHelper dbHelper = new DBHelper(App.getInstance(),  Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_file); // "/sdcard/"
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.close();
		m_layer = new GIEditableSQLiteLayer( Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_file, style, encoding); // "/sdcard/"


	}
	
	public void AddPoint(GI_WktPoint point)
	{
		m_layer.m_shapes.add(point);

		m_layer.Save();
	}
	
	public class DBHelper extends SQLiteOpenHelper
	{
		String m_name;
		public DBHelper(Context context, String name)
		{
			super (context, name, null, 1);
			m_name = name;
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			try
			{
				db.execSQL("CREATE TABLE Layer(id INTEGER PRIMARY KEY, Geometry VARCHAR(1000), Description VARCHAR(100), BBOX_left REAL, BBOX_right REAL, BBOX_top REAL, BBOX_bottom REAL)");
				db.execSQL("CREATE TABLE Fields(id INTEGER PRIMARY KEY, Name VARCHAR(32), Description VARCHAR(144), Type INT, SubType INT, 'Order' INT, DefaultValue VARCHAR(144))");
				ContentValues cv = new ContentValues();
				cv.put("id", 1);
				cv.put("Name", "Description");
				cv.put("Description", "Описание");
				cv.put("Type", 0);
				cv.put("SubType", 1);
				cv.put("'Order'", 0);
				cv.put("DefaultValue", "просто");
				db.insert("Fields", null, cv);
			}
			catch(Exception e)
			{
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			try
			{
				db.execSQL("DROP TABLE IF EXISTS Layer");
				db.execSQL("DROP TABLE IF EXISTS Fields");
			}
			catch(Exception e) {}
		}
	}

	@Override
	public void Delete() 
	{
		/*File f = new File("/sdcard/" + m_file);
		//File f = new File("/sdcard/" + m_file);
		if(f.exists())
		{
			f.delete();
		}*/
		
	}


	@Override
	public String SerializedGeometry() 
	{
		
		File f = new File(m_file);
		String res = f.getName();
		return res;
	}
}
