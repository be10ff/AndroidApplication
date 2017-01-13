package ru.tcgeo.application.layer.renderer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ru.tcgeo.application.gilib.models.GITileInfoYandex;
import ru.tcgeo.application.gilib.models.GITrafficTileInfoYandex;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIStyle;
import ru.tcgeo.application.gilib.models.Tile;
import ru.tcgeo.application.layer.renderer.GIRenderer;
import ru.tcgeo.application.layer.GILayer;
import rx.Observable;
import rx.Subscriber;

public class GIYandexRenderer extends GIRenderer {

	Canvas m_canvas;
	int downloaded;
	long downloaded_size;
	int drawed;
	int reused;
	int deleted;
	private ArrayList<GITrafficTileInfoYandex> m_cache;
	public GIYandexRenderer() 
	{
		m_cache = new ArrayList<GITrafficTileInfoYandex>();
		downloaded = 0;
		drawed = 0;
		reused = 0;
		deleted = 0;
	}

	@Override
	public void RenderImage(GILayer layer, GIBounds area, int opacity, Bitmap bitmap, double scale)
	{
		//GIBounds area_y = new GIBounds(area.m_projection, area.m_left, area.m_top, area.m_right, area.m_bottom);
		m_canvas = new Canvas(bitmap);
		//TODO all in Mercator
		GIBounds area_y = area.Reprojected(layer.projection());
		/**/
		
		//GILonLat left_top_m = geoToMercator(area_y.TopLeft());
		//GILonLat right_bottom_m = geoToMercator(area_y.BottomRight());
		//GIBounds area_m = new GIBounds(null, left_top_m.lon(), left_top_m.lat(), right_bottom_m.lon(), right_bottom_m.lat());
		//area = area_m.Reprojected(layer.projection());
		
		/**/
		int Width_px = bitmap.getWidth();
		
		double kf = 360.0f/256.0f;
        
        double left = area_y.left();
		double top= area_y.top();
        double right = area_y.right();
        double bottom = area_y.bottom();

        double width = right - left;
        
        double dz = Math.log(Width_px*kf/width)/Math.log(2);
        int z = (int) Math.round(dz);

        GITileInfoYandex left_top_tile = new GITileInfoYandex(z, area.left(), area.top());
        GITileInfoYandex right_bottom_tile = new GITileInfoYandex(z, area.right(), area.bottom());
        
    	float koeffX = (float) (bitmap.getWidth() / (right - left));
    	float koeffY = (float) (bitmap.getHeight() / (top - bottom));
//    	for(GITrafficTileInfoYandex cached : m_cache)
//    	{
//    		cached.m_used_at_last_time = 0;
//    	}

        try
        {
        	for(int x = left_top_tile.m_xtile; x <= right_bottom_tile.m_xtile; x++)
        	{
        		for(int y = left_top_tile.m_ytile; y <= right_bottom_tile.m_ytile; y++)
        		{
        			GITileInfoYandex tile = new GITileInfoYandex(z, x, y);
        			
        			Bitmap bit_tile = null;
        			Long TimeStamp = System.currentTimeMillis() / 1000L;
        			
        			for(GITrafficTileInfoYandex cached : m_cache)
        			{
        				if(cached.m_zoom == tile.m_zoom && cached.m_xtile == tile.m_xtile && cached.m_ytile == tile.m_ytile && cached.m_TimeStamp < TimeStamp + 600)
        				{
        					bit_tile = cached.m_bitmap;
        					cached.m_used_at_last_time = GITrafficTileInfoYandex.REUSE;
        					reused++;
        				}
        			}
        			
        			if(bit_tile == null)
        			{
	        			String urlStr = tile.getURL();
	        			URL url = new URL(urlStr);
	        	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	        	        urlConnection.connect();
				        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

				        bit_tile = BitmapFactory.decodeStream(in);
						downloaded_size = downloaded_size + bit_tile.getByteCount();
				        urlConnection.disconnect();
				        downloaded++;
        			}
			        //
			        if(bit_tile == null)
			        {
			        	continue;
			        }
			        //
			        m_cache.add(new GITrafficTileInfoYandex(z, x, y, bit_tile));
			        //
					Rect src = new Rect(0, 0, bit_tile.getWidth(), bit_tile.getWidth());
					float left_scr = (float)((tile.m_bounds.TopLeft().lon() - left) * koeffX);
					float top_scr = (float)(bitmap.getHeight() - (tile.m_bounds.TopLeft().lat() - bottom) * koeffY);
					float right_scr = (float) ((tile.m_bounds.BottomRight().lon() - left) * koeffX);
					float bottom_scr = (float)(bitmap.getHeight() - (tile.m_bounds.BottomRight().lat() - bottom) * koeffY);
					
					RectF dst = new RectF(left_scr, top_scr, right_scr, bottom_scr);
					m_canvas.drawBitmap(bit_tile, src, dst, null);
					drawed++;
					//bit_tile.recycle();
        	        
    				if(Thread.interrupted())
    				{
    					break;
    				}
        		}
				if(Thread.interrupted())
				{
					break;
				}
    		}
        	for(int i = m_cache.size() - 1; i >= 0; i--)
        	{
        		GITrafficTileInfoYandex cached = m_cache.get(i);
        		if(cached.m_used_at_last_time <= 0)
        		{
        			cached.m_bitmap.recycle();
        			m_cache.remove(cached);
        			cached = null;
        			deleted++;
        		}
        		else
        		{
        			cached.m_used_at_last_time--;
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

	@Override
	public Observable<Tile> getTiles(final GILayer layer, final GIBounds area, final Rect rect, float scaleFactor) {

		return Observable.create(new Observable.OnSubscribe<Tile>() {
			@Override
			public void call(Subscriber<? super Tile> subscriber) {
				GIBounds area_y = area.Reprojected(layer.projection());
				int Width_px = rect.width();

				double kf = 360.0f/256.0f;

				double left = area_y.left();
				double top= area_y.top();
				double right = area_y.right();
				double bottom = area_y.bottom();

				double width = right - left;

				double dz = Math.log(Width_px*kf/width)/Math.log(2);
				int z = (int) Math.round(dz);

				GITileInfoYandex left_top_tile = new GITileInfoYandex(z, area.left(), area.top());
				GITileInfoYandex right_bottom_tile = new GITileInfoYandex(z, area.right(), area.bottom());

				float koeffX = (float) (rect.width() / (right - left));
				float koeffY = (float) (rect.height() / (top - bottom));

				for(int x = left_top_tile.m_xtile; x <= right_bottom_tile.m_xtile; x++)
				{
					for(int y = left_top_tile.m_ytile; y <= right_bottom_tile.m_ytile; y++)
					{
						GITileInfoYandex tile = new GITileInfoYandex(z, x, y);

						Bitmap bit_tile = null;
						Long TimeStamp = System.currentTimeMillis() / 1000L;

						for(GITrafficTileInfoYandex cached : m_cache)
						{
							if(cached.m_zoom == tile.m_zoom && cached.m_xtile == tile.m_xtile && cached.m_ytile == tile.m_ytile && cached.m_TimeStamp < TimeStamp + 600)
							{
								bit_tile = cached.m_bitmap;
								cached.m_used_at_last_time = GITrafficTileInfoYandex.REUSE;
								reused++;
							}
						}

						if(bit_tile == null)
						{
							String urlStr = tile.getURL();
							try {
								URL url = new URL(urlStr);
								HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
								urlConnection.connect();
								InputStream in = new BufferedInputStream(urlConnection.getInputStream());
								bit_tile = BitmapFactory.decodeStream(in);
								if(bit_tile == null)
								{
									continue;
								}
								downloaded_size = downloaded_size + bit_tile.getByteCount();
								urlConnection.disconnect();
								downloaded++;
							} catch (Exception e) {
//								subscriber.onError(e);
							}
						}


						m_cache.add(new GITrafficTileInfoYandex(z, x, y, bit_tile));

						Rect src = new Rect(0, 0, bit_tile.getWidth(), bit_tile.getWidth());
						float left_scr = (float)((tile.m_bounds.TopLeft().lon() - left) * koeffX);
						float top_scr = (float)(rect.height() - (tile.m_bounds.TopLeft().lat() - bottom) * koeffY);
						float right_scr = (float) ((tile.m_bounds.BottomRight().lon() - left) * koeffX);
						float bottom_scr = (float)(rect.height() - (tile.m_bounds.BottomRight().lat() - bottom) * koeffY);

						RectF dst = new RectF(left_scr, top_scr, right_scr, bottom_scr);
						subscriber.onNext(new Tile(bit_tile, dst));
						drawed++;
					}
				}
				for(int i = m_cache.size() - 1; i >= 0; i--)
				{
					GITrafficTileInfoYandex cached = m_cache.get(i);
					if(cached.m_used_at_last_time <= 0)
					{
						cached.m_bitmap.recycle();
						m_cache.remove(cached);
						cached = null;
						deleted++;
					}
					else
					{
						cached.m_used_at_last_time--;
					}
				}

			}
		});
	}

}
