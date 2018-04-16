package ru.tcgeo.application.gilib;


import android.graphics.Point;
import android.location.Location;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import ru.tcgeo.application.App;
import ru.tcgeo.application.GITouchControl;
import ru.tcgeo.application.data.GIEditingStatus;
import ru.tcgeo.application.data.GITrackingStatus;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.utils.CommonUtils;
import ru.tcgeo.application.wkt.GIDBaseField;
import ru.tcgeo.application.wkt.GIGeometryControl;
import ru.tcgeo.application.wkt.GIXMLTrack;
import ru.tcgeo.application.wkt.GI_WktGeometry;
import ru.tcgeo.application.wkt.GI_WktLinestring;
import ru.tcgeo.application.wkt.GI_WktPoint;
import ru.tcgeo.application.wkt.GI_WktPolygon;

import static ru.tcgeo.application.gilib.GILayer.EditableType.POI;


@Deprecated
public class GIEditLayersKeeper {

	private static GIEditLayersKeeper instance;
	public GIMap m_Map;
	//buttons only
//	public Geoinfo activity;
//	public LocationManager m_location_manager;
	public GIGeometryControl m_current_track_control;
	//currents
	public GIEditableLayer m_layer;

	public GIEditableLayer m_TrackLayer;

	public GIEditableLayer m_POILayer;

	public GI_WktGeometry m_CurrentTrack;

	public GI_WktGeometry m_geometry;

	public ArrayList<GIEditableLayer> m_Layers;

	public boolean m_AutoFollow;
	public boolean m_ShowTargetDirection;
	GIGeometryControl m_current_geometry_editing_control;
	ArrayList<GIGeometryControl> m_controls;
	private GITouchControl m_TouchControl;
//	private GIPositionControl m_position;

	private boolean m_isPaused;
	private GIEditLayersKeeper()
	{
		m_layer = null;
		m_geometry = null;
		m_Layers = new ArrayList<GIEditableLayer>();
		m_controls = new ArrayList<GIGeometryControl>();
		m_AutoFollow = false;
		m_ShowTargetDirection = false;
		m_isPaused = false;
	}

	public static GIEditLayersKeeper Instance() {
		if (instance == null) {
			instance = new GIEditLayersKeeper();
		}
		return instance;
	}

	public void setTouchControl(GITouchControl TouchControl)
	{
		m_TouchControl = TouchControl;
	}

	public GIMap getMap()
	{
		return m_Map;
	}

	public void setMap(GIMap map) {
		m_Map = map;
	}
	
	public void AddLayer(GIEditableLayer layer)
	{
		m_Layers.add(layer);
	}

	public void ClearLayers()
	{
		m_Layers.clear();
	}

	public boolean CreateNewObject()
	{
		boolean res = false;

		if (m_layer.m_Type == null) {
			m_layer.m_Type = POI;
		}

		switch (m_layer.m_Type)
		{
			case POI:
			{
				m_geometry = new GI_WktPoint();
				res =  true;
				break;
			}
			case LINE:
			{
				m_geometry = new GI_WktLinestring();
				res =  true;
				break;
			}
			case POLYGON:
			{
				m_geometry = new GI_WktPolygon();
				GI_WktLinestring outer_ring = new GI_WktLinestring();
				((GI_WktPolygon)m_geometry).AddRing(outer_ring);
				res =  true;
				break;
			}
			case TRACK:
			{
				return false;
			}
		default:
			return false;
		}
		if( ! res)
		{
			return false;
		}
		m_geometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.NEW;
		m_geometry.m_attributes = new HashMap<String, GIDBaseField>();
		for(String key : m_layer.m_attributes.keySet())
		{
			m_geometry.m_attributes.put(key, new GIDBaseField(m_layer.m_attributes.get(key)));
		}
		m_layer.m_shapes.add(m_geometry);
		m_current_geometry_editing_control = new GIGeometryControl(m_layer, m_geometry);
		m_controls.add(m_current_geometry_editing_control);

		return res;
	}

	public void onPause()
	{
		StopEditing();
		m_isPaused = true;
	}

	public void onResume()
	{
		m_isPaused = false;
	}

	public void StopEditing()
	{
		m_TouchControl.setState(GIEditingStatus.STOPPED);
		boolean toRedraw = false;
		for(GIEditableLayer layer : m_Layers)
		{
			if (layer != null && layer.m_Status != GIEditableLayer.GIEditableLayerStatus.UNEDITED)
			{
				toRedraw = true;
				layer.Save();
				layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNEDITED;
			}
		}
		if(toRedraw)
		{
			m_Map.UpdateMap();
		}

		m_TouchControl.fbEditButton.setVisibility(View.GONE);
		m_TouchControl.fbEditButton.setActivated(false);

		for(GIGeometryControl control : m_controls)
		{
			control.Disable();
		}
		m_controls.clear();
	}

	public void StartEditing(GIEditableLayer layer)
	{
		if (m_TouchControl.getState() == GIEditingStatus.EDITING_POI)
		{
			return;
		}
		boolean toRedraw = false;
		m_TouchControl.setState(GIEditingStatus.RUNNING);
		m_layer = layer;
		for(GIEditableLayer old : m_Layers)
		{
			if(old.m_Status != GIEditableLayer.GIEditableLayerStatus.UNEDITED)
			{
				toRedraw = true;
				old.Save();
				old.m_Status = GIEditableLayer.GIEditableLayerStatus.UNEDITED;
			}
		}
		for(GIGeometryControl control : m_controls)
		{
			control.Disable();
		}
		m_controls.clear();

		if(layer.m_Status == GIEditableLayer.GIEditableLayerStatus.UNEDITED)
		{
			layer.m_Status = GIEditableLayer.GIEditableLayerStatus.EDITED;

			m_TouchControl.fbEditButton.setVisibility(View.VISIBLE);
			m_TouchControl.fbEditButton.setActivated(true);
			if(m_layer == m_TrackLayer){

				m_TouchControl.fbEditGeometry.setVisibility(View.GONE);
				m_TouchControl.fbEditCreate.setVisibility(View.GONE);
			}else{
				m_TouchControl.fbEditGeometry.setVisibility(View.VISIBLE);
				m_TouchControl.fbEditCreate.setVisibility(View.VISIBLE);
			}

			for(GI_WktGeometry geom : layer.m_shapes)
			{
				GIGeometryControl geometry_control = new GIGeometryControl(m_layer, geom);
				m_controls.add(geometry_control);
			}
			toRedraw = true;
		}
		if(toRedraw)
		{
			m_Map.UpdateMap();
		}
	}

	public void StartEditingPOI(GIEditableLayer layer, GI_WktGeometry geometry)
	{
		boolean toRedraw = false;
		m_TouchControl.setState(GIEditingStatus.EDITING_POI);
		m_layer = layer;
		m_geometry = geometry;
		for(GIEditableLayer old : m_Layers)
		{
			if(old.m_Status != GIEditableLayer.GIEditableLayerStatus.UNEDITED)
			{
				toRedraw = true;
				old.Save();
				old.m_Status = GIEditableLayer.GIEditableLayerStatus.UNEDITED;
			}
		}
		for(GIGeometryControl control : m_controls)
		{
			control.Disable();
		}
		m_controls.clear();

		if(layer.m_Status == GIEditableLayer.GIEditableLayerStatus.UNEDITED)
		{
			layer.m_Status = GIEditableLayer.GIEditableLayerStatus.EDITED;

			for(GI_WktGeometry geom : layer.m_shapes)
			{
				GIGeometryControl geometry_control = new GIGeometryControl(m_POILayer, geom);
				if(geom == m_geometry)
				{
					m_current_geometry_editing_control = geometry_control;
					/**/
					m_current_geometry_editing_control.m_points.get(0).setActiveStatus(true);
					m_current_geometry_editing_control.m_points.get(0).setChecked(false);
					m_current_geometry_editing_control.m_points.get(0).invalidate();
					m_TouchControl.setState(GIEditingStatus.EDITING_GEOMETRY);
					/**/
				}
				m_controls.add(geometry_control);
			}
		}

		m_TouchControl.showEditAttributesFragment(m_geometry);
		m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
		m_layer.Save();
		((GI_WktPoint)m_geometry).m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;
		m_current_geometry_editing_control.invalidate();
		if(toRedraw)
		{
			m_Map.UpdateMap();
		}

	}

	public void FillAttributes()
	{
		if(m_geometry != null)
		{
			if(!m_geometry.IsEmpty())
			{
				m_TouchControl.showEditAttributesFragment(m_geometry);
			}
			m_TouchControl.setState(GIEditingStatus.RUNNING);
			StopEditingGeometry();
		}
	}

	public boolean ClickAt(GILonLat point, GIBounds area)
	{

		boolean res = false;
		switch (m_TouchControl.getState())
		{
			case WAITIN_FOR_SELECT_OBJECT:
			{
				for(GI_WktGeometry geometry : m_layer.m_shapes)
				{
					if(geometry.isTouch(area))
					{
						m_geometry = geometry;
						m_TouchControl.showEditAttributesFragment(m_geometry);
						m_TouchControl.setState(GIEditingStatus.RUNNING);
						m_TouchControl.btnEditAttributes.setChecked(false);
						m_Map.UpdateMap();
						res = true;
					}
				}
				break;
			}
			case WAITING_FOR_OBJECT_NEWLOCATION:
			{
				switch (m_geometry.m_type)
				{
					case POINT:
					{
						((GI_WktPoint)m_geometry).Set(point);
						m_TouchControl.showEditAttributesFragment(m_geometry);
						m_TouchControl.setState(GIEditingStatus.RUNNING);
						m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
						m_layer.Save();
						((GI_WktPoint)m_geometry).m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;
						m_current_geometry_editing_control.addPoint((GI_WktPoint) m_geometry);
						m_current_geometry_editing_control.invalidate();

						res = true;
						m_Map.UpdateMap();

//						m_EditLayerDialog.m_btnNew.setChecked(false);
						m_TouchControl.btnEditCreate.setChecked(false);
						break;
					}
					case LINE:
					{
						GI_WktPoint p = new GI_WktPoint(point);
						((GI_WktLinestring)m_geometry).AddPoint(p);

						m_current_geometry_editing_control.addPoint(p);
						m_current_geometry_editing_control.invalidate();
						m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
						m_layer.Save();
						res = true;
						break;
					}
					case POLYGON:
					{
						GI_WktPoint p = new GI_WktPoint(point);

						((GI_WktPolygon)m_geometry).AddPoint(p);

						m_current_geometry_editing_control.addPoint(p);
						m_current_geometry_editing_control.invalidate();
						m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
						m_layer.Save();
						res = true;
						break;
					}
				default:
					break;
				}
				break;
			}
			case WAITING_FOR_TO_DELETE:
			{
				for(GI_WktGeometry geometry : m_layer.m_shapes)
				{
					if(geometry.isTouch(area))
					{
						m_geometry = geometry;
						m_TouchControl.setState(GIEditingStatus.RUNNING);
						for(GIGeometryControl control : m_controls)
						{
							if(control.m_geometry == m_geometry)
							{
								m_current_geometry_editing_control = control;
								continue;
							}
                        }
                        res = true;
					}
				}
				if(res)
				{
					m_TouchControl.setState(GIEditingStatus.RUNNING);
					m_layer.m_shapes.remove(m_geometry);
					m_layer.DeleteObject(m_geometry);
					m_geometry.Delete();
					m_geometry = null;
					m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
					m_layer.Save();
					m_Map.UpdateMap();
					m_TouchControl.btnEditDelete.setChecked(false);

					for(GIGeometryPointControl c : m_current_geometry_editing_control.m_points)
					{
						c.Remove();
					}
					m_current_geometry_editing_control.Disable();
				}

				break;
			}
			case WAITING_FOR_SELECT_GEOMETRY_TO_EDITING :
			{
				boolean reDraw = false;
				for(GI_WktGeometry geometry : m_layer.m_shapes)
				{
					if(geometry.isTouch(area))
					{
						m_geometry = geometry;
						m_geometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.GEOMETRY_EDITING;
						for(GIGeometryControl control : m_controls)
						{
							if(control.m_geometry == m_geometry)
							{
								m_current_geometry_editing_control = control;
								for(GIGeometryPointControl c : m_current_geometry_editing_control.m_points)
								{
									c.setActiveStatus(true);
									c.setChecked(false);
									c.invalidate();
								}
								continue;
							}
						}
						m_current_geometry_editing_control.invalidate();
						m_TouchControl.setState(GIEditingStatus.EDITING_GEOMETRY);
						reDraw = true;
					}
				}
				if(reDraw)
				{
					m_Map.UpdateMap();
				}
				break;
			}
			//case EDITING_GEOMETRY:
			case WAITING_FOR_NEW_POINT_LOCATION :
			{
				m_TouchControl.setState(GIEditingStatus.EDITING_GEOMETRY);

				for(GIGeometryPointControl control : m_current_geometry_editing_control.m_points)
				{
					if(control.getChecked())
					{
						control.m_WKTPoint.m_lon = point.lon();
						control.m_WKTPoint.m_lat = point.lat();

						control.setWKTPoint(control.m_WKTPoint);

						control.setChecked(false);
					}

				}
				m_current_geometry_editing_control.invalidate();
				//TODO
				m_TouchControl.setState(GIEditingStatus.EDITING_GEOMETRY);

				break;
			}
		default:
			break;
		}
		return res;
	}

	public void StopEditingGeometry()
	{
		if(m_geometry == null)
		{
			return;
		}
		m_geometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;

		if(m_geometry.m_type == GI_WktGeometry.GIWKTGeometryType.POLYGON)
		{
			GI_WktPolygon polygon = (GI_WktPolygon)m_geometry;
			for(GI_WktLinestring ring : polygon.m_rings)
			{
				if(ring.m_points.size() > 1)
				{
					ring.m_points.get(ring.m_points.size() - 1).m_lon = ring.m_points.get(0).m_lon;
					ring.m_points.get(ring.m_points.size() - 1).m_lat = ring.m_points.get(0).m_lat;
				}
			}
		}
		for(GIGeometryPointControl c : m_current_geometry_editing_control.m_points)
		{
			c.setActiveStatus(false);
			c.setChecked(false);
			c.invalidate();
		}
		m_current_geometry_editing_control.invalidate();
	}

	public void onSelectPoint(GIGeometryPointControl control)
	{
		//m_CurrentTarget = control.m_WKTPoint;
		//GIDirectionToPOIArrow arrow = new GIDirectionToPOIArrow(m_CurrentTarget);

		if(m_current_geometry_editing_control == null)
		{
			return;
		}
		boolean checked_yet = control.getChecked();
		if(m_current_geometry_editing_control != null)
		{
			for(GIGeometryPointControl other : m_current_geometry_editing_control.m_points)
			{
				other.setChecked(false);
			}
		}
		control.setChecked(checked_yet);
		if(checked_yet)
		{
			m_TouchControl.setState(GIEditingStatus.WAITING_FOR_NEW_POINT_LOCATION);
		}
		else
		{
			m_TouchControl.setState(GIEditingStatus.EDITING_GEOMETRY);
		}
	}

	//one point is one datarow
	public void onGPSLocationChanged(Location location)
	{

		GILonLat deg = null;
		float accurancy = 100;

		if(location == null)
		{
			deg = GIProjection.ReprojectLonLat(m_Map.Center(), m_Map.Projection(), GIProjection.WGS84());
		}
		else
		{
			deg = new GILonLat(location.getLongitude(), location.getLatitude());
			accurancy = location.getAccuracy();
		}

		if (!m_isPaused && m_AutoFollow) {
			GILonLat mercator = GIProjection.ReprojectLonLat(deg, GIProjection.WGS84(), m_Map.Projection());
			Point new_center = m_Map.MapToScreen(mercator);
			double distance = Math.hypot(new_center.y - m_Map.Height() / 2, new_center.x - m_Map.Width() / 2);
			//TODO uncomment
			if (distance > 20) {
				m_Map.SetCenter(mercator);
			}
		}

		if (m_TouchControl.getTrackingStatus() == GITrackingStatus.WRITE) {
			if (m_TrackLayer != null) {
				GI_WktPoint point = new GI_WktPoint();
				point.Set(deg);
				AddPointToTrack(deg, accurancy);
			}
		}

	}

	
	public boolean CreateTrack()
	{
		boolean res = false;
		if(m_TrackLayer == null){
			SimpleDateFormat dateFormat = new SimpleDateFormat(App.getInstance().dateTimeFormat, Locale.ENGLISH);
			String date = dateFormat.format(new Date(Calendar.getInstance().getTimeInMillis()));

			m_TrackLayer = GILayer.createTrack(m_Map.ps.m_name, date);
			m_TrackLayer.setType(GILayer.EditableType.TRACK);
			m_TrackLayer.Save();

			m_Map.ps.m_Group.addEntry(m_TrackLayer.m_layer_properties);
			m_Map.AddLayer(m_TrackLayer);

        }
		if(m_TrackLayer != null)
		{
			m_TouchControl.setTrackingStatus(GITrackingStatus.WRITE);
			m_CurrentTrack = new GIXMLTrack();

			m_CurrentTrack.m_attributes = new HashMap<String, GIDBaseField>();
			for(String key : m_TrackLayer.m_attributes.keySet())
			{
				m_CurrentTrack.m_attributes.put(key, new GIDBaseField(m_TrackLayer.m_attributes.get(key)));
			}
			String time = CommonUtils.getCurrentTime();
			GIDBaseField field = new GIDBaseField();
			field.m_name = "Description";
			field.m_value = time;
			m_CurrentTrack.m_attributes.put("Description", field);

			GIDBaseField proj_field = new GIDBaseField();
			proj_field.m_name = "Project";
			proj_field.m_value = m_Map.ps.m_name;
			m_CurrentTrack.m_attributes.put("Project", proj_field);


			res = ((GIXMLTrack) m_CurrentTrack).Create(m_Map.ps.m_name, m_Map.ps.m_name + CommonUtils.getCurrentTimeShort(), m_TrackLayer.m_style, m_TrackLayer.m_encoding);
			m_CurrentTrack.m_status = GI_WktGeometry.GIWKTGeometryStatus.NEW;
			m_TrackLayer.m_shapes.add(m_CurrentTrack);

			//todo
			m_current_track_control = new GIGeometryControl(m_TrackLayer, m_CurrentTrack);
			m_current_track_control.setMap(m_Map);

			m_TrackLayer.Save();
		}
		return res;
	}

	public void StopTrack()
	{

		if(m_CurrentTrack != null)
		{
			if(m_CurrentTrack.m_type == GI_WktGeometry.GIWKTGeometryType.TRACK)
			{
				GIXMLTrack track = (GIXMLTrack)m_CurrentTrack;
				track.StopTrack();
			}
		}
		if(m_current_track_control != null)
		{
			m_current_track_control.Disable();
		}
		m_CurrentTrack = null;
		m_Map.UpdateMap();
	}

	public void AddPointToTrack(GILonLat lonlat, float accurancy)
	{
		GIXMLTrack track = (GIXMLTrack)m_CurrentTrack;
		if(track == null)
		{
			return;
		}
		GI_WktPoint point = new GI_WktPoint();
		point.Set(lonlat);
		point.m_attributes = new HashMap<String, GIDBaseField>();
		GIDBaseField field = new GIDBaseField();
		field.m_name = "Description";
		field.m_value = CommonUtils.getCurrentTime();
		point.m_attributes.put("Description", field);
		if(m_current_track_control != null)
		{
			((GIXMLTrack)m_current_track_control.m_geometry).AddPoint(point, accurancy);
			m_current_track_control.invalidate();
		}
	}

	public void CreatePOI()
	{
		if(m_POILayer != null)
		{
			GI_WktPoint point = new GI_WktPoint();
			GILonLat location = GIProjection.ReprojectLonLat(m_Map.Center(), m_Map.Projection(), GIProjection.WGS84());
			point.Set(location);
			point.m_attributes = new HashMap<String, GIDBaseField>();
			for(String key : m_POILayer.m_attributes.keySet())
			{
				point.m_attributes.put(key, new GIDBaseField(m_POILayer.m_attributes.get(key)));
			}
			GIDBaseField field = new GIDBaseField();
			field.m_name = "DateTime";
			field.m_value = CommonUtils.getCurrentTime();
			point.m_attributes.put("DateTime", field);


			GIDBaseField proj_field = new GIDBaseField();
			proj_field.m_name = "Project";
			proj_field.m_value = m_Map.ps.m_name;
			point.m_attributes.put("Project", proj_field);

			m_POILayer.AddGeometry(point);
			StartEditingPOI(m_POILayer, point);

		}
	}



}
