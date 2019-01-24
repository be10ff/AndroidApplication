package ru.tcgeo.application.data.gilib.models.tile;


import ru.tcgeo.application.utils.MapUtils;

public class GITileInfoGoogleMV extends GITileInfoFolder {

    public GITileInfoGoogleMV(int z, double lon, double lat) {
        super(z, lon, lat);
    }

    public GITileInfoGoogleMV(int z, int tile_x, int tile_y) {
        super(z, tile_x, tile_y);
    }

    @Override
    public void path() {
        String name = MapUtils.Tile2GoogleMV(m_xtile, m_ytile, m_zoom);
        path = "/Z" + m_zoom + "/" + name + ".png";
    }

    public String getPath() {
        return path;
    }
}
