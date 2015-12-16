package ru.tcgeo.application;

import java.io.File;

import ru.tcgeo.application.gilib.gps.GICompassView;
import ru.tcgeo.application.gilib.gps.GISensors;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.gilib.GIControlFloating;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.GIEditableLayer;
import ru.tcgeo.application.gilib.GIEditableLayer.GIEditableLayerType;
import ru.tcgeo.application.gilib.GIEditableSQLiteLayer;
import ru.tcgeo.application.gilib.GIGroupLayer;
import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.GILayer.GILayerType;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.GIPList;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.GIRuleToolControl;
import ru.tcgeo.application.gilib.GISQLLayer;
import ru.tcgeo.application.gilib.GISQLLayer.GISQLiteZoomingType;
import ru.tcgeo.application.gilib.models.GIScaleRange;
import ru.tcgeo.application.gilib.GISquareToolControl;
import ru.tcgeo.application.gilib.GITouchControl;
import ru.tcgeo.application.gilib.GITuple;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.gps.GIGPSButtonView;
import ru.tcgeo.application.gilib.gps.GIGPSLocationListener;
import ru.tcgeo.application.gilib.gps.GILocatorView;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.gilib.parser.GIPropertiesGroup;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayerRef;
import ru.tcgeo.application.gilib.parser.GIRange;
import ru.tcgeo.application.gilib.parser.GISQLDB;
import ru.tcgeo.application.gilib.parser.GISource;

import ru.tcgeo.application.home_screen.EditableLayersAdapter;
import ru.tcgeo.application.home_screen.EditableLayersAdapterItem;
import ru.tcgeo.application.home_screen.LayersAdapter;
import ru.tcgeo.application.home_screen.LayersAdapterItem;
import ru.tcgeo.application.home_screen.MarkersAdapter;
import ru.tcgeo.application.home_screen.MarkersAdapterItem;
import ru.tcgeo.application.home_screen.ProjectsAdapter;
import ru.tcgeo.application.home_screen.ProjectsAdapterItem;
import ru.tcgeo.application.home_screen.SettingsDialog;
import ru.tcgeo.application.local_project_management.SettingsFragment;
import ru.tcgeo.application.utils.ScreenUtils;
import ru.tcgeo.application.views.GIScaleControl;
import ru.tcgeo.application.views.OpenFileDialog;
import ru.tcgeo.application.wkt.GI_WktGeometry;
import ru.tcgeo.application.wkt.GI_WktPoint;
import android.app.Activity;
import android.app.Dialog;
//import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.otto.Bus;

public class Geoinfo extends FragmentActivity implements IFolderItemListener// implements
																	// OnTouchListener
{
	public static String SETTINGS_FRAGMENT_TAG="settings_fragment_tag";


	GIMap map;
	GITouchControl touchControl;
	SharedPreferences sp;

	final public String SAVED_PATH = "default_project_path";
	Dialog projects_dialog;
	Dialog settings_dialog;
	Dialog markers_dialog;
	Dialog editablelayers_dialog;

	GIScaleControl m_scale_control;
//	ImageButton follow_button;
	GIControlFloating m_marker_point;
	//LocationManager m_location_manager;
	GIGPSLocationListener m_location_listener;
	//View
//	FrameLayout m_top_bar;
	//GICompassView m_compass_view;
	GILocatorView m_locator;
//	GIGPSButtonView m_gps_button;

//	FloatingActionMenu actionMenu;
	GIGPSButtonView fbGPS;

	public final IFolderItemListener m_fileOpenListener = this;

	public void AddProjects(ArrayAdapter<ProjectsAdapterItem> adapter) {
		File dir = (Environment.getExternalStorageDirectory());
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				if (file.getName().endsWith(".pro")) {
					GIProjectProperties proj = new GIProjectProperties(
							file.getPath(), true);
					if (proj != null) {
						adapter.add(new ProjectsAdapterItem(proj));
					}
				}
			}
		}
	}

	public void AddMarkers(ArrayAdapter<MarkersAdapterItem> adapter) {
		if (map.ps.m_markers_source == null) {
			if (adapter.isEmpty()) {
				GIPList PList = new GIPList();
				PList.Load(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + map.ps.m_markers); // "/sdcard/"
				for (GIPList.GIMarker marker : PList.m_list) {
					adapter.add(new MarkersAdapterItem(marker));
				}
			}
		}
		if (map.ps.m_markers_source != null) {
			if (map.ps.m_markers_source.equalsIgnoreCase("file")) {
				if (adapter.isEmpty()) {
					GIPList PList = new GIPList();
					PList.Load(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + map.ps.m_markers);// "/sdcard/"
					for (GIPList.GIMarker marker : PList.m_list) {
						adapter.add(new MarkersAdapterItem(marker));
					}
				}
			}
			if (map.ps.m_markers_source.equalsIgnoreCase("layer")) {
				GIEditableLayer layer = null;
				for (GITuple tuple : map.m_layers.m_list) {
					if (tuple.layer.getName()
							.equalsIgnoreCase(map.ps.m_markers)) {
						layer = (GIEditableLayer) tuple.layer;
						break;
					}
				}
				if (layer != null){
					adapter.clear();
					GIPList list = new GIPList();
					for (GI_WktGeometry geom : layer.m_shapes){
						GI_WktPoint point = (GI_WktPoint) geom;
						if (point != null) {
							GIPList.GIMarker marker = list.new GIMarker();
							if (geom.m_attributes.containsKey("Name")){
								marker.m_name = (String) geom.m_attributes.get("Name").m_value.toString();
							}else if (!geom.m_attributes.keySet().isEmpty()){
								marker.m_name = (String) geom.m_attributes.get(geom.m_attributes.keySet().toArray()[0]).m_value;
							}else{
								marker.m_name = String.valueOf(geom.m_ID);
							}
							marker.m_lon = point.m_lon;
							marker.m_lat = point.m_lat;
							marker.m_description = "";
							marker.m_image = "";
							marker.m_diag = 0;
							adapter.add(new MarkersAdapterItem(marker));
						}
					}
				}
			}
		}
	}

	public void AddEditableLayers(GIGroupLayer layer,
			ArrayAdapter<EditableLayersAdapterItem> adapter) {
		if (adapter.isEmpty()) {

			/*
			 * for(GIPropertiesLayerRef layer : map.ps.m_Edit.m_Entries) {
			 * adapter.add(new EditableLayersAdapterItem(layer)); }
			 */
			if (adapter.isEmpty()) {
				for (GIEditableLayer editable_layer : GIEditLayersKeeper
						.Instance().m_Layers) {
					adapter.add(new EditableLayersAdapterItem(editable_layer));
				}
			}
		}
	}

	public void zoomIn(View target) {
		map.ScaleMapBy(map.Center(), 1.5f);
	}

	public void zoomOut(View target) {
		map.ScaleMapBy(map.Center(), 0.66f);
	}

//	public void moveToPosition(View target) {
//		if (follow_button.isActivated()) {
//			follow_button.setActivated(false);
//		} else {
//			follow_button.setActivated(true);
//		}
//		if (null != m_location_listener.m_location)
//			map.SetCenter(m_location_listener.m_location);
//	}

	// Temporary substitution for a GILayer iterator
	public void add_layers(GIGroupLayer layer,
			ArrayAdapter<LayersAdapterItem> adapter) {
		for (GITuple tuple : layer.m_list) {
			if (GILayerType.LAYER_GROUP == tuple.layer.type_)
				add_layers((GIGroupLayer) tuple.layer, adapter);
			else {
				adapter.add(new LayersAdapterItem(tuple));
			}
		}
	}

	// == Layers Dialog ==
	/*
	 * Dialog is always shown under the caller button, if there is enough space.
	 * Vertical size expends depending on contents up to a fixed value. Some
	 * other styles issued positioning errors.
	 */
	public void layersDialogClicked(final View layers_button) {
		final int layers_dialog_max_height = getWindowManager().getDefaultDisplay().getHeight() / 2;
		layers_button.setActivated(true);

		final Dialog layers_dialog = new Dialog(this,
				R.style.Theme_layers_dialog);
		layers_dialog.setContentView(R.layout.layers_dialog);
		layers_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		layers_dialog.setCanceledOnTouchOutside(true);

		layers_dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				layers_button.setActivated(false);
			}
		});

		// Place dialog under the button
		LayoutParams parameters = layers_dialog.getWindow().getAttributes();
		parameters.height = layers_dialog_max_height; // Some hard-coded size

		int[] button_location = { 0, 0 };
		layers_button.getLocationOnScreen(button_location);

		// Official documentation says that this will give actual screen size,
		// without taking into account decor elements (status bar).
		// But it works exactly as I expected - gives full accessible window
		// size.
		int screenCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
		int screenCenterY = getWindowManager().getDefaultDisplay().getHeight() / 2;

		// Dialog's 0,0 coordinates are in the middle of the screen
		parameters.x = button_location[0] - screenCenterX
				+ layers_button.getWidth() / 2;
		parameters.y = button_location[1] - screenCenterY
				+ layers_button.getHeight() + parameters.height / 2;

		layers_dialog.getWindow().setAttributes(parameters);

		// Fill list with data
		ListView layers_list = (ListView) layers_dialog
				.findViewById(R.id.layers_list);
		LayersAdapter adapter = new LayersAdapter(this,
				R.layout.layers_list_item, R.id.layers_list_item_text);
		// TODO
		// add_layer_header
		/**/
		View header = getLayoutInflater().inflate(
				R.layout.add_layer_header_layout, null);
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OpenFileDialog dlg = new OpenFileDialog();
				dlg.setIFolderItemListener(m_fileOpenListener);
				dlg.show(getSupportFragmentManager(), "open_dlg");
			}
		});
		layers_list.addHeaderView(header);
		/**/
		ImageButton additional = (ImageButton) layers_dialog
				.findViewById(R.id.layers_additional_button);
		additional.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OpenFileDialog dlg = new OpenFileDialog();
				dlg.setIFolderItemListener(m_fileOpenListener);
				dlg.show(getSupportFragmentManager(), "open_dlg");
				layers_dialog.dismiss();
			}
		});
		/**/
		add_layers((GIGroupLayer) map.m_layers, adapter);
		layers_list.setAdapter(adapter);
		layers_dialog.show();
	}

	// == Info Dialog ==
	/*
	 * Mostly copied from Layer_Dialog
	 */
	public void ProjectSelectorDialogClicked(final View button) {
		final int dialog_max_height = getWindowManager().getDefaultDisplay().getHeight() / 2;
		button.setActivated(true);

		projects_dialog = new Dialog(this, R.style.Theme_layers_dialog);
		projects_dialog.setContentView(R.layout.project_selector_dialog);
		projects_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		projects_dialog.setCanceledOnTouchOutside(true);

		projects_dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				button.setActivated(false);
			}
		});

		// Place dialog under the button
		LayoutParams parameters = projects_dialog.getWindow().getAttributes();
		parameters.height = dialog_max_height; // Some hard-coded size

		int[] button_location = { 0, 0 };
		button.getLocationOnScreen(button_location);

		// Official documentation says that this will give actual screen size,
		// without taking into account decor elements (status bar).
		// But it works exactly as I expected - gives full accessible window
		// size.
		int screenCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
		int screenCenterY = getWindowManager().getDefaultDisplay().getHeight() / 2;

		// Dialog's 0,0 coordinates are in the middle of the screen
		parameters.x = button_location[0] - screenCenterX + button.getWidth()
				/ 2;
		parameters.y = button_location[1] - screenCenterY + button.getHeight()
				+ parameters.height / 2;

		projects_dialog.getWindow().setAttributes(parameters);

		// Fill list with data
		ListView project_list = (ListView) projects_dialog
				.findViewById(R.id.projects_list);
		View header = getLayoutInflater().inflate(
				R.layout.project_list_management_item, null);
//		header.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				projects_dialog.cancel();
//				GIServer.Instance()
//						.getPresenter().getDialog()
//						.show(getFragmentManager(), "dialog");
//			}
//		});
		project_list.addHeaderView(header);
		ProjectsAdapter adapter = new ProjectsAdapter(this,
				R.layout.project_selector_list_item,
				R.id.project_list_item_path);
		AddProjects(adapter);
		project_list.setAdapter(adapter);
		projects_dialog.show();
	}


	public void MarkersDialogClicked(final View button) {
		final int dialog_max_height = getWindowManager().getDefaultDisplay().getHeight() / 2;
		button.setActivated(true);
		markers_dialog = new Dialog(this, R.style.Theme_layers_dialog);
		markers_dialog.setContentView(R.layout.markers_dialog);
		markers_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		markers_dialog.setCanceledOnTouchOutside(true);

		markers_dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				button.setActivated(false);
			}
		});

		// Place dialog under the button
		LayoutParams parameters = markers_dialog.getWindow().getAttributes();
		parameters.height = dialog_max_height; // Some hard-coded size

		int[] button_location = { 0, 0 };
		button.getLocationOnScreen(button_location);

		// Official documentation says that this will give actual screen size,
		// without taking into account decor elements (status bar).
		// But it works exactly as I expected - gives full accessible window
		// size.
		int screenCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
		int screenCenterY = getWindowManager().getDefaultDisplay().getHeight() / 2;

		// Dialog's 0,0 coordinates are in the middle of the screen
		parameters.x = button_location[0] - screenCenterX + button.getWidth()
				/ 2;
		parameters.y = button_location[1] - screenCenterY + button.getHeight()
				+ parameters.height / 2;

		markers_dialog.getWindow().setAttributes(parameters);

		// Fill list with data
		ListView markers_list = (ListView) markers_dialog
				.findViewById(R.id.markers_list);
		MarkersAdapter adapter = new MarkersAdapter(this,
				R.layout.markers_list_item, R.id.markers_list_item_text);
		AddMarkers(adapter);
		markers_list.setAdapter(adapter);
		markers_dialog.show();
	}


	public void EditableLayersDialogClicked(final View button) {
		final int dialog_max_height = getWindowManager().getDefaultDisplay().getHeight() / 2;
		button.setActivated(true);
		editablelayers_dialog = new Dialog(this, R.style.Theme_layers_dialog);
		editablelayers_dialog.setContentView(R.layout.markers_dialog);
		editablelayers_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		editablelayers_dialog.setCanceledOnTouchOutside(true);

		editablelayers_dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				button.setActivated(false);
			}
		});

		// Place dialog under the button
		LayoutParams parameters = editablelayers_dialog.getWindow()
				.getAttributes();
		parameters.height = dialog_max_height; // Some hard-coded size

		int[] button_location = { 0, 0 };
		button.getLocationOnScreen(button_location);

		// Official documentation says that this will give actual screen size,
		// without taking into account decor elements (status bar).
		// But it works exactly as I expected - gives full accessible window
		// size.
		int screenCenterX = getWindowManager().getDefaultDisplay().getWidth() / 2;
		int screenCenterY = getWindowManager().getDefaultDisplay().getHeight() / 2;

		// Dialog's 0,0 coordinates are in the middle of the screen
		parameters.x = button_location[0] - screenCenterX + button.getWidth()
				/ 2;
		parameters.y = button_location[1] - screenCenterY + button.getHeight()
				+ parameters.height / 2;

		editablelayers_dialog.getWindow().setAttributes(parameters);

		// Fill list with data
		ListView markers_list = (ListView) editablelayers_dialog
				.findViewById(R.id.markers_list);
		/**/
		View header = getLayoutInflater().inflate(
				R.layout.editable_layers_stop_edit, null);
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GIEditLayersKeeper.Instance().StopEditing();
				editablelayers_dialog.cancel();
			}
		});
		markers_list.addHeaderView(header);
		/**/
		EditableLayersAdapter adapter = new EditableLayersAdapter(this,
				R.layout.markers_list_item, R.id.markers_list_item_text);
		AddEditableLayers((GIGroupLayer) map.m_layers, adapter);
		// AddEditableLayers(adapter);
		markers_list.setAdapter(adapter);
		editablelayers_dialog.show();
	}

	//todo
	public void SettingsDialogClicked(final View button) {

		DialogFragment dlg = new SettingsDialog();
//		dlg.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//		dlg.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//		dlg.getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dlg.show(getSupportFragmentManager(), "settings_dialog" );


//		button.setActivated(true);
//
//		settings_dialog = new Dialog(this, R.style.Theme_layers_dialog);
//		settings_dialog.setContentView(R.layout.project_settings_dialog);
//		settings_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//		settings_dialog.setCanceledOnTouchOutside(true);
//
//		//
//		LayoutParams parameters = settings_dialog.getWindow().getAttributes();
//		settings_dialog.getWindow().setAttributes(parameters);
//		//
//
//		settings_dialog.setOnDismissListener(new OnDismissListener() {
//			public void onDismiss(DialogInterface dialog) {
//				button.setActivated(false);
//			}
//		});
//
//		ListView project_list = (ListView) settings_dialog.findViewById(R.id.projects_list);
//		ListView layers_list = (ListView) settings_dialog.findViewById(R.id.layers_list);
//		FrameLayout attributes_frame = (FrameLayout) settings_dialog.findViewById(R.id.attributes_frame);
//
//
//
//
//		ProjectsAdapter projects_adapter = new ProjectsAdapter(this,
//				R.layout.project_selector_list_item,
//				R.id.project_list_item_path);
//		AddProjects(projects_adapter);
//		project_list.setAdapter(projects_adapter);
//
//		/**/
//		LayersAdapter adapter = new LayersAdapter(this,
//				R.layout.layers_list_item, R.id.layers_list_item_text);
//
//		View header = getLayoutInflater().inflate(
//				R.layout.add_layer_header_layout, null);
//		header.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				OpenFileDialog dlg = new OpenFileDialog(getApplicationContext());
//				dlg.setIFolderItemListener(m_fileOpenListener);
//				dlg.show(getFragmentManager(), "open_dlg");
//			}
//		});
//		layers_list.addHeaderView(header);
//
////		ImageButton additional = (ImageButton) layers_dialog
////				.findViewById(R.id.layers_additional_button);
////		additional.setOnClickListener(new OnClickListener() {
////
////			@Override
////			public void onClick(View v) {
////				OpenFileDialog dlg = new OpenFileDialog(getApplicationContext());
////				dlg.setIFolderItemListener(m_fileOpenListener);
////				dlg.show(getFragmentManager(), "open_dlg");
////				layers_dialog.dismiss();
////			}
////		});
//
//		addLayers((GIGroupLayer) map.m_layers, adapter);
//		layers_list.setAdapter(adapter);
//		/**/
//
//
//
//		settings_dialog.show();
//
//
//		/**/
////		FragmentTransaction ft = ((FragmentActivity) settings_dialog.getOwnerActivity()).getFragmentManager().beginTransaction();
////		ft.add(R.id.attributes_frame, new SettingsFragment()).commit();
//		/**/
	}




	public void LoadPro(String path) {
		map.ps = new GIProjectProperties(path);
		GIBounds temp = new GIBounds(map.ps.m_projection, map.ps.m_left,
				map.ps.m_top, map.ps.m_right, map.ps.m_bottom);
		map.InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
		touchControl.InitMap(map);
		GIPropertiesGroup current_group = map.ps.m_Group;
		GIEditLayersKeeper.Instance().ClearLayers();
		loadGroup(current_group);
	}


	private void loadGroup(GIPropertiesGroup current_layer2)
	{
		for (GIPropertiesLayer current_layer : current_layer2.m_Entries)
		{
			if (current_layer.m_type == GILayerType.LAYER_GROUP) {
				loadGroup((GIPropertiesGroup) current_layer);
			}
			if (current_layer.m_type == GILayerType.TILE_LAYER) {
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
					layer = GILayer.CreateLayer(
							current_layer.m_source.GetLocalPath(),
							GILayerType.TILE_LAYER);
					layer.setName(current_layer.m_name);
					layer.m_layer_properties = current_layer;
					// map.AddLayer(layer);
					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
				} else {
					continue;
				}

			}
			if (current_layer.m_type == GILayerType.ON_LINE) {
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("text")) {
					layer = GILayer.CreateLayer(
							current_layer.m_source.GetRemotePath(),
							GILayerType.ON_LINE);
					layer.setName(current_layer.m_name);
					layer.m_layer_properties = current_layer;
					// map.AddLayer(layer);
					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
				} else {
					continue;
				}

			}
			if (current_layer.m_type == GILayerType.SQL_LAYER) 
			{
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("text")) 
				{
					layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayerType.SQL_LAYER);
					layer.setName(current_layer.m_name);
					if (current_layer.m_sqldb != null) {
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("SMART")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.SMART;
							((GISQLLayer) layer).m_max_z = current_layer.m_sqldb.m_max_z;
							((GISQLLayer) layer).m_min_z = current_layer.m_sqldb.m_min_z;
						}
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("AUTO")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.AUTO;
						}
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("ADAPTIVE")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.ADAPTIVE;
							((GISQLLayer) layer).getAvalibleLevels();
						}
					}
					layer.m_layer_properties = current_layer;
					map.AddLayer(layer,	new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
				} 
				else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
				{
					layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayerType.SQL_LAYER);
					layer.setName(current_layer.m_name);
					if (current_layer.m_sqldb != null) {
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("SMART")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.SMART;
							((GISQLLayer) layer).m_max_z = current_layer.m_sqldb.m_max_z;
							((GISQLLayer) layer).m_min_z = current_layer.m_sqldb.m_min_z;
						}
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("AUTO")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.AUTO;
						}
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("ADAPTIVE")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.ADAPTIVE;
							((GISQLLayer) layer).getAvalibleLevels();
						}
					}
					layer.m_layer_properties = current_layer;
					// map.AddLayer(layer);
					map.AddLayer(layer,	new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
				}
				else
				{
					continue;
				}

			}
			if (current_layer.m_type == GILayerType.SQL_YANDEX_LAYER) {
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("text")) 
				{
					layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayerType.SQL_YANDEX_LAYER);
					layer.setName(current_layer.m_name);
					if (current_layer.m_sqldb != null) {
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("SMART")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.SMART;
							((GISQLLayer) layer).m_max_z = current_layer.m_sqldb.m_max_z;
							((GISQLLayer) layer).m_min_z = current_layer.m_sqldb.m_min_z;
						}
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("AUTO")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.AUTO;
						}
						if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("ADAPTIVE")) 
						{
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.ADAPTIVE;
							((GISQLLayer) layer).getAvalibleLevels();
						}
					}
					layer.m_layer_properties = current_layer;
					map.AddLayer(layer,	new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
				} 
				else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
				{
					layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayerType.SQL_YANDEX_LAYER);
					layer.setName(current_layer.m_name);
					if (current_layer.m_sqldb != null) {
						if (current_layer.m_sqldb.m_zoom_type
								.equalsIgnoreCase("SMART")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.SMART;
							((GISQLLayer) layer).m_max_z = current_layer.m_sqldb.m_max_z;
							((GISQLLayer) layer).m_min_z = current_layer.m_sqldb.m_min_z;
						}
						if (current_layer.m_sqldb.m_zoom_type
								.equalsIgnoreCase("AUTO")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.AUTO;
						}
						if (current_layer.m_sqldb.m_zoom_type
								.equalsIgnoreCase("ADAPTIVE")) {
							((GISQLLayer) layer).m_zooming_type = GISQLiteZoomingType.ADAPTIVE;
							((GISQLLayer) layer).getAvalibleLevels();
						}
					}
					layer.m_layer_properties = current_layer;
					// map.AddLayer(layer);
					map.AddLayer(layer,	new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
				}
				else
				{
					continue;
				}

			}			
			if (current_layer.m_type == GILayerType.DBASE) {
				Paint fill = new Paint();
				Paint line = new Paint();
				for (GIColor color : current_layer.m_style.m_colors) {
					if (color.m_description.equalsIgnoreCase("line")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						line.setStyle(Style.STROKE);
						line.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
					} else if (color.m_description.equalsIgnoreCase("fill")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						fill.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
						fill.setStyle(Style.FILL);
					}
				}

				Paint editing_fill = new Paint();
				editing_fill.setColor(Color.CYAN);
				editing_fill.setAlpha(96);
				editing_fill.setStyle(Style.FILL);

				Paint editing_stroke = new Paint();
				editing_stroke.setColor(Color.CYAN);
				editing_stroke.setStrokeWidth(2);
				editing_fill.setAlpha(128);
				editing_stroke.setStyle(Style.STROKE);
				GIVectorStyle vstyle_editing = new GIVectorStyle(
						editing_stroke, editing_fill,
						(int) current_layer2.m_opacity);

				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
					GIVectorStyle vstyle = new GIVectorStyle(line, fill,
							(int) current_layer2.m_opacity);
					layer = GILayer
							.CreateLayer(current_layer.m_source.GetLocalPath(),
									GILayerType.DBASE, vstyle,
									current_layer.m_encoding);

					layer.setName(current_layer.m_name);

					layer.m_layer_properties = current_layer;
					layer.AddStyle(vstyle_editing);
					/**/
					for (GIPropertiesLayerRef ref : map.ps.m_Edit.m_Entries) {
						if (ref.m_name.equalsIgnoreCase(current_layer.m_name)) {
							GIEditableSQLiteLayer l = (GIEditableSQLiteLayer) layer;
							if (ref.m_type.equalsIgnoreCase("POINT")) {
								l.setType(GIEditableLayerType.POINT);
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("LINE")) {
								l.setType(GIEditableLayerType.LINE);
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("POLYGON")) {
								l.setType(GIEditableLayerType.POLYGON);
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("TRACK")) {
								l.setType(GIEditableLayerType.TRACK);
								continue;
							}
						}
					}
					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
					GIEditLayersKeeper.Instance().AddLayer(
							(GIEditableSQLiteLayer) layer);
				}

				else {
					continue;
				}
			}
			//
			if (current_layer.m_type == GILayerType.XML) {
				Paint fill = new Paint();
				Paint line = new Paint();
				for (GIColor color : current_layer.m_style.m_colors) {
					if (color.m_description.equalsIgnoreCase("line")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						line.setStyle(Style.STROKE);
						line.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
					} else if (color.m_description.equalsIgnoreCase("fill")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						fill.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
						fill.setStyle(Style.FILL);
					}
				}

				Paint editing_fill = new Paint();
				editing_fill.setColor(Color.CYAN);
				editing_fill.setAlpha(96);
				editing_fill.setStyle(Style.FILL);

				Paint editing_stroke = new Paint();
				editing_stroke.setColor(Color.CYAN);
				editing_stroke.setStrokeWidth(2);
				editing_fill.setAlpha(128);
				editing_stroke.setStyle(Style.STROKE);
				GIVectorStyle vstyle_editing = new GIVectorStyle(
						editing_stroke, editing_fill,
						(int) current_layer2.m_opacity);

				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
					GIVectorStyle vstyle = new GIVectorStyle(line, fill,
							(int) current_layer2.m_opacity);
					layer = GILayer.CreateLayer(
							current_layer.m_source.GetLocalPath(),
							GILayerType.XML, vstyle, current_layer.m_encoding);

					layer.setName(current_layer.m_name);
					layer.m_layer_properties = current_layer;

					layer.AddStyle(vstyle_editing);
					/**/
					for (GIPropertiesLayerRef ref : map.ps.m_Edit.m_Entries) {
						if (ref.m_name.equalsIgnoreCase(current_layer.m_name)) {
							GIEditableLayer l = (GIEditableLayer) layer;
							if (ref.m_type.equalsIgnoreCase("POINT")) {
								l.setType(GIEditableLayerType.POINT);
								GIEditLayersKeeper.Instance().m_POILayer = l;
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("LINE")) {
								l.setType(GIEditableLayerType.LINE);
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("POLYGON")) {
								l.setType(GIEditableLayerType.POLYGON);
								continue;
							}
							if (ref.m_type.equalsIgnoreCase("TRACK")) {
								GIEditLayersKeeper.Instance().m_TrackLayer = l;
								l.setType(GIEditableLayerType.TRACK);
								continue;
							}
						}
					}
					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
					GIEditLayersKeeper.Instance().AddLayer(
							(GIEditableLayer) layer);
				}

				else {
					continue;
				}
			}
			//TODO
			if (current_layer.m_type == GILayerType.PLIST) 
			{
				Paint fill = new Paint();
				Paint line = new Paint();
				for (GIColor color : current_layer.m_style.m_colors) {
					if (color.m_description.equalsIgnoreCase("line")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							line.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						line.setStyle(Style.STROKE);
						line.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
					} else if (color.m_description.equalsIgnoreCase("fill")) {
						if (color.m_name.equalsIgnoreCase("custom")) {
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						} else {
							color.setFromName();
							fill.setARGB(color.m_alpha, color.m_red,
									color.m_green, color.m_blue);
						}
						fill.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
						fill.setStyle(Style.FILL);
					}
				}


				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
					GIVectorStyle vstyle = new GIVectorStyle(line, fill,
							(int) current_layer2.m_opacity);
					layer = GILayer.CreateLayer(
							current_layer.m_source.GetLocalPath(),
							GILayerType.PLIST, vstyle, current_layer.m_encoding);

					layer.setName(current_layer.m_name);
					layer.m_layer_properties = current_layer;

					map.AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
					GIEditLayersKeeper.Instance().AddLayer(
							(GIEditableLayer) layer);
				}
			}

		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		//getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);
		// ?????
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);
		touchControl = (GITouchControl) findViewById(R.id.touchcontrol);

		createMap();

		GIEditLayersKeeper.Instance().setFragmentManager(getFragmentManager());
		GIEditLayersKeeper.Instance().setTouchControl(touchControl);
		GIEditLayersKeeper.Instance().setMap(map);
		GIEditLayersKeeper.Instance().setRoot(R.id.root);

		// Setup pixel size to let scale work properly
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		double screenPixels = Math.hypot(dm.widthPixels, dm.heightPixels);
		double screenInches = Math.hypot(dm.widthPixels / dm.xdpi,
				dm.heightPixels / dm.ydpi);
		GIMap.inches_per_pixel = screenInches / screenPixels;
		

		/**/
		
		//TODO uncomment
//		m_location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,	5, 5, m_location_listener);
//		m_location_manager.requestLocationUpdates(	LocationManager.NETWORK_PROVIDER, 5, 5, m_location_listener);
		
		m_location_listener = new GIGPSLocationListener(map);
		GIEditLayersKeeper.Instance().m_location_manager = m_location_listener.m_location_manager;

		GIScaleControl m_scale_control_fixed = (GIScaleControl) findViewById(R.id.scale_control_screen);
		m_scale_control_fixed.setMap(map);
		//--------------------------------------------------------------------
		// floating buttons
		//--------------------------------------------------------------------

		//--------------------------------------------------------------------
		// GPS buttons
		//--------------------------------------------------------------------
		fbGPS = new GIGPSButtonView(this);
		FloatingActionButton.LayoutParams gps_menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(96), ScreenUtils.dpToPx(96));
		gps_menu_params.setMargins(ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2));

		FloatingActionButton gps_action_button = new FloatingActionButton.Builder(this)
				.setContentView(fbGPS)
				.setBackgroundDrawable(null)
				.setPosition(FloatingActionButton.POSITION_TOP_LEFT)
				.setLayoutParams(gps_menu_params)
				.build();

		SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
		FloatingActionButton.LayoutParams action_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(84), ScreenUtils.dpToPx(84));
		itemBuilder.setLayoutParams(action_params);
		fbGPS.SetGPSEnabledStatus(m_location_listener.m_location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER));

		//--------------------------------------------------------------------
		// GPS AUTO_FOLL0W
		//--------------------------------------------------------------------
//		final CheckBox m_btnAutoFollow = (CheckBox)getLayoutInflater().inflate(R.layout.button_autofollow, null);
		final CheckBox m_btnAutoFollow = new CheckBox(this);
		m_btnAutoFollow.setButtonDrawable(R.drawable.auto_follow_status_);
		SubActionButton fbAutoFollow = itemBuilder.setContentView(m_btnAutoFollow).build();
		m_btnAutoFollow.setChecked(GIEditLayersKeeper.Instance().m_AutoFollow);
		m_btnAutoFollow.setOnClickListener(new View.OnClickListener()
			{
			@Override
			public void onClick(View v) {
				GIEditLayersKeeper.Instance().m_AutoFollow = m_btnAutoFollow.isChecked();
				if (m_btnAutoFollow.isChecked()) {
					Location location = GIEditLayersKeeper.Instance().m_location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (location != null) {
						GILonLat go_to = GILonLat.fromLocation(location);
						GILonLat go_to_map = GIProjection.ReprojectLonLat(go_to, GIProjection.WGS84(), GIProjection.WorldMercator());
						GIEditLayersKeeper.Instance().getMap().SetCenter(go_to_map);
						GIEditLayersKeeper.Instance().GetPositionControl();
					}
				}
				GIEditLayersKeeper.Instance().GetPositionControl();
			}
		});
		fbAutoFollow.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				GIEditLayersKeeper.Instance().m_current_track_control.Show(!GIEditLayersKeeper.Instance().m_current_track_control.mShow);
				return false;
			}
		});

		//--------------------------------------------------------------------
		// GPS TRACK_CONTROL
		//--------------------------------------------------------------------
//		final CheckBox m_btnTrackControl = (CheckBox)getLayoutInflater().inflate(R.layout.button_trackcontrol, null);
		final CheckBox m_btnTrackControl = new CheckBox(this);
		m_btnTrackControl.setButtonDrawable(R.drawable.stop_start_track_button);
		SubActionButton fbTrackControl = itemBuilder.setContentView(m_btnTrackControl).build();
		m_btnTrackControl.setChecked(GIEditLayersKeeper.Instance().m_TrackingStatus == GIEditLayersKeeper.GITrackingStatus.WRITE);
		m_btnTrackControl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (GIEditLayersKeeper.Instance().m_TrackingStatus == GIEditLayersKeeper.GITrackingStatus.STOP) {
					if (!GIEditLayersKeeper.Instance().CreateTrack()) {
						GIEditLayersKeeper.Instance().m_TrackingStatus = GIEditLayersKeeper.GITrackingStatus.STOP;
						m_btnTrackControl.setChecked(false);
					}
				} else {
					GIEditLayersKeeper.Instance().m_TrackingStatus = GIEditLayersKeeper.GITrackingStatus.STOP;
					GIEditLayersKeeper.Instance().StopTrack();
				}
			}
		});

		//--------------------------------------------------------------------
		// GPS SHOW TRACK
		//--------------------------------------------------------------------
//		final CheckBox m_btnShowTrack = (CheckBox)getLayoutInflater().inflate(R.layout.button_show_track, null);
		final CheckBox m_btnShowTrack = new CheckBox(this);
		m_btnShowTrack.setButtonDrawable(R.drawable.fixable_button);
		SubActionButton fbShowTrack = itemBuilder.setContentView(m_btnShowTrack).build();
		m_btnShowTrack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(GIEditLayersKeeper.Instance().m_current_track_control != null) {
					GIEditLayersKeeper.Instance().m_current_track_control.Show(m_btnShowTrack.isChecked());
					GIEditLayersKeeper.Instance().getMap().UpdateMap();
				}
			}
		});

		//--------------------------------------------------------------------
		// GPS POI CONTROL
		//--------------------------------------------------------------------
//		final CheckBox m_btnPoiControl = (CheckBox)getLayoutInflater().inflate(R.layout.button_poi_control, null);
		final CheckBox m_btnPoiControl = new CheckBox(this);
		m_btnPoiControl.setButtonDrawable(R.drawable.poi_status);
		SubActionButton fbPoiControl = itemBuilder.setContentView(m_btnPoiControl).build();
		m_btnPoiControl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(GIEditLayersKeeper.Instance().getState() != GIEditLayersKeeper.GIEditingStatus.EDITING_POI && GIEditLayersKeeper.Instance().getState() != GIEditLayersKeeper.GIEditingStatus.EDITING_GEOMETRY)
				{
					GIEditLayersKeeper.Instance().CreatePOI();
				}
				else
				{
					GIEditLayersKeeper.Instance().StopEditing();
				}
			}
		});
		//--------------------------------------------------------------------
		// GPS buttons
		//--------------------------------------------------------------------
		FloatingActionMenu gpsActionMenu = new FloatingActionMenu.Builder(this)

				.addSubActionView(fbAutoFollow)
				.addSubActionView(fbTrackControl)
				.addSubActionView(fbShowTrack)
				.addSubActionView(fbPoiControl)

				.attachTo(gps_action_button)
				.setRadius(ScreenUtils.dpToPx(150))
				.setStartAngle(0)
				.setEndAngle(90)
				.build();
		//--------------------------------------------------------------------
		// GPS buttons
		//--------------------------------------------------------------------

		//--------------------------------------------------------------------
		// Compass buttons
		//--------------------------------------------------------------------
		GICompassView fbCompass = new GICompassView(this);
		FloatingActionButton.LayoutParams compass_menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(96), ScreenUtils.dpToPx(96));
		compass_menu_params.setMargins(ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2));

		FloatingActionButton compass_action_button = new FloatingActionButton.Builder(this)
				.setContentView(fbCompass)
				.setBackgroundDrawable(null)
				.setPosition(FloatingActionButton.POSITION_TOP_RIGHT)
				.setLayoutParams(compass_menu_params)
				.build();

		SubActionButton.Builder compass_item_builder = new SubActionButton.Builder(this);
		FloatingActionButton.LayoutParams compass_action_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(84), ScreenUtils.dpToPx(84));
		compass_item_builder.setLayoutParams(compass_action_params);

		//--------------------------------------------------------------------
		// COMPASS_OPEN_BUTTON
		//--------------------------------------------------------------------
		final ImageButton btnProjectSelectorButton = new ImageButton(this);
		btnProjectSelectorButton.setImageResource(R.drawable.open);
		btnProjectSelectorButton.setBackgroundDrawable(null);
		SubActionButton fbOpen = itemBuilder.setContentView(btnProjectSelectorButton).build();
		btnProjectSelectorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				ProjectSelectorDialogClicked(v);
				SettingsDialogClicked(v);
			}
		});
		//--------------------------------------------------------------------
		// COMPASS_OPEN_Layers
		//--------------------------------------------------------------------
		final ImageButton btnLayers = new ImageButton(this);
		btnLayers.setImageResource(R.drawable.top_bar_layers_button);
		btnLayers.setBackgroundDrawable(null);
		SubActionButton fbLayers = itemBuilder.setContentView(btnLayers).build();
		btnLayers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layersDialogClicked(v);

			}
		});
		//--------------------------------------------------------------------
		// COMPASS_EDIT_Layers
		//--------------------------------------------------------------------
		final ImageButton btnEditLayers = new ImageButton(this);
		btnEditLayers.setImageResource(android.R.drawable.ic_menu_edit);
		btnEditLayers.setBackgroundDrawable(null);
		SubActionButton fbEditLayers = itemBuilder.setContentView(btnEditLayers).build();
		btnEditLayers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditableLayersDialogClicked(v);
			}
		});
		//--------------------------------------------------------------------
		// COMPASS_MARKERS
		//--------------------------------------------------------------------
		final ImageButton btnMarkers = new ImageButton(this);
		btnMarkers.setImageResource(R.drawable.poi);
		btnMarkers.setBackgroundDrawable(null);
		SubActionButton fbMarkers= itemBuilder.setContentView(btnMarkers).build();
		btnMarkers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MarkersDialogClicked(v);
			}
		});
		//--------------------------------------------------------------------
		// Compass buttons
		//--------------------------------------------------------------------
		FloatingActionMenu compassActionMenu = new FloatingActionMenu.Builder(this)

				.addSubActionView(fbOpen)
				.addSubActionView(fbLayers)
				.addSubActionView(fbEditLayers)
				.addSubActionView(fbMarkers)

				.attachTo(compass_action_button)
				.setRadius(ScreenUtils.dpToPx(150))
				.setStartAngle(90)
				.setEndAngle(180)
				.build();
		//--------------------------------------------------------------------
		// Compass buttons
		//--------------------------------------------------------------------

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	// ToDo
	@Override
	protected void onResume() {
		super.onResume();
		GIEditLayersKeeper.Instance().onResume();
		GISensors.Instance(this).run(true);
//		m_gps_button.onResume();
		fbGPS.onResume();

	};

	// ToDo
	@Override
	protected void onStop() {
		super.onStop();
		// GIEditLayersKeeper.Instance().m_position = null;
	};

	// ToDo
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// GIEditLayersKeeper.Instance().m_position = null;
	};

	// ToDo
	@Override
	protected void onPause() {
		super.onPause();
		GIEditLayersKeeper.Instance().onPause();
		GISensors.Instance(this).run(false);
//		m_gps_button.onPause();
		fbGPS.onPause();
		// GIEditLayersKeeper.Instance().m_position = null;
		map.Synhronize();
		String SaveAsPath = getResources().getString(R.string.default_project_path);
		if (map.ps.m_SaveAs != null) {
			if (map.ps.m_SaveAs.length() > 0) {
				SaveAsPath = map.ps.m_SaveAs;
			}
		}
		map.ps.SavePro(SaveAsPath);
	};

	//

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		GIBounds bounds = new GIBounds(GIProjection.WorldMercator(),
				savedInstanceState.getFloat("b_left"),
				savedInstanceState.getFloat("b_top"),
				savedInstanceState.getFloat("b_right"),
				savedInstanceState.getFloat("b_bottom"));

		map.InitBounds(bounds);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO: For now layers are re-created. They should be re-used.
		outState.putFloat("b_left", (float) map.Bounds().left());
		outState.putFloat("b_top", (float) map.Bounds().top());
		outState.putFloat("b_right", (float) map.Bounds().right());
		outState.putFloat("b_bottom", (float) map.Bounds().bottom());
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	private boolean createMap() {
		map = (GIMap) findViewById(R.id.map);
		View parent = findViewById(R.id.root);
		parent.setBackgroundColor(Color.WHITE);
		sp = getPreferences(MODE_PRIVATE);
		String path = sp.getString(SAVED_PATH,
				getResources().getString(R.string.default_project_path));
		// TODO
		try {
			LoadPro(path);
			return true;
		} catch (Exception e) {

			// ProjectSelectorDialog();
			GIBounds temp = new GIBounds(GIProjection.WGS84(), 0, 90, 90, 0);
			map.InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
			map.ps = new GIProjectProperties();
			sp = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(SAVED_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + map.ps.m_SaveAs);
			editor.apply();
			editor.commit();
			touchControl.InitMap(map);
			return false;
		}

	}

	@Override
	public void OnCannotFileRead(File file) {
		Toast.makeText(getApplicationContext(), "can't be read!",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void OnFileClicked(File file) {
		addLayer(file);
	}

	public void addLayer(File file) {
		String filenameArray[] = file.getName().split("\\.");
		String extention = filenameArray[filenameArray.length - 1];
		if (extention.equalsIgnoreCase("sqlitedb")) 
		{
			GIPropertiesLayer properties_layer = new GIPropertiesLayer();
			properties_layer.m_enabled = true;
			properties_layer.m_name = file.getName();
			properties_layer.m_range = new GIRange();
			properties_layer.m_source = new GISource("absolute", file.getAbsolutePath()); //getName()
			properties_layer.m_type = GILayerType.SQL_LAYER;
			properties_layer.m_strType = "SQL_LAYER";
			GILayer layer;
			//TODO
			layer = GILayer.CreateLayer(properties_layer.m_source.GetAbsolutePath(), GILayerType.SQL_LAYER);
			//layer = GILayer.CreateLayer(file.getName(), GILayerType.SQL_LAYER);
			properties_layer.m_sqldb = new GISQLDB();//"auto";
			properties_layer.m_sqldb.m_zoom_type = "auto";

			properties_layer.m_sqldb.m_min_z = ((GISQLLayer)layer).m_min;
			properties_layer.m_sqldb.m_max_z = ((GISQLLayer)layer).m_max;
			((GISQLLayer)layer).m_min_z = ((GISQLLayer)layer).m_min;
			((GISQLLayer)layer).m_max_z = ((GISQLLayer)layer).m_max;
			int min = ((GISQLLayer)layer).m_min;
			int max = ((GISQLLayer)layer).m_max;
//			if(min > 0)
//			{
//				min = min - 1;
//			}
			
			properties_layer.m_range = new GIRange();
			double con = 0.0254*0.0066*256/(0.5*40000000);
			properties_layer.m_range.m_from = (int)( 1/(Math.pow(2,  min)*con));
			properties_layer.m_range.m_to =  (int) ( 1/(Math.pow(2,  max)*con));

			map.ps.m_Group.addEntry(properties_layer);
			layer.setName(file.getName());
			layer.m_layer_properties = properties_layer;
			map.InsertLayerAt(layer, 0);
		}
		//else if(extention.equalsIgnoreCase("xml"))
		map.UpdateMap();
	}

	public GIMap getMap() {
		return map;
	}

	public Dialog getProjectsDialog() {
		return projects_dialog;
	}

	public Dialog getMarkersDialog() {
		return markers_dialog;
	}

	public GIControlFloating getMarkerPoint() {
		return m_marker_point;
	}

	public Dialog getEditablelayersDialog() {
		return editablelayers_dialog;
	}
}
