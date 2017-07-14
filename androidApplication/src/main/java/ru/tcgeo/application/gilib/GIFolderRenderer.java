package ru.tcgeo.application.gilib;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Locale;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIStyle;

public class GIFolderRenderer extends GIRenderer {

	Canvas m_canvas;

	public GIFolderRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void RenderImage(GILayer layer_, GIBounds area, int opacity, Bitmap bitmap, double scale)
	{
        GIFolderLayer layer = (GIFolderLayer)layer_;
        m_canvas = new Canvas(bitmap);
		area = area.Reprojected(layer.projection());
		int Width_px = bitmap.getWidth();


		//adjust zoom ratio for readable labels.
		double kf = 360.0f/(256.0f*layer.m_layer_properties.m_sqldb.mRatio);

        double left = area.left();
		double top= area.top();
        double right = area.right();
        double bottom = area.bottom();

		float koeffX = (float) (bitmap.getWidth() / (right - left));
		float koeffY = (float) (bitmap.getHeight() / (top - bottom));

        double width = right - left;
        double dz = Math.log(Width_px*kf/width)/Math.log(2);
        int z = (int) Math.round(dz);


        z = ((GIFolderLayer)layer).getLevel(z);
        if(layer.m_layer_properties.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.AUTO && (layer.getMin() > z || layer.getMax() < z))
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
        	ArrayList<GITileInfoFolder> tiles = new ArrayList<GITileInfoFolder>();
        	if(layer.m_layer_properties.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
        	{
        		tiles = ((GIFolderLayer)layer).GetTilesAdaptive(area, z);
        	}
        	else
        	{
            	tiles = ((GIFolderLayer)layer).GetTiles(area, z);
        	}


			for(int i = 0; i < tiles.size(); i++)
        	{
				GITileInfoFolder tile = tiles.get(i);

				if(layer.IsTilePresent(tile)){
					Bitmap bit_tile = BitmapFactory.decodeFile(layer.getTilePath(tile));

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
				}

				if(Thread.interrupted())
				{
					break;
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
	public void AddStyle(GIStyle style) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getType(GILayer layer) {
		// TODO Auto-generated method stub
		return 0;
	}

}
