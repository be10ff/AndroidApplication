package ru.tcgeo.application.data.gilib.layer;


public class GISASLayer extends GITopoFolderLayer {
    public GISASLayer(String path) {
        super(path);
        type = GILayerType.SAS;
    }

}
