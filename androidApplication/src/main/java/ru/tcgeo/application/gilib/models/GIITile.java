package ru.tcgeo.application.gilib.models;


import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.GISQLYandexTile;
import ru.tcgeo.application.gilib.GITileInfoOSM;

public abstract class GIITile
{
	public static GITileInfoOSM CreateTile(int z, double lon, double lat, GILayer.GILayerType type)
	{
		switch (type)
		{
			case SQL_LAYER:
			{
				return new GITileInfoOSM(z, lon, lat);
			}
			case SQL_YANDEX_LAYER:
			{
				return new GISQLYandexTile(z, lon, lat);
			}
			default:
			{
				return new GITileInfoOSM(z, lon, lat);
			}
		}
	}
	public static GITileInfoOSM CreateTile(int z, int tile_x, int tile_y, GILayer.GILayerType type)
	{
		switch (type)
		{
			case SQL_LAYER:
			{
				return new GITileInfoOSM(z, tile_x, tile_y);
			}
			case SQL_YANDEX_LAYER:
			{
				return new GISQLYandexTile(z, tile_x, tile_y);
			}
			default:
			{
				return new GITileInfoOSM(z, tile_x, tile_y);
			}
		}
	}	
}

