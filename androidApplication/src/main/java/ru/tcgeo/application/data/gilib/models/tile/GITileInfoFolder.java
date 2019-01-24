package ru.tcgeo.application.data.gilib.models.tile;

abstract public class GITileInfoFolder extends GITileInfoOSM {

    protected String path;

    public GITileInfoFolder(int z, double lon, double lat) {
        super(z, lon, lat);
        path();
    }

    public GITileInfoFolder(int z, int tile_x, int tile_y) {
        super(z, tile_x, tile_y);
        path();
    }

    abstract public void path();

    public String getPath() {
        return path;
    }
}
