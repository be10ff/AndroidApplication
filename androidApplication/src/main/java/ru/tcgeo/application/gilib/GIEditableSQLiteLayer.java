package ru.tcgeo.application.gilib;
/**
 * класс пользовательской геометрии в SQLite
 * в проекте прописывается как DBase
 */

import java.io.File;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import ru.tcgeo.application.gilib.models.GIEncoding;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.wkt.GIDBaseField;
import ru.tcgeo.application.wkt.GIWKTParser;
import ru.tcgeo.application.wkt.GI_WktGeometry;
import ru.tcgeo.application.wkt.GI_WktUserTrack;

public class GIEditableSQLiteLayer extends GIEditableLayer
{

	public GIEditableSQLiteLayer(String path) 
	{
		super(path);
		type_ = GILayerType.DBASE;
	}
	public GIEditableSQLiteLayer(String path, GIVectorStyle style)
	{
		super(path, style);
		type_ = GILayerType.DBASE;
	}

	public GIEditableSQLiteLayer(String path, GIVectorStyle style, GIEncoding encoding)
	{
		super(path, style, encoding);
		type_ = GILayerType.DBASE;
	}

	/*
	GIDataRequestor RequestDataIn_old(GIBounds point, GIDataRequestor requestor, double scale) 
	{
		GIBounds area = point.Reprojected(projection());
		requestor.StartLayer(this);
		SQLiteDatabase db; 
		Cursor c;	
		try
		{
			db = SQLiteDatabase.openDatabase(m_path, null, SQLiteDatabase.OPEN_READWRITE);
			String sql_string_objects = String.format("SELECT * FROM Layer");
			c = db.rawQuery(sql_string_objects, null);	
		    if (c.moveToFirst())
		    {
		    	while ( !c.isAfterLast() )
		        {
		    		long id = c.getLong(c.getColumnIndex("id"));
		    		String wkt = c.getString(c.getColumnIndex("Geometry"));
		    		GI_WktGeometry geom = GIWKTParser.CreateGeometryFromWKT(wkt);
		    		geom.m_ID = id;
		    		if(geom.isTouch(point))
		    		{
			    		requestor.StartObject(new GIGeometry(geom.m_ID));

			    		String field_name = "Test_field";
			    		String field_value = "Test value";
			    		
			    		Cursor fields;
						String sql_string = String.format("SELECT * FROM Fields");
						fields = db.rawQuery(sql_string, null);
						if(fields.moveToFirst())
						{
					    	while( !fields.isAfterLast() )
					        {
					    		int field_id_value = fields.getInt(fields.getColumnIndex("id"));
					    		String field_name_value = fields.getString(fields.getColumnIndex("Name"));
					    		
					    		String field_description_value = fields.getString(fields.getColumnIndex("Description"));
					    		field_name = field_name_value;
					    		int field_type_value = fields.getInt(fields.getColumnIndex("Type"));
					    		int field_subtype_value = fields.getInt(fields.getColumnIndex("SubType"));
					    		int field_order_value = fields.getInt(fields.getColumnIndex("Order"));
					    		String field_default_value = fields.getString(fields.getColumnIndex("DefaultValue"));
					    		switch(field_type_value)
					    		{
						    		case 0:
						    		{
						    			field_value = c.getString(c.getColumnIndex(field_name_value));
						    			if(field_value == null)
						    			{
						    				field_value = field_default_value;
						    			}
						    			break;
						    		}
						    		default:
						    		{
						    			field_value = field_default_value;
						    			break;
						    		}
					    		}
					    		requestor.ProcessSemantic(field_name, field_value);
					    		fields.moveToNext();
					        }
						}
			    		requestor.EndObject(new GIGeometry(geom.m_ID));
		    		}
		            c.moveToNext();
		        }
		    }	
	        c.close(); 
	        db.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}
		requestor.EndLayer(this);	
		return requestor;
	}*/

	public void DeleteObject(GI_WktGeometry geometry)
	{
		SQLiteDatabase db; 
		try
		{
			db = SQLiteDatabase.openDatabase(m_path, null, SQLiteDatabase.OPEN_READWRITE);
			db.delete("Layer", "id = " + geometry.m_ID, null);
			db.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}
	}

	public void AddGeometry(GI_WktGeometry geometry)
	{
		m_shapes.add(geometry);
		SQLiteDatabase db; 
		try
		{
			db = SQLiteDatabase.openDatabase(m_path, null, SQLiteDatabase.OPEN_READWRITE);
			ContentValues values = new ContentValues();
			values.put("Geometry", geometry.toWKT());
			values.put("BBOX_left", 0);
			values.put("BBOX_right", 0);
			values.put("BBOX_top", 0);
			values.put("BBOX_bottom", 0);
    		for(String key : geometry.m_attributes.keySet())
    		{
    			values.put(key,  geometry.m_attributes.get(key).m_value.toString());
    		}
    		geometry.m_ID = db.insert("Layer", null, values);
			db.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}		
	}
	
	public void Save()
	{
		SQLiteDatabase db; 
		try
		{
			db = SQLiteDatabase.openDatabase(m_path, null, SQLiteDatabase.OPEN_READWRITE);
			//
			
			for(GI_WktGeometry geom : m_shapes)
			{
				if(geom.m_status == GI_WktGeometry.GIWKTGeometryStatus.NEW)
				{
					ContentValues values = new ContentValues();
					values.put("Geometry", geom.toWKT());
					values.put("BBOX_left", 0);
					values.put("BBOX_right", 0);
					values.put("BBOX_top", 0);
					values.put("BBOX_bottom", 0);
		    		for(String key : geom.m_attributes.keySet())
		    		{
		    			values.put(key,  geom.m_attributes.get(key).m_value.toString());
		    		}
					geom.m_ID = db.insert("Layer", null, values);
					geom.m_status = GI_WktGeometry.GIWKTGeometryStatus.GEOMETRY_EDITING;
				}
				if(geom.m_status == GI_WktGeometry.GIWKTGeometryStatus.MODIFIED)
				{
					ContentValues values = new ContentValues();
					values.put("Geometry", geom.toWKT());
					values.put("BBOX_left", 0);
					values.put("BBOX_right", 0);
					values.put("BBOX_top", 0);
					values.put("BBOX_bottom", 0);
		    		for(String key : geom.m_attributes.keySet())
		    		{
		    			values.put(key,  geom.m_attributes.get(key).m_value.toString());
		    		}
		    		db.update("Layer", values, "id = ?", new String[] {String.valueOf(geom.m_ID)});
					geom.m_status = GI_WktGeometry.GIWKTGeometryStatus.SAVED;
				}
			}
			db.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}
	}
	public void LoadAttributeStruct()
	{
		SQLiteDatabase db; 
		try
		{
			db = SQLiteDatabase.openDatabase(m_path, null, SQLiteDatabase.OPEN_READWRITE);
    		Cursor fields;
			String sql_string = String.format("SELECT * FROM Fields");
			fields = db.rawQuery(sql_string, null);
			if(fields.moveToFirst())
			{
		    	while( !fields.isAfterLast() )
		        {
		    		int field_id_value = fields.getInt(fields.getColumnIndex("id"));
		    		String field_name_value = fields.getString(fields.getColumnIndex("Name"));
		    		String field_description_value = fields.getString(fields.getColumnIndex("Description"));
		    		int field_type_value = fields.getInt(fields.getColumnIndex("Type"));
		    		int field_subtype_value = fields.getInt(fields.getColumnIndex("SubType"));
		    		int field_order_value = fields.getInt(fields.getColumnIndex("Order"));
		    		String field_default_value = fields.getString(fields.getColumnIndex("DefaultValue"));
		    		GIDBaseField field = new GIDBaseField(field_id_value, field_name_value, field_description_value, field_type_value, field_subtype_value, field_order_value, field_default_value, null);
		    		m_attributes.put(field_name_value, field);
		    		fields.moveToNext();
		        }
			}
			fields.close();
	        db.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}		
	}
	public void Load()
	{
		LoadAttributeStruct();
		SQLiteDatabase db; 
		Cursor c;	
		try
		{
			Log.d("LOG_TAG", "load :" + m_path);
			db = SQLiteDatabase.openDatabase( m_path, null, SQLiteDatabase.OPEN_READWRITE);
			String sql_string_objects = String.format("SELECT * FROM Layer");
			c = db.rawQuery(sql_string_objects, null);	
		    if (c.moveToFirst())
		    {
		    	while ( !c.isAfterLast() )
		        {
		    		long id = c.getLong(c.getColumnIndex("id"));
		    		String wkt = c.getString(c.getColumnIndex("Geometry"));
		    		GI_WktGeometry geom = GIWKTParser.CreateGeometryFromWKT(wkt);
		    		geom.m_ID = id;
		    		geom.m_attributes = new HashMap<String, GIDBaseField>();
		    		for(String key : m_attributes.keySet())
		    		{
		    			geom.m_attributes.put(key, new GIDBaseField(m_attributes.get(key)));
		    		}
		    		for(String key : geom.m_attributes.keySet())
		    		{
		    			geom.m_attributes.get(key).m_value = (Object) c.getString(c.getColumnIndex(key));
		    		}
		    		if(geom.m_type == GI_WktGeometry.GIWKTGeometryType.TRACK)
		    		{
		    			GI_WktUserTrack track = (GI_WktUserTrack)geom;
		    			GIEditableSQLiteLayer layer = new GIEditableSQLiteLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.pathSeparator + track.m_file, m_style, m_encoding); //"/sdcard/"
		    			track.m_layer = layer;
		    		}
	    			m_shapes.add(geom);
		            c.moveToNext();
		        }
		    }	
	        c.close(); 
	        db.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}
	}
	
	

}
