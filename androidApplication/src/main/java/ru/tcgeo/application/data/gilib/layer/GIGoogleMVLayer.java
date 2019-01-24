package ru.tcgeo.application.data.gilib.layer;


public class GIGoogleMVLayer extends GITopoFolderLayer {

    public GIGoogleMVLayer(String path) {
        super(path);
        type = GILayerType.GOOGLE_MV;
    }
}
