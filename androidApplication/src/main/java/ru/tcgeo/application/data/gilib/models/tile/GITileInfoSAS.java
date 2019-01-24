package ru.tcgeo.application.data.gilib.models.tile;


public class GITileInfoSAS extends GITileInfoFolder {

    public GITileInfoSAS(int z, double lon, double lat) {
        super(z, lon, lat);
    }

    public GITileInfoSAS(int z, int tile_x, int tile_y) {
        super(z, tile_x, tile_y);
    }

    @Override
    public void path() {
        //todo
        path = "/Z" + m_zoom + "/" + m_ytile + "_" + m_xtile + ".png";
    }

    public String getPath() {
        return path;
    }
}
