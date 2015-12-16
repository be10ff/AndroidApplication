package ru.tcgeo.application.gilib;

import java.util.ArrayList;
import java.util.Locale;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIStyle;

public class GISQLRenderer extends GIRenderer {

	Canvas m_canvas;

	public GISQLRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void RenderImage(GILayer layer, GIBounds area, int opacity, Bitmap bitmap, double scale)
	{
		m_canvas = new Canvas(bitmap);
		area = area.Reprojected(layer.projection());
		int Width_px = bitmap.getWidth();

//		double kf = 360.0f/256.0f;
		//todo adjust zoom ratio for readable labels.
		double kf = 360.0f/(256.0f*layer.m_layer_properties.m_sqldb.mRatio);

        double left = area.left();
		double top= area.top();
        double right = area.right();
        double bottom = area.bottom();

        double width = right - left;
        double dz = Math.log(Width_px*kf/width)/Math.log(2);
        int z = (int) Math.round(dz);


        z = ((GISQLLayer)layer).getLevel(z);
        if(((GISQLLayer)layer).m_zooming_type == GISQLLayer.GISQLiteZoomingType.AUTO && (((GISQLLayer)layer).m_min > z || ((GISQLLayer)layer).m_max < z))
        {
        	return;
        }
        if((((GISQLLayer)layer).m_zooming_type == GISQLLayer.GISQLiteZoomingType.SMART || ((GISQLLayer)layer).m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)&& (((GISQLLayer)layer).m_min_z > z || ((GISQLLayer)layer).m_max_z < z))
        {
        	return;
        }
        try
        {
        	//String m_path =  ((GISQLLayer)layer).m_site;
        	//SQLiteDatabase db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_path, null, SQLiteDatabase.OPEN_READONLY);

        	SQLiteDatabase db = SQLiteDatabase.openDatabase(((GISQLLayer)layer).m_path, null, SQLiteDatabase.OPEN_READONLY);
        	ArrayList<GITileInfoOSM> tiles = new ArrayList<GITileInfoOSM>();
        	if(((GISQLLayer)layer).m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
        	{
        		tiles = ((GISQLLayer)layer).GetTiles(db, area, z);
        	}
        	else
        	{
            	tiles = ((GISQLLayer)layer).GetTiles(area, z);
        	}
        	//int j = tiles.size();
        	//int k = j;
        	for(int i = 0; i < tiles.size(); i++)
        	{
    			GITileInfoOSM tile = tiles.get(i);
    			Bitmap bit_tile = null;
				String sql_string = String.format(Locale.ENGLISH, "SELECT image FROM tiles WHERE x=%d AND y=%d AND z=%d", tile.m_xtile,  tile.m_ytile,  17-tile.m_zoom);
				Cursor c = db.rawQuery(sql_string, null);
			    if (c.moveToFirst())
			    {
			        while ( !c.isAfterLast() )
			        {
			           byte[] blob = c.getBlob(0);
			           bit_tile = BitmapFactory.decodeByteArray(blob, 0, blob.length);
			           c.moveToNext();
			        }
			    }
		        c.close();
		    	float koeffX = (float) (bitmap.getWidth() / (right - left));
		    	float koeffY = (float) (bitmap.getHeight() / (top - bottom));
		    	if(bit_tile != null)
		    	{
			    	Rect src = new Rect(0, 0, bit_tile.getWidth(), bit_tile.getWidth());
					float left_scr = (float)((tile.m_bounds.TopLeft().lon() - left) * koeffX);
					float top_scr = (float)(bitmap.getHeight() - (tile.m_bounds.TopLeft().lat() - bottom) * koeffY);
					float right_scr = (float) ((tile.m_bounds.BottomRight().lon() - left) * koeffX);
					float bottom_scr = (float)(bitmap.getHeight() - (tile.m_bounds.BottomRight().lat() - bottom) * koeffY);
					RectF dst = new RectF(left_scr, top_scr, right_scr, bottom_scr);
					m_canvas.drawBitmap(bit_tile, src, dst, null);
					bit_tile.recycle();
		    	}
				if(Thread.interrupted())
				{
					break;
				}
    		}
        	db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        };
	}
	/*@Override
	public void RenderImage(GILayer layer, GIBounds area, int opacity,
			Bitmap bitmap, double scale)
	{
		m_canvas = new Canvas(bitmap);
		//String m_file = ((GISQLLayer)layer).m_site;
		area = area.Reprojected(layer.projection());
		int Width_px = bitmap.getWidth();
		double kf = 360.0f/256.0f;
        double left = area.m_left;
		double top= area.m_top;
        double right = area.m_right;
        double bottom = area.m_bottom;

        double width = right - left;
        double dz = Math.log(Width_px*kf/width)/Math.log(2);
        int z = (int) Math.round(dz) + 2;

        if(z > 19)
        {
        	z = 19;
        }
        if(z < 0)
        {
        	z = 0;
        }

        GITileInfoOSM left_top_tile = new GITileInfoOSM(z, left, top);
        GITileInfoOSM right_bottom_tile = new GITileInfoOSM(z, right, bottom);

    	float koeffX = (float) (bitmap.getWidth() / (right - left));
    	float koeffY = (float) (bitmap.getHeight() / (top - bottom));
        try
        {
        	for(int x = left_top_tile.m_xtile; x <= right_bottom_tile.m_xtile; x++)
        	{
        		for(int y = left_top_tile.m_ytile; y <= right_bottom_tile.m_ytile; y++)
        		{
        			GITileInfoOSM tile = new GITileInfoOSM(z, x, y);
        			Bitmap bit_tile = null;
        			//String m_path = "Worlds.sqlitedb";
        			String m_path =  ((GISQLLayer)layer).m_site;
    				SQLiteDatabase db;
    				Cursor c;
    				try
    				{
    					db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_path, null, SQLiteDatabase.OPEN_READONLY);
    					String sql_string = String.format("SELECT image FROM tiles WHERE x=%d AND y=%d AND z=%d", tile.m_xtile,  tile.m_ytile,  17-tile.m_zoom);
    					c = db.rawQuery(sql_string, null);
    				    if (c.moveToFirst())
    				    {
    				        while ( !c.isAfterLast() )
    				        {
    				           byte[] blob = c.getBlob(0);
    				           bit_tile = BitmapFactory.decodeByteArray(blob, 0, blob.length);
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
					Rect src = new Rect(0, 0, bit_tile.getWidth(), bit_tile.getWidth());
					float left_scr = (float)((tile.m_bounds.TopLeft().lon() - left) * koeffX);
					float top_scr = (float)(bitmap.getHeight() - (tile.m_bounds.TopLeft().lat() - bottom) * koeffY);
					float right_scr = (float) ((tile.m_bounds.BottomRight().lon() - left) * koeffX);
					float bottom_scr = (float)(bitmap.getHeight() - (tile.m_bounds.BottomRight().lat() - bottom) * koeffY);
					RectF dst = new RectF(left_scr, top_scr, right_scr, bottom_scr);
					m_canvas.drawBitmap(bit_tile, src, dst, null);
					bit_tile.recycle();
        		}
    		}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        };
	}*/
	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap,
			double scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap,
			float scale_factor, double scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void AddStyle(GIStyle style) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getType(GILayer layer) {
		// TODO Auto-generated method stub
		return 0;
	}

}
