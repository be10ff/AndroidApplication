package ru.tcgeo.application.gilib.planimetry;

import android.graphics.RectF;

/**
 * интерфейс произвольной фигуры на плоскости
 */
public abstract interface GIGeometryObject 
{
	public static enum TYPE {polygon, line, edge, vertex};
	abstract public RectF getBounds();
	abstract public GIGeometryObject clone();
	abstract public TYPE getType();
	//there are Points just like a pair (int, int)
	//x is code_L, min Morton's code of bound rect;
	//y is code_H, max Morton's code of bound rect;
//	abstract public Point getMortonCodes();
//	abstract public void setMortonCodes(Point codes);
}
