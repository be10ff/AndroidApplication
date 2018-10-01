package ru.tcgeo.application.gilib.models.tile;


public class GITileInfoFolder extends GITileInfoOSM
{
	private String path;

	public GITileInfoFolder(){
        super();
	}
	public GITileInfoFolder(int z, double lon, double lat){
        super(z, lon, lat);

        path = "/Z" + m_zoom + "/" + m_ytile + "_" + m_xtile + ".png";
	}

	public GITileInfoFolder(int z, int tile_x, int tile_y){
        super(z, tile_x, tile_y);
        path = "/Z" + m_zoom + "/" + m_ytile + "_" + m_xtile + ".png";
	}

	public String getPath(){
        return path;
	}
}
