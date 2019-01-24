package ru.tcgeo.application.data.gilib.planimetry;

import android.graphics.RectF;

/**
 * интерфейс произвольной фигуры на плоскости
 */
public interface GIGeometryObject {
	RectF getBounds();

	GIGeometryObject clone();

	TYPE getType();

	enum TYPE {polygon, line, edge, vertex}
	//there are Points just like a pair (int, int)
	//x is code_L, min Morton's code of bound rect;
	//y is code_H, max Morton's code of bound rect;
//	abstract public Point getMortonCodes();
//	abstract public void setMortonCodes(Point codes);
}
