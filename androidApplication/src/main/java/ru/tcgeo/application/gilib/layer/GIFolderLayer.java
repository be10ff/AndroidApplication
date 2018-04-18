package ru.tcgeo.application.gilib.layer;

import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.tcgeo.application.gilib.layer.renderer.GIFolderRenderer;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIITile;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.tile.GITileInfoFolder;
import ru.tcgeo.application.gilib.models.tile.GITileInfoOSM;


public class GIFolderLayer extends GILayer {

	String m_path;
	ArrayList<Integer> m_levels;
	//min & max zoom in *.sqldb see getMinMaxLevels()
	private int max;
	private int min;


	public GIFolderLayer(String path)
	{
		m_path = path;
		type = GILayerType.ON_LINE;
		m_renderer = new GIFolderRenderer();
		m_projection = GIProjection.WGS84();
		max = 19;
		min = 0;
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

    //seems unnesessary
	public void getAvalibleLevels()
	{
        m_levels = new ArrayList<Integer>();
        File folder = new File(m_path);
        if(!folder.exists()){
            return;
        }
        for(File subfolder : folder.listFiles()){
            if(subfolder.isDirectory()){
                try {
                    int lvl = Integer.valueOf(subfolder.getName());
                    m_levels.add(lvl);
                } catch (NumberFormatException e ){

                }

            }
        }
		Collections.sort(m_levels);

	}

	public void getMinMaxLevels(){

        getAvalibleLevels();
        if(m_levels != null && !m_levels.isEmpty()) {
            min = m_levels.get(0);
            max = m_levels.get(m_levels.size() - 1);
        }
	}

	//только при одинаковом покрытии для всех level
	public int getLevel(int lvl) {
		switch( m_layer_properties.m_sqldb.m_zooming_type){
			case AUTO:{
				//ну типа значения по умолчанию
		        return lvl;
			}
			case SMART:{
		        if(lvl > max){
		        	if(lvl <= m_layer_properties.m_sqldb.m_max_z){
		        		lvl = max;
		        	}else{
		        		lvl = 0;
		        	}
		        }
		        if(lvl < min){
		        	if(lvl >= m_layer_properties.m_sqldb.m_min_z){
		        		lvl = min;
		        	}else{
		        		lvl = 30;
		        	}
		        }
		        return lvl;
			}
			case ADAPTIVE:{
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

	public boolean IsTilePresent(GITileInfoFolder tile)
	{
        File file = new File(getTilePath(tile));
        return file.exists();
	}

    public String getTilePath(GITileInfoFolder tile){
        return m_path + tile.getPath();
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
	public ArrayList<GITileInfoFolder> GetTilesIteration (ArrayList<GITileInfoFolder> tiles, GITileInfoFolder root, GIBounds area, GIBounds bounds, int z, int to, int actual)
	{
		GITileInfoFolder left_top_tile = (GITileInfoFolder) GIITile.CreateTile(z, bounds.left(), bounds.top(), type);
		GITileInfoFolder right_bottom_tile = (GITileInfoFolder) GIITile.CreateTile(z, bounds.right(), bounds.bottom(), type);
		boolean present = true;
		for (int x = left_top_tile.m_xtile; x <= right_bottom_tile.m_xtile; x++) {
			for (int y = left_top_tile.m_ytile; y <= right_bottom_tile.m_ytile; y++) {
				GITileInfoFolder tile = (GITileInfoFolder) GIITile.CreateTile(z, x, y, type);
				if (IsTilePresent(tile)) {
					tiles.add(tile);
					if (z < actual) {
						GIBounds bo = tile.getBounds().Intersect(area);
    					if(bo != null)
    					{
    						tiles = GetTilesIteration(tiles, tile, area, bo, z+1, to, actual);
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
    						tiles = GetTilesIteration(tiles, tile, area, bo, z+1, to, actual);
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

	public ArrayList<GITileInfoFolder> GetTiles(GIBounds area, int actual)
	{
		ArrayList<GITileInfoFolder> tiles = new ArrayList<GITileInfoFolder>();
		GITileInfoFolder left_top_tile = (GITileInfoFolder) GIITile.CreateTile(actual, area.left(), area.top(), type);
		GITileInfoFolder right_bottom_tile = (GITileInfoFolder) GIITile.CreateTile(actual, area.right(), area.bottom(), type);
		for (int x = left_top_tile.m_xtile; x <= right_bottom_tile.m_xtile; x++) {
			for (int y = left_top_tile.m_ytile; y <= right_bottom_tile.m_ytile; y++) {
				GITileInfoFolder tile = (GITileInfoFolder) GIITile.CreateTile(actual, x, y, type);
				tiles.add(tile);
			}
		}
		return tiles;
	}

	public ArrayList<GITileInfoFolder> GetTilesAdaptive(GIBounds area, int actual)
	{
		ArrayList<GITileInfoFolder> tiles = new ArrayList<GITileInfoFolder>();
		int from = actual - 2;
		if(from < min)
		{
			from = min;
		}
		int to = actual + 2;
		if(to > max)
		{
			to = max;
		}
		if((to < min)||(from > max))
		{
			return tiles;
		}
		tiles = GetTilesIteration(tiles, null, area, area, from, to, actual);
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

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public enum GISQLiteZoomingType {
		SMART,        // при выходе за указанный диапазон отрисовываются ближайшие доступные
		ADAPTIVE,    // подходящие тайлы ищутся рекурсией по дереву
		AUTO        // тайлы отрисовываются по факту нахождения в базе
	}
}
