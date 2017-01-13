package ru.tcgeo.application.layer.renderer;

import java.util.ArrayList;
import java.util.Locale;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import ru.tcgeo.application.gilib.models.GITileInfoOSM;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIStyle;
import ru.tcgeo.application.gilib.models.Tile;
import ru.tcgeo.application.layer.GILayer;
import ru.tcgeo.application.layer.GISQLLayer;
import rx.Observable;
import rx.Subscriber;

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


		//adjust zoom ratio for readable labels.
		double kf = 360.0f/(256.0f*layer.m_layer_properties.m_sqldb.mRatio);

        double left = area.left();
		double top= area.top();
        double right = area.right();
        double bottom = area.bottom();

        double width = right - left;
        double dz = Math.log(Width_px*kf/width)/Math.log(2);
        int z = (int) Math.round(dz);


        z = ((GISQLLayer)layer).getLevel(z);
        if(layer.m_layer_properties.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.AUTO && (((GISQLLayer)layer).getMin() > z || ((GISQLLayer)layer).getMax() < z))
        {
        	return;
        }
        if((layer.m_layer_properties.m_sqldb.m_zooming_type  == GISQLLayer.GISQLiteZoomingType.SMART
				|| layer.m_layer_properties.m_sqldb.m_zooming_type  == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
				&& (layer.m_layer_properties.m_sqldb.m_min_z  > z || layer.m_layer_properties.m_sqldb.m_max_z < z))
        {
        	return;
        }
        try
        {
        	SQLiteDatabase db = SQLiteDatabase.openDatabase(((GISQLLayer)layer).m_path, null, SQLiteDatabase.OPEN_READONLY);
        	ArrayList<GITileInfoOSM> tiles = new ArrayList<GITileInfoOSM>();
        	if(layer.m_layer_properties.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
        	{
        		tiles = ((GISQLLayer)layer).GetTiles(db, area, z);
        	}
        	else
        	{
            	tiles = ((GISQLLayer)layer).GetTiles(area, z);
        	}

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
	public Observable<Tile> getTiles(final GILayer layer, final GIBounds bounds, final Rect rect, float scaleFactor) {

		return Observable.create(
				new Observable.OnSubscribe<Tile>() {
					@Override
					public void call(Subscriber<? super Tile> sub) {

						GIBounds area = bounds.Reprojected(layer.projection());
						int Width_px = rect.width();
						//adjust zoom ratio for readable labels.
						double kf = 360.0f/(256.0f*layer.m_layer_properties.m_sqldb.mRatio);

						double left = area.left();
						double top= area.top();
						double right = area.right();
						double bottom = area.bottom();

						double width = right - left;
						double dz = Math.log(Width_px*kf/width)/Math.log(2);
						int z = (int) Math.round(dz);


						z = ((GISQLLayer)layer).getLevel(z);

						if(layer.m_layer_properties.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.AUTO && (((GISQLLayer)layer).getMin() > z || ((GISQLLayer)layer).getMax() < z))
						{
							sub.onCompleted();
						}
						if((layer.m_layer_properties.m_sqldb.m_zooming_type  == GISQLLayer.GISQLiteZoomingType.SMART
								|| layer.m_layer_properties.m_sqldb.m_zooming_type  == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
								&& (layer.m_layer_properties.m_sqldb.m_min_z  > z || layer.m_layer_properties.m_sqldb.m_max_z < z))
						{
							sub.onCompleted();
						}

						SQLiteDatabase db = SQLiteDatabase.openDatabase(((GISQLLayer)layer).m_path, null, SQLiteDatabase.OPEN_READONLY);
						ArrayList<GITileInfoOSM> tiles = new ArrayList<GITileInfoOSM>();
						if(layer.m_layer_properties.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
						{
							tiles = ((GISQLLayer)layer).GetTiles(db, area, z);
						}
						else
						{
							tiles = ((GISQLLayer)layer).GetTiles(area, z);
						}

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
							float koeffX = (float) (rect.width() / (right - left));
							float koeffY = (float) (rect.height() / (top - bottom));
							if(bit_tile != null)
							{
								float left_scr = (float)((tile.m_bounds.TopLeft().lon() - left) * koeffX);
								float top_scr = (float)(rect.height() - (tile.m_bounds.TopLeft().lat() - bottom) * koeffY);
								float right_scr = (float) ((tile.m_bounds.BottomRight().lon() - left) * koeffX);
								float bottom_scr = (float)(rect.height() - (tile.m_bounds.BottomRight().lat() - bottom) * koeffY);
								RectF dst = new RectF(left_scr, top_scr, right_scr, bottom_scr);
								sub.onNext(new Tile(bit_tile, dst));
							}
						}
						db.close();
						sub.onCompleted();
					}
				}
		);
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
