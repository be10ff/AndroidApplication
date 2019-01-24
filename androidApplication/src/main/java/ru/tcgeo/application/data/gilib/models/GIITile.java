package ru.tcgeo.application.data.gilib.models;


import ru.tcgeo.application.data.gilib.layer.GILayer;
import ru.tcgeo.application.data.gilib.models.tile.GISQLYandexTile;
import ru.tcgeo.application.data.gilib.models.tile.GITileInfoGoogleMV;
import ru.tcgeo.application.data.gilib.models.tile.GITileInfoOSM;
import ru.tcgeo.application.data.gilib.models.tile.GITileInfoTopoFolder;

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
			case TOPO_FOLDER:
			{
				return new GITileInfoTopoFolder(z, lon, lat);
			}
			case GOOGLE_MV: {
				return new GITileInfoGoogleMV(z, lon, lat);
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
			case TOPO_FOLDER:
			{
				return new GITileInfoTopoFolder(z, tile_x, tile_y);
			}
			case GOOGLE_MV: {
				return new GITileInfoGoogleMV(z, tile_x, tile_y);
			}
			default:
			{
				return new GITileInfoOSM(z, tile_x, tile_y);
			}
		}
	}	
}

