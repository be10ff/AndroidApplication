package ru.tcgeo.application.data;

/**
 * Created by artem on 13.04.18.
 */


public enum GIEditingStatus {
    STOPPED,                                    //после конструктора.
    RUNNING,                                    //работа с слоем
    WAITIN_FOR_SELECT_OBJECT,                    //выбор объекта для редактирования аттрибутов
    WAITING_FOR_OBJECT_NEWLOCATION,                //добавление точки к геометрии
    WAITING_FOR_TO_DELETE,                        //выбор объекта для удаления
    WAITING_FOR_SELECT_GEOMETRY_TO_EDITING,        //выбор объекта для редактирования геометрии
    EDITING_GEOMETRY,                            //выбор точки объекта для смены ее координат
    WAITING_FOR_NEW_POINT_LOCATION,                //выбор новых координат выбранной точки
    EDITING_POI                                //работа с POI
}

