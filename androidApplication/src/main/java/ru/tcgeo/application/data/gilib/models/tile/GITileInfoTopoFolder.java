package ru.tcgeo.application.data.gilib.models.tile;


public class GITileInfoTopoFolder extends GITileInfoFolder {
    private String path;

    public GITileInfoTopoFolder(int z, double lon, double lat) {
        super(z, lon, lat);
    }

    public GITileInfoTopoFolder(int z, int tile_x, int tile_y) {
        super(z, tile_x, tile_y);
    }

    @Override
    public void path() {
        path = "/Z" + m_zoom + "/" + m_ytile + "_" + m_xtile + ".png";
    }

    public String getPath() {
        return path;
    }
}
