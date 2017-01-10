package ru.tcgeo.application.gilib.models;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by artem on 10.01.17.
 */

public class Tile {

    public Tile(Bitmap bitmap, RectF rect){
        this.bitmap = bitmap;
        this.rect = rect;
    }

    Bitmap bitmap;
    RectF rect;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public RectF getRect() {
        return rect;
    }
}
