package ru.tcgeo.application.interfaces;

import java.io.File;

import ru.tcgeo.application.data.gilib.layer.GILayer;

public interface IFolderItemListener {
    void OnCannotFileRead(File file);//implement what to do folder is Unreadable
    void OnFileClicked(File file);//What to do When a file is clicked

    //    void onAdditionalLayer(GILayer layer);
    void onAddPointsLayer(GILayer.EditableType type, String name);
    void onDissmiss();
}
