package ru.tcgeo.application.data.gilib.layer;

public class GITopoFolderLayer extends GIFolderLayer {

    public GITopoFolderLayer(String path) {
        super(path);
        type = GILayerType.TOPO_FOLDER;
    }

}
