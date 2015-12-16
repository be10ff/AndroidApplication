package ru.tcgeo.application.gilib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIITile;
import ru.tcgeo.application.gilib.models.GIProjection;


public class GISQLLayer extends GILayer {

	//String m_site;
	String m_path; 
	public enum GISQLiteZoomingType
	{
		SMART,		// при выходе за указанный диапазон отрисовываются ближайшие доступные
		ADAPTIVE,	// подходящие тайлы ищутся рекурсией по дереву
		AUTO;		// тайлы отрисовываются по факту нахождения в базе
	}
	public int m_max_z;
	public int m_min_z;
	public int m_max;
	public int m_min;
	public GISQLiteZoomingType m_zooming_type;

	ArrayList<Integer> m_levels;

	public GISQLLayer(String path) 
	{
		m_path = path;
		type_ = GILayerType.ON_LINE;
		m_renderer = new GISQLRenderer();
		m_projection = GIProjection.WGS84();
		m_zooming_type = GISQLiteZoomingType.SMART;
		m_max_z = 19;
		m_min_z = 1;
		m_max = 19;
		m_min = 0;
		getMinMaxLevels();
	}
	@Override
	public void Redraw(GIBounds area, Bitmap bitmap, Integer opacity,
			double scale)
	{
		synchronized(this)
		{
			m_renderer.RenderImage(this, area, opacity, bitmap, scale);
		}
	}

	public void getAvalibleLevels()
	{
		m_levels = new ArrayList<Integer>();
		SQLiteDatabase db;
		Cursor c;
		try
		{
			//db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_site, null, SQLiteDatabase.OPEN_READONLY);
			db = SQLiteDatabase.openDatabase(m_path, null, SQLiteDatabase.OPEN_READONLY);
			String sql_string = String.format("SELECT DISTINCT (z) FROM tiles");
			c = db.rawQuery(sql_string, null);
		    if (c.moveToFirst())
		    {
		        while ( !c.isAfterLast() )
		        {
		        	m_levels.add(17 - c.getInt(0));
		           c.moveToNext();
		        }
		    }
	        c.close();
	        db.close();
		}
		catch(Exception e)
		{
			//Log.d("LOG_TAG", e.toString());
		}
		Collections.sort(m_levels);

	}
	public void getMinMaxLevels()
	{
		SQLiteDatabase db;
		Cursor c;
		try
		{
			//db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_site, null, SQLiteDatabase.OPEN_READONLY);
			db = SQLiteDatabase.openDatabase(m_path, null, SQLiteDatabase.OPEN_READONLY);
			String sql_string = String.format("SELECT minzoom, maxzoom FROM info");
			c = db.rawQuery(sql_string, null);
		    if (c.moveToFirst())
		    {
		        while ( !c.isAfterLast() )
		        {
		        	m_min = 17 - c.getInt(1);
		        	m_max = 17 - c.getInt(0);
		           c.moveToNext();
		        }
		    }
	        c.close();
	        db.close();
		}
		catch(Exception e)
		{
			//Log.d("LOG_TAG", e.toString());
		}
	}


	//только при одинаковом покрытии для всех level
	public int getLevel(int lvl)
	{
		switch( m_zooming_type)
		{
			case AUTO:
			{
				//ну типа значения по умолчанию
		        return lvl;
			}
			case SMART:
			{
		        if(lvl > m_max)
		        {
		        	if(lvl <= m_max_z)
		        	{
		        		lvl = m_max;
		        	}
		        	else
		        	{
		        		lvl = 0;
		        	}

		        }
		        if(lvl < m_min)
		        {
		        	if(lvl >= m_min_z)
		        	{
		        		lvl = m_min;
		        	}
		        	else
		        	{
		        		lvl = 30;
		        	}
		        }
		        return lvl;
			}
			case ADAPTIVE:
			{
		        //будет искать тайлы для покрытия рекурсией
				return lvl;
			}

		}
		return lvl;
	}



	/***
	 *
	 * @param tile искомый тайл
	 * @return true если доступен
	 *
	 */
	//	public boolean IsTilePresent(SQLiteDatabase db, GITileInfoOSM tile)
	public boolean IsTilePresent(SQLiteDatabase db, GITileInfoOSM tile)
	{
		boolean res = false;
		//SQLiteDatabase db;
		//Cursor c;
		try
		{
			//db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_site, null, SQLiteDatabase.OPEN_READONLY);
			String sql_string = String.format(Locale.ENGLISH, "SELECT x, y, z FROM tiles WHERE x=%d AND y=%d AND z=%d", tile.m_xtile,  tile.m_ytile,  17-tile.m_zoom);
			Cursor c = db.rawQuery(sql_string, null);
		    if (c.moveToFirst())
		    {
		        while ( !c.isAfterLast() )
		        {
		           //int x = c.getInt(0);
		           res = true;
		           c.moveToNext();
		        }
		    }
	        c.close();
	        //db.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}
		finally
		{

		}
		return res;
	}

	/**
	 * Итерация рекурсии
	 *
	 * @param tiles массив тайлов покрытия
	 * @param root тайл верхнего уровня котоый (если он задан) надо заполнить тайлами текущего
	 * @param bounds координаты покрываемой области
	 * @param z индекс текущего уровня
	 * @param to максимальный индекс уровня для рассмотрения
	 * @param actual "актуальный" уровень
	 * @return tiles массив тайлов покрытия
	 */
	public ArrayList<GITileInfoOSM> GetTilesIteration (SQLiteDatabase db, ArrayList<GITileInfoOSM> tiles, GITileInfoOSM root, GIBounds area, GIBounds bounds, int z, int to, int actual)
	{
    	GITileInfoOSM left_top_tile = GIITile.CreateTile(z, bounds.left(), bounds.top(), type_);
        GITileInfoOSM right_bottom_tile = GIITile.CreateTile(z, bounds.right(), bounds.bottom(), type_);
//    	GITileInfoOSM left_top_tile = new GITileInfoOSM(z, bounds.m_left, bounds.m_top);
//        GITileInfoOSM right_bottom_tile = new GITileInfoOSM(z, bounds.m_right, bounds.m_bottom);
        boolean present = true;
    	for(int x = left_top_tile.m_xtile; x <= right_bottom_tile.m_xtile; x++)
    	{
    		for(int y = left_top_tile.m_ytile; y <= right_bottom_tile.m_ytile; y++)
    		{
    			GITileInfoOSM tile =  GIITile.CreateTile(z, x, y, type_);
    			//GITileInfoOSM tile = new GITileInfoOSM(z, x, y);
    			if(IsTilePresent(db, tile))
    			{
    				tiles.add(tile);
    				if(z < actual)
    				{
    					GIBounds bo = tile.getBounds().Intersect(area);
    					if(bo != null)
    					{
    						tiles = GetTilesIteration(db, tiles, tile, area, bo, z+1, to, actual);
    					}
    				}
    			}
    			else
    			{
    				present = false;
    				if(z+1 < to)
    				{
    					GIBounds bo = tile.getBounds().Intersect(area);
    					if(bo != null)
    					{
    						tiles = GetTilesIteration(db, tiles, tile, area, bo, z+1, to, actual);
    					}
    				}
    			}
    		}
    	}
    	if(present && root != null && z-1 < actual)
    	{
    		tiles.remove(root);
    	}
		return tiles;

	}
	public ArrayList<GITileInfoOSM> GetTiles(GIBounds area, int actual)
	{
		ArrayList<GITileInfoOSM> tiles = new ArrayList<GITileInfoOSM>();
//    	GITileInfoOSM left_top_tile = new GITileInfoOSM(actual, area.m_left, area.m_top);
//        GITileInfoOSM right_bottom_tile = new GITileInfoOSM(actual, area.m_right, area.m_bottom);
    	GITileInfoOSM left_top_tile = GIITile.CreateTile(actual, area.left(), area.top(), type_);
        GITileInfoOSM right_bottom_tile = GIITile.CreateTile(actual, area.right(), area.bottom(), type_);
        //boolean present = true;
    	for(int x = left_top_tile.m_xtile; x <= right_bottom_tile.m_xtile; x++)
    	{
    		for(int y = left_top_tile.m_ytile; y <= right_bottom_tile.m_ytile; y++)
    		{
    			GITileInfoOSM tile = GIITile.CreateTile(actual, x, y, type_);
    			//GITileInfoOSM tile = new GITileInfoOSM(actual, x, y);
				tiles.add(tile);
    		}
    	}
    	return tiles;
	}
	public ArrayList<GITileInfoOSM> GetTiles(SQLiteDatabase db, GIBounds area, int actual)
	{
		ArrayList<GITileInfoOSM> tiles = new ArrayList<GITileInfoOSM>();
		int from = actual - 2;
		if(from < m_min)
		{
			from = m_min;
		}
		int to = actual + 2;
		if(to > m_max)
		{
			to = m_max;
		}
		if((to < m_min)||(from > m_max))
		{
			return tiles;
		}
		tiles = GetTilesIteration(db, tiles, null, area, area, from, to, actual);
		Collections.sort(tiles, new Comparator<GITileInfoOSM>()
				{
					public int compare(GITileInfoOSM lhs, GITileInfoOSM rhs) {

						if(lhs.m_zoom < rhs.m_zoom)
						{
							return -1;
						}
						if(lhs.m_zoom > rhs.m_zoom)
						{
							return 1;
						}
						return 0;
					}
				}
		);
		return tiles;
	}
}
