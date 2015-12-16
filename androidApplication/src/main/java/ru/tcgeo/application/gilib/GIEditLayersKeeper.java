package ru.tcgeo.application.gilib;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.app.FragmentManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;

import ru.tcgeo.application.gilib.gps.GICompassFragment;
//import ru.tcgeo.application.gilib.gps.GIGPSDialog;
import ru.tcgeo.application.gilib.gps.GILocatorFragment;
import ru.tcgeo.application.gilib.gps.GILocatorRange;
import ru.tcgeo.application.gilib.gps.GIXMLTrack;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.wkt.GIDBaseField;
import ru.tcgeo.application.wkt.GIGeometryControl;
import ru.tcgeo.application.wkt.GI_WktGeometry;
import ru.tcgeo.application.wkt.GI_WktLinestring;
import ru.tcgeo.application.wkt.GI_WktPoint;
import ru.tcgeo.application.wkt.GI_WktPolygon;


public class GIEditLayersKeeper {

	public enum GIEditingStatus
	{
		STOPPED,									//после конструктора.
		RUNNING,									//работа с слоем
		WAITIN_FOR_SELECT_OBJECT,					//выбор объекта для редактирования аттрибутов
		WAITING_FOR_OBJECT_NEWLOCATION,				//добавление точки к геометрии
		WAITING_FOR_TO_DELETE,						//выбор объекта для удаления
		WAITING_FOR_SELECT_GEOMETRY_TO_EDITING,		//выбор объекта для редактирования геометрии
		EDITING_GEOMETRY,							//выбор точки объекта для смены ее координат
		WAITING_FOR_NEW_POINT_LOCATION,				//выбор новых координат выбранной точки
		EDITING_POI;								//работа с POI
	}
	
	public enum GITrackingStatus
	{
		WRITE,
		PAUSE,
		STOP
	}
	
	private GITouchControl m_TouchControl;
	public GIMap m_Map;
//	private Context m_context;
	private int m_root;
	public LocationManager m_location_manager;
	// views
	public GIEditLayerDialog m_EditLayerDialog;
//	public GIGPSDialog m_GPSDialog;
	public GICompassFragment m_compass;
	public GILocatorFragment m_locator;
	public GILocatorRange m_range;
	public GIEditAttributesFragment m_EditAttributesFragment;
	private FragmentManager m_FragmentManager;
	GIGeometryControl m_current_geometry_editing_control;
	public GIGeometryControl m_current_track_control;
	ArrayList<GIGeometryControl> m_controls;
	private GIPositionControl m_position;
	//private GIGPSButtonView m_GPSButton;

	//currents
	public GIEditableLayer m_layer;
	public GIEditableLayer m_TrackLayer;
	public GIEditableLayer m_POILayer;

	public GI_WktGeometry m_CurrentTrack;
	public GI_WktGeometry m_CurrentPOI;
	public GI_WktGeometry m_CurrentTarget;
	public GI_WktGeometry m_geometry;

	//GILonLat m_last_location;
	public ArrayList<GIEditableLayer> m_Layers;



	//statuses
	public GITrackingStatus m_TrackingStatus;
	public boolean m_AutoFollow;
	public boolean m_ShowTargetDirection;
	private GIEditingStatus m_Status;
	private GIEditingStatus m_PreviusStatus;
	private boolean m_isPaused;


	public final static String edit_layer_tag = "EDIT_LAYER_TAG";
	final public String edit_attributes_tag = "EDIT_ATTRIBUTES_TAG";
	final public String gps_dialog_tag = "GPS_DIALOG_TAG";
	final public String compass_view_tag = "COMPASS_TAG";
	final public String locator_view_tag = "LOCATOR_TAG";

	private static GIEditLayersKeeper instance;

	public static GIEditLayersKeeper Instance()
	{
		if(instance == null)
		{
			instance = new GIEditLayersKeeper();
		}
		return instance;
	}

	private GIEditLayersKeeper()
	{
		m_EditLayerDialog = null;
		m_EditAttributesFragment = null;
		m_TrackingStatus = GITrackingStatus.STOP;
		m_layer = null;
		m_geometry = null;
		m_Status = GIEditingStatus.STOPPED;
		m_Layers = new ArrayList<GIEditableLayer>();
		m_controls = new ArrayList<GIGeometryControl>();
		m_AutoFollow = false;
		m_ShowTargetDirection = false;
		m_isPaused = false;
	}
	public GIEditingStatus getState()
	{
		return m_Status;
	}
	public boolean IsRunning()
	{
		return !(m_Status == GIEditingStatus.STOPPED);
	}

	public GIEditLayerDialog getEditLayerDialog()
	{
		if(m_Status == GIEditingStatus.EDITING_POI)
		{

		}
		m_EditLayerDialog = (GIEditLayerDialog) m_FragmentManager.findFragmentByTag(edit_layer_tag);
		if(m_EditLayerDialog == null)
		{
				m_EditLayerDialog = new GIEditLayerDialog();
			m_FragmentManager.beginTransaction().add(m_root, m_EditLayerDialog, edit_layer_tag).commit();
		}
		return m_EditLayerDialog;

	}
	public GIEditAttributesFragment getEditAttributesFragment()
	{
		m_EditAttributesFragment = (GIEditAttributesFragment) m_FragmentManager.findFragmentByTag(edit_attributes_tag);
		if(m_EditAttributesFragment == null)
		{
			m_EditAttributesFragment = new GIEditAttributesFragment();
			m_FragmentManager.beginTransaction().add(m_root, m_EditAttributesFragment, edit_attributes_tag).commit();
		}
		return m_EditAttributesFragment;
	}

	public void setState(GIEditingStatus status)
	{
		m_Status = status;
		//m_PreviusStatus = status;
		if(IsRunning())
		{
			m_TouchControl.SetMeasureState(false, false);
		}

	}

	public void setFragmentManager(FragmentManager fragment_manager)
	{
		m_FragmentManager = fragment_manager;
	}

	public void setTouchControl(GITouchControl TouchControl)
	{
		m_TouchControl = TouchControl;
	}

	public void setMap(GIMap map)
	{
		m_Map = map;
	}
	
	public GIMap getMap()
	{
		return m_Map;
	}

	public void AddLayer(GIEditableLayer layer)
	{
		m_Layers.add(layer);
	}
	public void ClearLayers()
	{
		m_Layers.clear();
	}
	
	public void setRoot(int id)
	{
		m_root = id;
	}
	public boolean CreateNewObject() 
	{
		boolean res = false;
		switch (m_layer.m_Type)
		{
			case POINT:
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
		setState(GIEditingStatus.STOPPED);
		boolean toRedraw = false;
		for(GIEditableLayer layer : m_Layers)
		{
			if(layer.m_Status != GIEditableLayer.GIEditableLayerStatus.UNEDITED)
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
		if(m_EditLayerDialog != null )
		{
			if(m_EditLayerDialog.isAdded())
			{
				m_FragmentManager.beginTransaction().remove( m_EditLayerDialog).commit();
			}

		}
		if(m_EditAttributesFragment != null)
		{
			if(m_EditAttributesFragment.isAdded())
			{
				m_FragmentManager.beginTransaction().remove( m_EditAttributesFragment).commit();
			}
			m_EditAttributesFragment = null;
		}
		for(GIGeometryControl control : m_controls)
		{
			control.Disable();
		}
		m_controls.clear();
	}

//	public void GPSDialog()
//	{
//		m_GPSDialog = (GIGPSDialog) m_FragmentManager.findFragmentByTag(gps_dialog_tag);
//		if(m_GPSDialog == null)
//		{
//			m_GPSDialog = new GIGPSDialog();
//			m_FragmentManager.beginTransaction().add(m_root, m_GPSDialog, gps_dialog_tag).commit();
//		}
//		else
//		{
//			if(m_GPSDialog.isAdded())
//			{
//				m_FragmentManager.beginTransaction().remove( m_GPSDialog).commit();
//			}
//		}
//	}
	public void AccurancyRangeView(boolean show)
	{
		if(show)
		{
			if(m_range == null)
			{
				m_range = new GILocatorRange();
			}
		}
		else
		{
			m_range.Disable();
		}
	}
	
	public void CompassView()
	{
		m_compass = (GICompassFragment) m_FragmentManager.findFragmentByTag(compass_view_tag);
		if(m_compass == null)
		{
			m_compass = new GICompassFragment();
			m_FragmentManager.beginTransaction().add(m_root, m_compass, compass_view_tag).commit();
		}
		else
		{
			if(m_compass.isAdded())
			{
				m_FragmentManager.beginTransaction().remove( m_compass).commit();
			}
		}
	}
	public void LocatorView(GI_WktGeometry poi)
	{
		m_locator = (GILocatorFragment) m_FragmentManager.findFragmentByTag(locator_view_tag);
		if(m_locator == null)
		{
			m_locator = new GILocatorFragment(poi);
			m_FragmentManager.beginTransaction().add(m_root, m_locator, locator_view_tag).commit();
		}
		else
		{
			if(m_locator.isAdded())
			{
				m_FragmentManager.beginTransaction().remove( m_locator).commit();
			}
		}
	}
	
	public void StartEditing(GIEditableLayer layer)
	{
		if(m_Status == GIEditingStatus.EDITING_POI)
		{
			return;
		}
		boolean toRedraw = false;
		m_Status = GIEditingStatus.RUNNING;
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

			m_EditLayerDialog = (GIEditLayerDialog) m_FragmentManager.findFragmentByTag(edit_layer_tag);
			if(m_EditLayerDialog == null)
			{
				m_EditLayerDialog = new GIEditLayerDialog();
				m_FragmentManager.beginTransaction().add(m_root, m_EditLayerDialog, edit_layer_tag).commit();

			}
			else
			{
				if(m_layer == m_TrackLayer)
				{
					m_EditLayerDialog.m_btnGeometry.setVisibility(View.GONE);
					m_EditLayerDialog.m_btnNew.setVisibility(View.GONE);
				}
				else
				{
					m_EditLayerDialog.m_btnGeometry.setVisibility(View.VISIBLE);
					m_EditLayerDialog.m_btnNew.setVisibility(View.VISIBLE);
				}
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
	public void StartTracking()
	{
		m_TrackLayer.m_Status = GIEditableLayer.GIEditableLayerStatus.EDITED;
		for(GI_WktGeometry geom : m_TrackLayer.m_shapes)
		{
			GIGeometryControl geometry_control = new GIGeometryControl(m_layer, geom);
			m_controls.add(geometry_control);
		}
	}
	
	public void StartEditingPOI(GIEditableLayer layer, GI_WktGeometry geometry)
	{
		boolean toRedraw = false;
		m_Status = GIEditingStatus.EDITING_POI;
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
					m_Status = GIEditingStatus.EDITING_GEOMETRY;
					/**/
				}
				m_controls.add(geometry_control);
			}
		}
		
		GIEditLayersKeeper.Instance().getEditAttributesFragment();
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
				GIEditLayersKeeper.Instance().getEditAttributesFragment();

			}
			m_Status = GIEditingStatus.RUNNING;
			StopEditingGeometry();
		}
	}
	public boolean ClickAt(GILonLat point, GIBounds area)
	{

		boolean res = false;
		switch (m_Status)
		{
			case WAITIN_FOR_SELECT_OBJECT:
			{
				for(GI_WktGeometry geometry : m_layer.m_shapes)
				{
					if(geometry.isTouch(area))
					{
						m_geometry = geometry;
						GIEditLayersKeeper.Instance().getEditAttributesFragment();
						m_Status = GIEditingStatus.RUNNING;
						m_EditLayerDialog.m_btnAttributes.setChecked(false);
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
						
						GIEditLayersKeeper.Instance().getEditAttributesFragment();
						m_Status = GIEditingStatus.RUNNING;
						m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
						m_layer.Save();
						((GI_WktPoint)m_geometry).m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;
						m_current_geometry_editing_control.addPoint((GI_WktPoint)m_geometry);
						m_current_geometry_editing_control.invalidate();
						
						res = true;
						m_Map.UpdateMap();
						
						m_EditLayerDialog.m_btnNew.setChecked(false);
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
						m_Status = GIEditingStatus.RUNNING;
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
					m_Status = GIEditingStatus.RUNNING;
					m_layer.m_shapes.remove(m_geometry);
					m_layer.DeleteObject(m_geometry);
					m_geometry.Delete();
					m_geometry = null;
					m_layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
					m_layer.Save();
					m_Map.UpdateMap();
					m_EditLayerDialog.m_btnDelete.setChecked(false);
					
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
						m_Status = GIEditingStatus.EDITING_GEOMETRY;
						reDraw = true;
					}
				}
				if(reDraw)
				{
					UpdateMap();
				}
				break;
			}
			//case EDITING_GEOMETRY:
			case WAITING_FOR_NEW_POINT_LOCATION :
			{
				m_Status = GIEditingStatus.EDITING_GEOMETRY;
				
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
				if(m_PreviusStatus != null)
				{
					m_Status = m_PreviusStatus;
				}
				else
				{
					m_Status = GIEditingStatus.EDITING_GEOMETRY;
				}
				//m_Status = m_PreviusStatus;
				//m_Status = GIEditingStatus.EDITING_GEOMETRY;
				break;
			}
		default:
			break;
		}
		return res;
	}
	public void UpdateMap()
	{
		m_Map.UpdateMap();
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
			setState(GIEditingStatus.WAITING_FOR_NEW_POINT_LOCATION);
		}
		else
		{
			setState(GIEditingStatus.EDITING_GEOMETRY);
		}
	}
	
	public void onLongClickPoint(GIGeometryPointControl control) 
	{
		GILonLatInputDialog m_GILonLatInputFragment = new GILonLatInputDialog(control);
		m_GILonLatInputFragment.show(m_FragmentManager, "lon_lat");
	}

//	public void setAutoFollow(boolean follow){
//		m_AutoFollow = follow;
//	}
	

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

		if(!m_isPaused)
		{

			if(m_AutoFollow)
			{
				GILonLat mercator = GIProjection.ReprojectLonLat(deg, GIProjection.WGS84(), m_Map.Projection());
				Point new_center = m_Map.MapToScreen(mercator);
				double distance = Math.hypot(new_center.y - m_Map.Height()/2, new_center.x - m_Map.Width()/2);
				//TODO uncomment
				if(distance > 20)
				{
					m_Map.SetCenter(mercator);
				}
			}
			GIEditLayersKeeper.Instance().SetPositionControl(location);
		}
		if(m_TrackingStatus == GITrackingStatus.WRITE)
		{
			//TODO
			//if(accurancy < m_context.getResources().getInteger(R.integer.))
			{
				if(m_TrackLayer != null)
				{
					GI_WktPoint point = new GI_WktPoint();
					point.Set(deg);
//					if(m_current_track_control != null) {
//						m_current_track_control.Show(m_TrackLayer.m_layer_properties.m_enabled);
//					}
					AddPointToTrack(deg, accurancy);
				}
			}
		}

	}


	public boolean CreateTrack()
	{
		boolean res = false;
		if(m_TrackLayer != null)
		{
			m_TrackingStatus = GITrackingStatus.WRITE;
			m_CurrentTrack = new GIXMLTrack();
			
			m_CurrentTrack.m_attributes = new HashMap<String, GIDBaseField>();
			for(String key : m_TrackLayer.m_attributes.keySet())
			{
				m_CurrentTrack.m_attributes.put(key, new GIDBaseField(m_TrackLayer.m_attributes.get(key)));
			}
			String time = getCurrentTime();
			GIDBaseField field = new GIDBaseField();
			field.m_name = "Description";
			field.m_value = time;
			m_CurrentTrack.m_attributes.put("Description", field);

			GIDBaseField proj_field = new GIDBaseField();
			proj_field.m_name = "Project";
			proj_field.m_value = m_Map.ps.m_name;
			m_CurrentTrack.m_attributes.put("Project", proj_field);


			res = ((GIXMLTrack)m_CurrentTrack).Create(getCurrentTimeShort(), m_TrackLayer.m_style, m_TrackLayer.m_encoding);
			m_CurrentTrack.m_status = GI_WktGeometry.GIWKTGeometryStatus.NEW;
			m_TrackLayer.m_shapes.add(m_CurrentTrack);

			//todo
			m_current_track_control = new GIGeometryControl(m_TrackLayer, m_CurrentTrack);
			m_current_track_control.setMap(m_Map);

//			m_current_track_control.Show(m_TrackLayer.m_layer_properties.m_enabled);
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
	
	private String getCurrentTime()
	{
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DATE);
		int mounth = calendar.get(Calendar.MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
       
        return String.format(Locale.ENGLISH, "%02d_%02d_%02d_%02d_%02d", mounth+1, day, hour, minute, second);
	}
	private String getCurrentTimeShort()
	{
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DATE);
		int mounth = calendar.get(Calendar.MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

		return m_Map.ps.m_name + String.format(Locale.ENGLISH, "%02d_%02d_%02d_%02d", mounth+1, day, hour, minute);
	}
	
	public void AddPointToTrack()
	{
		GIXMLTrack track = (GIXMLTrack)m_geometry;
		if(track == null)
		{
			return;
		}
		GI_WktPoint point = new GI_WktPoint();
		GILonLat location = GIProjection.ReprojectLonLat(m_Map.Center(), m_Map.Projection(), GIProjection.WGS84());
		point.Set(location);
		point.m_attributes = new HashMap<String, GIDBaseField>();
		GIDBaseField field = new GIDBaseField();
		field.m_name = "Description";
		field.m_value = getCurrentTime();
		point.m_attributes.put("Description", field);
		track.AddPoint(point, 0);
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
		field.m_value = getCurrentTime();
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
			field.m_value = getCurrentTime();
			point.m_attributes.put("DateTime", field);


			GIDBaseField proj_field = new GIDBaseField();
			proj_field.m_name = "Project";
			proj_field.m_value = m_Map.ps.m_name;
			point.m_attributes.put("Project", proj_field);
			
			m_POILayer.AddGeometry(point);
			StartEditingPOI(m_POILayer, point);

		}
	}
	public void GetPositionControl()
	{
		if(m_Map == null)
		{
			return;
		}
		else if(!m_Map.isShown())
		{
			return;
		}

		Location location = m_location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		/**/
//		String mocLocationProvider = "mock";
		String mocLocationProvider = LocationManager.GPS_PROVIDER;

		if (null != m_location_manager.getProvider(mocLocationProvider))
		{
			location = m_location_manager.getLastKnownLocation(mocLocationProvider);
		}
		/**/
		if(location != null)
		{
			if(m_position == null)
			{
				m_position = new GIPositionControl(m_Map.getContext(), m_Map);
			}
		}
		return;
	}
	
	public void SetPositionControl(Location location)
	{
		if(m_position != null)
		{
			m_position.setLonLat( new GILonLat(location.getLongitude(), location.getLatitude()));
		}
	}
	

}
