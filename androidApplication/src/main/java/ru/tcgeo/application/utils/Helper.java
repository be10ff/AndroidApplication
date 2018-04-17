//package ru.tcgeo.application.utils;
//
//import android.view.View;
//
//import java.util.ArrayList;
//
//import ru.tcgeo.application.data.GIEditingStatus;
//import ru.tcgeo.application.gilib.GIEditableLayer;
//import ru.tcgeo.application.gilib.GIGeometryPointControl;
//import ru.tcgeo.application.gilib.models.GIBounds;
//import ru.tcgeo.application.gilib.models.GILonLat;
//import ru.tcgeo.application.interfaces.ITouchControl;
//import ru.tcgeo.application.wkt.GIGeometryControl;
//import ru.tcgeo.application.wkt.GI_WktGeometry;
//import ru.tcgeo.application.wkt.GI_WktLinestring;
//import ru.tcgeo.application.wkt.GI_WktPoint;
//import ru.tcgeo.application.wkt.GI_WktPolygon;
//
//import static ru.tcgeo.application.data.GIEditingStatus.WAITING_FOR_NEW_POINT_LOCATION;
//import static ru.tcgeo.application.data.GIEditingStatus.WAITING_FOR_OBJECT_NEWLOCATION;
//import static ru.tcgeo.application.data.GIEditingStatus.WAITING_FOR_SELECT_GEOMETRY_TO_EDITING;
//import static ru.tcgeo.application.data.GIEditingStatus.WAITING_FOR_TO_DELETE;
//import static ru.tcgeo.application.data.GIEditingStatus.WAITIN_FOR_SELECT_OBJECT;
//
///**
// * Created by artem on 16.04.18.
// */
//
//public class Helper {
//
//    ITouchControl touchControl;
//
//    /**/
//    GIEditableLayer m_layer;
//    GI_WktGeometry m_geometry;
//    GIGeometryControl m_current_geometry_editing_control;
//    ArrayList<GIGeometryControl> m_controls;
//
//        /**/
//    public Helper(ITouchControl touchControl){
//        this.touchControl = touchControl;
//    }
//
//
//    public void StartEditing(GIEditableLayer layer)
//    {
//        if (touchControl.getState() == GIEditingStatus.EDITING_POI)
//        {
//            return;
//        }
//        boolean toRedraw = false;
//        touchControl.setState(GIEditingStatus.RUNNING);
//        m_layer = layer;
//        for(GIEditableLayer old : m_Layers)
//        {
//            if(old.m_Status != GIEditableLayer.GIEditableLayerStatus.UNEDITED)
//            {
//                toRedraw = true;
//                old.Save();
//                old.m_Status = GIEditableLayer.GIEditableLayerStatus.UNEDITED;
//            }
//        }
//        for(GIGeometryControl control : m_controls)
//        {
//            control.Disable();
//        }
//        m_controls.clear();
//
//        if(layer.m_Status == GIEditableLayer.GIEditableLayerStatus.UNEDITED)
//        {
//            layer.m_Status = GIEditableLayer.GIEditableLayerStatus.EDITED;
//
//            touchControl.fbEditButton.setVisibility(View.VISIBLE);
//            fbEditButton.setActivated(true);
//            if(m_layer == m_TrackLayer){
//
//                fbEditGeometry.setVisibility(View.GONE);
//                fbEditCreate.setVisibility(View.GONE);
//            }else{
//                fbEditGeometry.setVisibility(View.VISIBLE);
//                fbEditCreate.setVisibility(View.VISIBLE);
//            }
//
//            for(GI_WktGeometry geom : layer.m_shapes)
//            {
//                GIGeometryControl geometry_control = new GIGeometryControl(m_layer, geom);
//                m_controls.add(geometry_control);
//            }
//            toRedraw = true;
//        }
//        if(toRedraw)
//        {
//            m_map.UpdateMap();
//        }
//    }
//
//    public void StartEditingPOI(GIEditableLayer layer, GI_WktGeometry geometry)
//    {
//        boolean toRedraw = false;
//        setState(GIEditingStatus.EDITING_POI);
//        m_layer = layer;
//        m_geometry = geometry;
//        for(GIEditableLayer old : m_Layers)
//        {
//            if(old.m_Status != GIEditableLayer.GIEditableLayerStatus.UNEDITED)
//            {
//                toRedraw = true;
//                old.Save();
//                old.m_Status = GIEditableLayer.GIEditableLayerStatus.UNEDITED;
//            }
//        }
//        for(GIGeometryControl control : m_controls)
//        {
//            control.Disable();
//        }
//        m_controls.clear();
//
//        if(layer.m_Status == GIEditableLayer.GIEditableLayerStatus.UNEDITED)
//        {
//            layer.m_Status = GIEditableLayer.GIEditableLayerStatus.EDITED;
//
//            for(GI_WktGeometry geom : layer.m_shapes)
//            {
//                GIGeometryControl geometry_control = new GIGeometryControl(m_POILayer, geom);
//                if(geom == m_geometry)
//                {
//                    m_current_geometry_editing_control = geometry_control;
//					/**/
//                    m_current_geometry_editing_control.m_points.get(0).setActiveStatus(true);
//                    m_current_geometry_editing_control.m_points.get(0).setChecked(false);
//                    m_current_geometry_editing_control.m_points.get(0).invalidate();
//                    setState(GIEditingStatus.EDITING_GEOMETRY);
//					/**/
//                }
//                m_controls.add(geometry_control);
//            }
//        }
//
//        showEditAttributesFragment(m_geometry);
//        m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
//        m_layer.Save();
//        ((GI_WktPoint)m_geometry).m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;
//        m_current_geometry_editing_control.invalidate();
//        if(toRedraw)
//        {
//            m_map.UpdateMap();
//        }
//
//    }
//
//
//
//    public boolean ClickAt(GILonLat point, GIBounds area) {
//
//        boolean res = false;
//        switch (getState()) {
//            case WAITIN_FOR_SELECT_OBJECT: {
//                for(GI_WktGeometry geometry : m_layer.m_shapes) {
//                    if(geometry.isTouch(area)) {
//                        m_geometry = geometry;
//                        showEditAttributesFragment(m_geometry);
//                        setState(GIEditingStatus.RUNNING);
//                        btnEditAttributes.setChecked(false);
//                        m_map.UpdateMap();
//                        res = true;
//                    }
//                }
//                break;
//            }
//            case WAITING_FOR_OBJECT_NEWLOCATION: {
//                switch (m_geometry.m_type) {
//                    case POINT:
//                    {
//                        ((GI_WktPoint)m_geometry).Set(point);
//                        showEditAttributesFragment(m_geometry);
//                        setState(GIEditingStatus.RUNNING);
//                        m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
//                        m_layer.Save();
//                        ((GI_WktPoint)m_geometry).m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;
//                        m_current_geometry_editing_control.addPoint((GI_WktPoint) m_geometry);
//                        m_current_geometry_editing_control.invalidate();
//
//                        res = true;
//                        m_map.UpdateMap();
//
//                        btnEditCreate.setChecked(false);
//                        break;
//                    }
//
//                    case LINE: {
//                        GI_WktPoint p = new GI_WktPoint(point);
//                        ((GI_WktLinestring)m_geometry).AddPoint(p);
//
//                        m_current_geometry_editing_control.addPoint(p);
//                        m_current_geometry_editing_control.invalidate();
//                        m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
//                        m_layer.Save();
//                        res = true;
//                        break;
//                    }
//
//                    case POLYGON: {
//                        GI_WktPoint p = new GI_WktPoint(point);
//
//                        ((GI_WktPolygon)m_geometry).AddPoint(p);
//
//                        m_current_geometry_editing_control.addPoint(p);
//                        m_current_geometry_editing_control.invalidate();
//                        m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
//                        m_layer.Save();
//                        res = true;
//                        break;
//                    }
//                    default:
//                        break;
//                }
//                break;
//            }
//
//            case WAITING_FOR_TO_DELETE: {
//                for(GI_WktGeometry geometry : m_layer.m_shapes) {
//                    if(geometry.isTouch(area)) {
//                        m_geometry = geometry;
//                        setState(GIEditingStatus.RUNNING);
//                        for(GIGeometryControl control : m_controls) {
//                            if(control.m_geometry == m_geometry) {
//                                m_current_geometry_editing_control = control;
//                                continue;
//                            }
//                        }
//                        res = true;
//                    }
//                }
//
//                if(res) {
//                    setState(GIEditingStatus.RUNNING);
//                    m_layer.m_shapes.remove(m_geometry);
//                    m_layer.DeleteObject(m_geometry);
//                    m_geometry.Delete();
//                    m_geometry = null;
//                    m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
//                    m_layer.Save();
//                    m_map.UpdateMap();
//                    btnEditDelete.setChecked(false);
//
//                    for(GIGeometryPointControl c : m_current_geometry_editing_control.m_points) {
//                        c.Remove();
//                    }
//                    m_current_geometry_editing_control.Disable();
//                }
//
//                break;
//            }
//            case WAITING_FOR_SELECT_GEOMETRY_TO_EDITING : {
//                boolean reDraw = false;
//                for(GI_WktGeometry geometry : m_layer.m_shapes) {
//                    if(geometry.isTouch(area)) {
//                        m_geometry = geometry;
//                        m_geometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.GEOMETRY_EDITING;
//                        for(GIGeometryControl control : m_controls) {
//                            if(control.m_geometry == m_geometry) {
//                                m_current_geometry_editing_control = control;
//                                for(GIGeometryPointControl c : m_current_geometry_editing_control.m_points) {
//                                    c.setActiveStatus(true);
//                                    c.setChecked(false);
//                                    c.invalidate();
//                                }
//                                continue;
//                            }
//                        }
//                        m_current_geometry_editing_control.invalidate();
//                        setState(GIEditingStatus.EDITING_GEOMETRY);
//                        reDraw = true;
//                    }
//                }
//                if(reDraw) {
//                    m_map.UpdateMap();
//                }
//                break;
//            }
//            //case EDITING_GEOMETRY:
//            case WAITING_FOR_NEW_POINT_LOCATION : {
//                setState(GIEditingStatus.EDITING_GEOMETRY);
//
//                for(GIGeometryPointControl control : m_current_geometry_editing_control.m_points) {
//                    if(control.getChecked()) {
//                        control.m_WKTPoint.m_lon = point.lon();
//                        control.m_WKTPoint.m_lat = point.lat();
//                        control.setWKTPoint(control.m_WKTPoint);
//                        control.setChecked(false);
//                    }
//
//                }
//                m_current_geometry_editing_control.invalidate();
//                setState(GIEditingStatus.EDITING_GEOMETRY);
//                break;
//            }
//            default:
//                break;
//        }
//        return res;
//    }
//
//}
