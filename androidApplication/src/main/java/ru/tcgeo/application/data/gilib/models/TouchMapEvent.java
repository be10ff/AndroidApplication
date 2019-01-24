package ru.tcgeo.application.data.gilib.models;

import android.graphics.Point;

public class TouchMapEvent {

    public Point point;

    public float accurancy;
    public int actions;

    public static final int STOPPED =                               0b00000001; //после конструктора.
    public static final int RUNNING =                               0b00000010; //работа с слоем
    public static final int WAITIN_FOR_SELECT_OBJECT =              0b00000100; //выбор объекта для редактирования аттрибутов
    public static final int WAITING_FOR_OBJECT_NEWLOCATION =        0b00001000; //добавление точки к геометрии
    public static final int WAITING_FOR_TO_DELETE =                 0b00010000; //выбор объекта для удаления
    public static final int WAITING_FOR_SELECT_GEOMETRY_TO_EDITING= 0b00100000; //выбор объекта для редактирования геометрии
    public static final int EDITING_GEOMETRY =                      0b01000000; //выбор точки объекта для смены ее координат
    public static final int WAITING_FOR_NEW_POINT_LOCATION =        0b10000000; //выбор новых координат выбранной точки
    public static final int EDITING_POI =                          0b100000000; //работа с POI

}
