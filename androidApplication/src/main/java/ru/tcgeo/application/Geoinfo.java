package ru.tcgeo.application;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.tcgeo.application.data.interactors.LoadProjectInteractor;
import ru.tcgeo.application.gilib.gps.GICompassView;
import ru.tcgeo.application.gilib.gps.GISensors;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.GIControlFloating;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.GITouchControl;
import ru.tcgeo.application.gilib.gps.GIGPSButtonView;
import ru.tcgeo.application.gilib.gps.GIGPSLocationListener;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;

import ru.tcgeo.application.home_screen.EditableLayersDialog;
import ru.tcgeo.application.home_screen.MarkersDialog;
import ru.tcgeo.application.home_screen.SettingsDialog;
import ru.tcgeo.application.home_screen.ProjectDialog;
import ru.tcgeo.application.utils.ScreenUtils;
import ru.tcgeo.application.view.MapView;
import ru.tcgeo.application.views.GIScaleControl;

//import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

public class Geoinfo extends FragmentActivity implements MapView {

	//
	@Bind(R.id.root)
	RelativeLayout root;

	@Bind(R.id.map)
	GIMap map;

	@Bind(R.id.touchcontrol)
	GITouchControl touchControl;

	@Bind(R.id.scale_control_screen)
	GIScaleControl scaleControl;

	@Bind(R.id.pbProgress)
	View pbProgress;

	SharedPreferences sp;

	final public String SAVED_PATH = "default_project_path";
	DialogFragment projectsDialog;
	DialogFragment markersDialog;
	DialogFragment editablelayersDialog;

	GIControlFloating m_marker_point;

	GIGPSLocationListener m_location_listener;

	GIGPSButtonView fbGPS;

	ImageButton fbEdit;

    public CheckBox btnEditCreate;
    public CheckBox btnEditGeometry;
    public CheckBox btnEditAttributes;
    public CheckBox btnEditDelete;

    public SubActionButton fbEditCreate;
    public SubActionButton fbEditGeometry;
    public SubActionButton fbEditAttributes;
    public SubActionButton fbEditDelete;

    public FloatingActionButton fbEditButton;

	public void MarkersDialogClicked(final View button) {
		markersDialog = new MarkersDialog();
		markersDialog.show(getSupportFragmentManager(), "markers_dialog");
	}


	public void EditableLayersDialogClicked(final View button) {
		editablelayersDialog = new EditableLayersDialog();
		editablelayersDialog.show(getSupportFragmentManager(), "markers_dialog");
	}
    public void ProjectSelectorDialogClicked(final View button) {
        projectsDialog = new ProjectDialog();
        projectsDialog.show(getSupportFragmentManager(), "project_dialog");
    }

    public DialogFragment getProjectsDialog(){
        return projectsDialog;
    }


	public void SettingsDialogClicked(final View button) {
		DialogFragment dlg = new SettingsDialog();
		dlg.show(getSupportFragmentManager(), "settings_dialog");
	}


	public void LoadProject(String path) {

//		GIBounds temp = new GIBounds(GIProjection.WGS84(), 0, 90, 90, 0);
//		map.InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
		pbProgress.setVisibility(View.VISIBLE);
		LoadProjectInteractor interactor = new LoadProjectInteractor();
		interactor.setView(this);
		interactor.loadProject(path);

//		map.LoadProject(path);

	}


    @Override
    public void onProject(GIProjectProperties ps) {
        GIEditLayersKeeper.Instance().ClearLayers();
        GIBounds temp = new GIBounds(ps.m_projection, ps.m_left,
                ps.m_top, ps.m_right, ps.m_bottom);
        map.InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
        touchControl.InitMap(map);
        map.ps = ps;
    }

    @Override
    public void onLayer(LoadProjectInteractor.Layer layer) {
        map.AddLayer(layer.giLayer, layer.giRange, layer.enabled);
    }

    @Override
    public void onComplited() {
        pbProgress.setVisibility(View.INVISIBLE);
//        map.UpdateMap();
    }

    @Override
    public void onError() {

		map.ps = new GIProjectProperties();
		sp = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(SAVED_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + map.ps.m_SaveAs);
		editor.apply();
		editor.commit();
		touchControl.InitMap(map);
        pbProgress.setVisibility(View.INVISIBLE);
		GIBounds temp = new GIBounds(GIProjection.WGS84(), 28, 65, 48, 46);
		map.InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
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
		ButterKnife.bind(this);


//		sp = getPreferences(MODE_PRIVATE);
//		String path = sp.getString(SAVED_PATH, getResources().getString(R.string.default_project_path));
//		LoadProject(path);
//
//		GIEditLayersKeeper.Instance().setFragmentManager(getFragmentManager());
//		GIEditLayersKeeper.Instance().setTouchControl(touchControl);
//		GIEditLayersKeeper.Instance().setMap(map);
//        GIEditLayersKeeper.Instance().setActivity(this);
//		GIEditLayersKeeper.Instance().setRoot(R.id.root);
//
//		// Setup pixel size to let scale work properly
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		double screenPixels = Math.hypot(dm.widthPixels, dm.heightPixels);
//		double screenInches = Math.hypot(dm.widthPixels / dm.xdpi,
//				dm.heightPixels / dm.ydpi);
//		GIMap.inches_per_pixel = screenInches / screenPixels;
//
//
//		/**/
//
//		//TODO uncomment
////		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,	5, 5, m_location_listener);
////		locationManager.requestLocationUpdates(	LocationManager.NETWORK_PROVIDER, 5, 5, m_location_listener);
//
////		m_location_listener = new GIGPSLocationListener(map);
//		m_location_listener = new GIGPSLocationListener((LocationManager) getSystemService(Context.LOCATION_SERVICE));
//		GIEditLayersKeeper.Instance().locationManager = m_location_listener.locationManager;
//
////		GIScaleControl m_scale_control_fixed = (GIScaleControl) findViewById(R.id.scale_control_screen);
//		scaleControl.setMap(map);

        m_location_listener = new GIGPSLocationListener(this);
        GIEditLayersKeeper.Instance().m_location_manager = m_location_listener.locationManager;		//--------------------------------------------------------------------
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
        action_params.gravity = Gravity.CENTER_HORIZONTAL;
		itemBuilder.setLayoutParams(action_params);


		fbGPS.SetGPSEnabledStatus(m_location_listener.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));

		//--------------------------------------------------------------------
		// GPS AUTO_FOLL0W
		//--------------------------------------------------------------------
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

		//--------------------------------------------------------------------
		// GPS TRACK_CONTROL
		//--------------------------------------------------------------------
		final CheckBox m_btnTrackControl = new CheckBox(this);
        m_btnTrackControl.setTextSize(0);
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
		final CheckBox m_btnShowTrack = new CheckBox(this);
        m_btnShowTrack.setTextSize(0);
		m_btnShowTrack.setButtonDrawable(R.drawable.show_track);
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
		final ImageButton m_btnPoiControl = new ImageButton(this);
        m_btnPoiControl.setImageResource(R.drawable.poi_status);
        m_btnPoiControl.setBackgroundDrawable(null);
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
				.setRadius(ScreenUtils.dpToPx(144))
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
				ProjectSelectorDialogClicked(v);
			}
		});
		//--------------------------------------------------------------------
		// COMPASS_OPEN_Layers
		//--------------------------------------------------------------------
		final ImageButton btnLayers = new ImageButton(this);
		btnLayers.setImageResource(R.drawable.gear);
		btnLayers.setBackgroundDrawable(null);
		SubActionButton fbLayers = itemBuilder.setContentView(btnLayers).build();
		btnLayers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingsDialogClicked(v);

			}
		});
		//--------------------------------------------------------------------
		// COMPASS_EDIT_Layers
		//--------------------------------------------------------------------
		final ImageButton btnEditLayers = new ImageButton(this);
		btnEditLayers.setImageResource(R.drawable.edit);
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
				.setRadius(ScreenUtils.dpToPx(144))
				.setStartAngle(90)
				.setEndAngle(180)
				.build();
		//--------------------------------------------------------------------
		// Compass buttons
		//--------------------------------------------------------------------

		//--------------------------------------------------------------------
		// Edit buttons
		//--------------------------------------------------------------------
        btnEditCreate = new CheckBox(this);
        btnEditCreate.setTextSize(0);
        btnEditCreate.setButtonDrawable(R.drawable.edit_create_bg);
        btnEditCreate.setBackgroundDrawable(null);
        fbEditCreate = itemBuilder.setContentView(btnEditCreate).build();


        btnEditGeometry = new CheckBox(this);
        btnEditGeometry.setTextSize(0);
        btnEditGeometry.setButtonDrawable(R.drawable.edit_geometry_bg);
        btnEditGeometry.setBackgroundDrawable(null);
        fbEditGeometry = itemBuilder.setContentView(btnEditGeometry).build();


        btnEditAttributes = new CheckBox(this);
        btnEditAttributes.setTextSize(0);
        btnEditAttributes.setButtonDrawable(R.drawable.edit_attributes_bg);
        btnEditAttributes.setBackgroundDrawable(null);
        fbEditAttributes = itemBuilder.setContentView(btnEditAttributes).build();

        btnEditDelete = new CheckBox(this);
        btnEditDelete.setTextSize(0);
        btnEditDelete.setButtonDrawable(R.drawable.edit_delete_bg);
        btnEditDelete.setBackgroundDrawable(null);
        fbEditDelete = itemBuilder.setContentView(btnEditDelete).build();


        btnEditCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((GIEditLayersKeeper.Instance().getState() != GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_OBJECT_NEWLOCATION) && (btnEditCreate.isChecked())) {
                    if (!GIEditLayersKeeper.Instance().CreateNewObject()) {
                        return;
                    }
                    GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_OBJECT_NEWLOCATION);
                    fbEditAttributes.setEnabled(false);
                    fbEditGeometry.setEnabled(false);
                    fbEditDelete.setEnabled(false);
                    btnEditAttributes.setChecked(false);
                    btnEditGeometry.setChecked(false);
                    btnEditDelete.setChecked(false);
                    GIEditLayersKeeper.Instance().UpdateMap();
                } else {
                    GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.RUNNING);
                    GIEditLayersKeeper.Instance().FillAttributes();
                    fbEditCreate.setEnabled(false);
                }
            }
        });
		btnEditGeometry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GIEditLayersKeeper.Instance().m_layer == GIEditLayersKeeper.Instance().m_TrackLayer){
                    return;
                }

                if((GIEditLayersKeeper.Instance().getState() == GIEditLayersKeeper.GIEditingStatus.EDITING_GEOMETRY)||(GIEditLayersKeeper.Instance().getState() == GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_SELECT_GEOMETRY_TO_EDITING)||(GIEditLayersKeeper.Instance().getState() == GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_NEW_POINT_LOCATION)){
                    GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.RUNNING);
                    GIEditLayersKeeper.Instance().StopEditingGeometry();
                    fbEditCreate.setEnabled(true);
                    fbEditAttributes.setEnabled(true);
                    fbEditDelete.setEnabled(true);
                    btnEditCreate.setChecked(false);
                    btnEditAttributes.setChecked(false);
                    btnEditDelete.setChecked(false);
                } else {
                    GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_SELECT_GEOMETRY_TO_EDITING);
                    fbEditCreate.setEnabled(false);
                    fbEditAttributes.setEnabled(false);
                    fbEditDelete.setEnabled(false);
                }
            }
        });
		btnEditAttributes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GIEditLayersKeeper.Instance().getState() != GIEditLayersKeeper.GIEditingStatus.WAITIN_FOR_SELECT_OBJECT) {
                    GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.WAITIN_FOR_SELECT_OBJECT);
                    fbEditCreate.setEnabled(false);
                    fbEditGeometry.setEnabled(false);
                    fbEditDelete.setEnabled(false);
                    btnEditCreate.setChecked(false);
                    btnEditGeometry.setChecked(false);
                    btnEditDelete.setChecked(false);
                } else {
                    GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.RUNNING);
                    fbEditCreate.setEnabled(true);
                    fbEditGeometry.setEnabled(true);
                    fbEditDelete.setEnabled(true);
                }
            }
        });

		btnEditDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_TO_DELETE);
            }
        });

		fbEdit = new ImageButton(this);
		FloatingActionButton.LayoutParams edit_menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(96), ScreenUtils.dpToPx(96));
		edit_menu_params.setMargins(ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2));
        fbEdit.setImageResource(R.drawable.edit);
        fbEdit.setBackgroundDrawable(null);
        fbEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GIEditLayersKeeper.Instance().m_layer == GIEditLayersKeeper.Instance().m_TrackLayer) {
                    fbEditGeometry.setVisibility(View.GONE);
                    fbEditCreate.setVisibility(View.GONE);
                } else {
                    fbEditGeometry.setVisibility(View.VISIBLE);
                    fbEditCreate.setVisibility(View.VISIBLE);
                }
            }
        });

		fbEditButton = new FloatingActionButton.Builder(this)
				.setContentView(fbEdit)
				.setBackgroundDrawable(null)
				.setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT)
				.setLayoutParams(edit_menu_params)
				.build();

        fbEditButton.setVisibility(View.GONE);
        fbEditButton.setActivated(false);

		FloatingActionButton.LayoutParams edit_action_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(84), ScreenUtils.dpToPx(84));
		edit_action_params.gravity = Gravity.CENTER_HORIZONTAL;
		itemBuilder.setLayoutParams(edit_action_params);

		FloatingActionMenu editActionMenu = new FloatingActionMenu.Builder(this)

				.addSubActionView(fbEditCreate)
                .addSubActionView(fbEditGeometry)
                .addSubActionView(fbEditAttributes)
                .addSubActionView(fbEditDelete)
                .attachTo(fbEditButton)
				.setRadius(ScreenUtils.dpToPx(144))
				.setStartAngle(180)
                .setEndAngle(270)
				.build();
		//--------------------------------------------------------------------
		// Edit buttons
		//--------------------------------------------------------------------


		sp = getPreferences(MODE_PRIVATE);
		String path = sp.getString(SAVED_PATH, getResources().getString(R.string.default_project_path));
		LoadProject(path);
		GIEditLayersKeeper.Instance().setFragmentManager(getFragmentManager());
		GIEditLayersKeeper.Instance().setTouchControl(touchControl);
		GIEditLayersKeeper.Instance().setMap(map);
		GIEditLayersKeeper.Instance().setActivity(this);
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
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,	5, 5, m_location_listener);
//		locationManager.requestLocationUpdates(	LocationManager.NETWORK_PROVIDER, 5, 5, m_location_listener);

//		m_location_listener = new GIGPSLocationListener(map);
//		m_location_listener = new GIGPSLocationListener((LocationManager) getSystemService(Context.LOCATION_SERVICE));
//		GIEditLayersKeeper.Instance().locationManager = m_location_listener.locationManager;

//		GIScaleControl m_scale_control_fixed = (GIScaleControl) findViewById(R.id.scale_control_screen);
		scaleControl.setMap(map);
//		fbGPS.SetGPSEnabledStatus(m_location_listener.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));

	}


	@Override
	protected void onResume() {
		super.onResume();
		GIEditLayersKeeper.Instance().onResume();
		GISensors.Instance(this).run(true);
		fbGPS.onResume();

	};


	@Override
	protected void onPause() {
		super.onPause();
		GIEditLayersKeeper.Instance().onPause();
		GISensors.Instance(this).run(false);
		fbGPS.onPause();
		map.Synhronize();
        String SaveAsPath = getResources().getString(R.string.default_project_path);
        if(map!=null && map.ps!= null && map.ps.m_path!= null && !map.ps.m_path.isEmpty()){
            SaveAsPath = map.ps.m_path;
        }
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

    public GIMap getMap() {
		return map;
	}

	public DialogFragment getMarkersDialog() {
		return markersDialog;
	}

	public GIControlFloating getMarkerPoint() {

		if (m_marker_point == null) {
			m_marker_point = new GIControlFloating(this);
			root.addView(m_marker_point);
			m_marker_point.setMap(getMap());
		}
		return m_marker_point;
	}

	public DialogFragment getEditablelayersDialog() {
		return editablelayersDialog;
	}


}
