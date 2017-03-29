package ru.tcgeo.application.layer.renderer;

import java.io.File;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Environment;

import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.GIRenderer;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIStyle;
import ru.tcgeo.application.gilib.models.GITile;
import ru.tcgeo.application.layer.GITileLayer;
import rx.Observable;

public class GITileRenderer extends GIRenderer {

	Canvas m_canvas;
	
	public GITileRenderer() 
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void RenderImage(GILayer layer, GIBounds area, int opacity,
							Bitmap bitmap, double scale)
	{
		m_canvas = new Canvas(bitmap);

		area = area.Reprojected(GIProjection.WGS84());
		
		double _scale = GIMap.getScale(area, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
		if(_scale == 0){return;}
		//float scale_factor = (float) (scale/_scale);
		String symantic = "location";
		String conditions = "";
		GITileLayer tiles = (GITileLayer)layer;
		tiles.m_tiles = new ArrayList<GITile>();
		area = area.Reprojected(layer.projection());
//		drawLayer(layer.m_id, symantic, conditions, bitmap.getWidth(), bitmap.getHeight(), area, tiles);
		for(GITile tile : tiles.m_tiles)
		{
			try
			{
				if(tile.m_range.IsWithinRange(scale))
				{
					File file = new File(Environment.getExternalStorageDirectory(), tile.m_filename).getAbsoluteFile();
					Bitmap bit_tile = BitmapFactory.decodeFile(file.getAbsolutePath());
					Rect src = new Rect(0, 0, bit_tile.getWidth(), bit_tile.getWidth());
					RectF dst = new RectF(tile.m_points.get(0).x, tile.m_points.get(0).y, tile.m_points.get(2).x, tile.m_points.get(2).y);
					m_canvas.drawBitmap(bit_tile, src, dst, null);
					bit_tile.recycle();
				}
			}
			catch(Exception e)
			{
				Paint paint = new Paint();
		        Path path= new Path();
		        paint.setColor(Color.GRAY);
		        paint.setStyle(Style.STROKE);
		        paint.setTextSize(12);
		        path.moveTo(tile.m_points.get(0).x, tile.m_points.get(0).y);
				for(int i = 1; i < tile.m_points.size(); i++)
				{
			        path.lineTo(tile.m_points.get(i).x, tile.m_points.get(i).y);
			        if(i == 4)
			        	m_canvas.drawText(tile.m_filename, tile.m_points.get(i).x + 10, tile.m_points.get(i).y + 10, paint);
				}
				m_canvas.drawPath(path, paint);
			};
		}

	}

	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap,
			double scale) 
	{
	}

	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap,
			float scale_factor, double scale) 
	{
	}


	@Override
	public void AddStyle(GIStyle style)
	{
	}

	@Override
	public int getType(GILayer layer) {
		// TODO Auto-generated method stub
		return 0;
	}

//	native int drawLayer(long layer_id, String symantic, String conditions, long bitmapWidth, long bitmapHeight, GIBounds area, GITileLayer tiles);
}
