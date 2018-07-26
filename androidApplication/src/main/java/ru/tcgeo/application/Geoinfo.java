package ru.tcgeo.application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.control.GIControlFloating;
import ru.tcgeo.application.control.GIGeometryPointControl;
import ru.tcgeo.application.control.GIPositionControl;
import ru.tcgeo.application.control.GIScaleControl;
import ru.tcgeo.application.control.GITouchControl;
import ru.tcgeo.application.data.GIEditingStatus;
import ru.tcgeo.application.data.GITrackingStatus;
import ru.tcgeo.application.data.interactors.LoadProjectInteractor;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.gps.GICompassView;
import ru.tcgeo.application.gilib.gps.GIDirectionToPOIArrow;
import ru.tcgeo.application.gilib.gps.GIGPSButtonView;
import ru.tcgeo.application.gilib.gps.GIGPSLocationListener;
import ru.tcgeo.application.gilib.gps.GILocatorFragment;
import ru.tcgeo.application.gilib.gps.GISensors;
import ru.tcgeo.application.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.gilib.layer.GILayer;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.Marker;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.utils.ScreenUtils;
import ru.tcgeo.application.view.FloatingActionButtonsCallback;
import ru.tcgeo.application.view.MapView;
import ru.tcgeo.application.views.callback.EditableLayerCallback;
import ru.tcgeo.application.views.callback.LayerCallback;
import ru.tcgeo.application.views.callback.LocationCallback;
import ru.tcgeo.application.views.callback.MarkerCallback;
import ru.tcgeo.application.views.callback.ProjectsCallback;
import ru.tcgeo.application.views.dialog.EditAttributesDialog;
import ru.tcgeo.application.views.dialog.ReEditableLayersDialog;
import ru.tcgeo.application.views.dialog.ReMarkersDialog;
import ru.tcgeo.application.views.dialog.ReProjectDialog;
import ru.tcgeo.application.views.dialog.ReSettingsDialog;
import ru.tcgeo.application.wkt.GI_WktGeometry;
import ru.tcgeo.application.wkt.GI_WktPoint;


public class Geoinfo extends FragmentActivity
        implements MapView,
        FloatingActionButtonsCallback, LocationCallback {
    final static public String locator_view_tag = "LOCATOR_TAG";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public AppCompatCheckBox btnEditCreate;
    public AppCompatCheckBox btnEditGeometry;
    public AppCompatCheckBox btnEditAttributes;
    public AppCompatCheckBox btnEditDelete;
    public SubActionButton fbEditCreate;
    public SubActionButton fbEditGeometry;
    public SubActionButton fbEditAttributes;
    public SubActionButton fbEditDelete;
    public FloatingActionButton fbEditButton;
    public boolean toAutoFollow = false;
    public boolean toShowTargetDirection = false;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.map)
    GIMap map;
    @BindView(R.id.touchcontrol)
    GITouchControl touchControl;
    @BindView(R.id.scale_control_screen)
    GIScaleControl scaleControl;
    @BindView(R.id.pbProgress)
    View pbProgress;
    GIGPSButtonView fbGPS;
    ImageButton fbEdit;
    FloatingActionMenu editActionMenu;
    GIControlFloating m_marker_point;
    GIPositionControl positionControl;
    private GIEditingStatus m_Status = GIEditingStatus.STOPPED;
    private GITrackingStatus m_TrackingStatus = GITrackingStatus.STOP;
    private LocationManager locationManager;
    private GIGPSLocationListener m_location_listener;
    private boolean isPaused = false;

    public void MarkersDialogClicked() {
        View v = root.findViewById(R.id.direction_to_point_arrow);
        Marker current = null;
        if (v != null) {
            current = (Marker) v.getTag();
        }
        List<Marker> markers = map.getMarkers();
        if (markers != null && current != null) {
            for (Marker marker : markers) {
                if (marker.lat == current.lat && marker.lon == current.lon) {
                    marker.selected = true;
                }
            }
        }

        ReMarkersDialog dialog = new ReMarkersDialog.Builder(this)
                .callback(new MarkerCallback() {
                    @Override
                    public void onGoToClick(Marker marker) {
                        GILonLat new_center = new GILonLat(marker.lon,
                                marker.lat);
                        GIControlFloating m_marker_point = getMarkerPoint();
                        m_marker_point.setLonLat(new_center);
                        if (marker.diag != 0) {
                            map.SetCenter(new_center, marker.diag);
                        } else {
                            map.SetCenter(GIProjection.ReprojectLonLat(new_center,
                                    GIProjection.WGS84(), map.Projection()));
                        }
                    }

                    @Override
                    public void onShowDirectiponClick(Marker marker, boolean show) {
                        View v = root.findViewById(R.id.direction_to_point_arrow);
                        if (v != null) {
                            root.removeView(v);
                        }
                        GILocatorFragment locator = (GILocatorFragment) getFragmentManager().findFragmentByTag(locator_view_tag);
                        if (locator != null && locator.isAdded()) {
                            getFragmentManager().beginTransaction().remove(locator).commit();
                        }
                        if (show) {
                            GILonLat new_center = new GILonLat(
                                    marker.lon, marker.lat);
                            GI_WktPoint poi = new GI_WktPoint(new_center);
                            GIDirectionToPOIArrow arrow = new GIDirectionToPOIArrow(root, map, marker);
                            getFragmentManager().beginTransaction().add(R.id.root, new GILocatorFragment(poi), locator_view_tag).commit();
                        }
                    }
                })
                .data(markers)
                .build();
        dialog.show();
    }

    public void EditableLayersDialogClicked() {

        ArrayList<GIEditableLayer> editableLayers = new ArrayList<>();
        if (map != null && map.m_layers != null && map.m_layers.m_list != null) {
            for (GILayer l : map.m_layers.m_list) {
                //todo
                if (l instanceof GIEditableLayer && l.m_layer_properties.editable != null) {
                    editableLayers.add((GIEditableLayer) l);
                }
            }
        }

        new ReEditableLayersDialog.Builder(this)
                .callback(new EditableLayerCallback() {
                    @Override
                    public void onStartEdit(GIEditableLayer layer) {
                        map.StartEditing(layer);
                    }

                    @Override
                    public void onStopEdit() {
                        map.StopEditing();
                        editActionMenu.close(true);

                    }
                })
                .data(editableLayers)
                .build()
                .show();
    }

    public void ProjectSelectorDialogClicked() {
        new ReProjectDialog.Builder(this)
                .callback(new ProjectsCallback() {
                    @Override
                    public void onClick(GIProjectProperties project) {
                        if (!project.m_path.equals(getMap().ps.m_path)) {
                            onSaveProject();
                            getMap().Clear();
                            LoadProject(project.m_path);
                            getMap().UpdateMap();
                            App.Instance().getPreference().setLastProjectPath(project.m_path);
                        }
                    }

                    @Override
                    public void onNewProject() {
                        onSaveProject();
                        getMap().Clear();

                        map.ps = new GIProjectProperties(Geoinfo.this);
                        App.Instance().getPreference().setLastProjectPath(map.ps.m_path);
                        GIBounds temp = new GIBounds(GIProjection.WGS84(), 28, 65, 48, 46);
                        map.InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
                        SettingsDialogClicked();
                    }
                })
                .build()
                .show();
    }

    public void SettingsDialogClicked() {
        new ReSettingsDialog.Builder(this)
                .callback(new LayerCallback() {
                    @Override
                    public void onMarkersSourceCheckChanged(GILayer layer, boolean isChecked) {
                        map.setMarkersSource(layer, isChecked);
                    }

                    @Override
                    public void onImmediatelyChange() {
                        map.UpdateMap();
                    }

                    @Override
                    public GILayer onAddLayer(File file) {
                        return map.addLayer(file);
                    }

                    @Override
                    public void onRemoveLayer(GILayer layer) {
                        map.m_layers.m_list.remove(layer);
                        map.ps.m_Group.m_Entries.remove(layer.m_layer_properties);
                        map.UpdateMap();
                    }

                    @Override
                    public void onMoveLayer(GILayer from, GILayer to) {
                        Collections.swap(map.m_layers.m_list, map.m_layers.m_list.indexOf(from), map.m_layers.m_list.indexOf(to));
                        Collections.swap(map.ps.m_Group.m_Entries, map.ps.m_Group.m_Entries.indexOf(from.m_layer_properties), map.ps.m_Group.m_Entries.indexOf(to.m_layer_properties));
                        map.UpdateMap();
                    }

                    @Override
                    public void onPOILayer(GIEditableLayer layer) {
                        for (GILayer l : map.m_layers.m_list) {
                            if (l instanceof GIEditableLayer) {
                                GILayer.Builder builder = new GILayer.Builder(l);
                                builder.active(false);
                                builder.build();
                            }
                        }
                        GILayer.Builder builder = new GILayer.Builder(layer);
                        builder.active(false);
                        builder.build();
                        layer.m_layer_properties.editable.active = true;
                        map.setPoiLayer(layer);
                    }


                })
                .data(map.getLayers())
                .project(map.ps)
                .build().show();
    }

    public void showEditAttributesFragment(GI_WktGeometry geometry) {
        EditAttributesDialog dialog = new EditAttributesDialog(this, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                btnEditCreate.setEnabled(true);
                btnEditAttributes.setEnabled(true);
                btnEditGeometry.setEnabled(true);
                btnEditDelete.setEnabled(true);
                map.UpdateMap();
            }
        }, geometry.m_attributes);
        dialog.show();
    }

    public void LoadProject(String path) {

        pbProgress.setVisibility(View.VISIBLE);
        LoadProjectInteractor interactor = new LoadProjectInteractor(this);
        interactor.setView(this);
        interactor.loadProject(path);
    }

    @Override
    public void onProject(GIProjectProperties ps) {
        map.ps = ps;
        map.ClearEditableLayers();
        GIBounds temp = new GIBounds(ps.m_projection, ps.m_left,
                ps.m_top, ps.m_right, ps.m_bottom);

        //??

        map.InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
        touchControl.setMap(map);
        App.Instance().setMap(map);
        map.ps = ps;
    }

    @Override
    public void onLayer(LoadProjectInteractor.Layer layer) {
        map.AddLayer(layer.giLayer, layer.giRange, layer.enabled);
        if (layer.giLayer instanceof GIEditableLayer) {
            GIEditableLayer l = (GIEditableLayer) layer.giLayer;
            if (l != null && l.m_layer_properties.editable != null
                    && l.m_layer_properties.editable.enumType != GILayer.EditableType.TRACK) {
                l.setType(l.m_layer_properties.editable.enumType);
                if (l.m_Type == GILayer.EditableType.TRACK && l.m_layer_properties.editable.active) {
                    map.setTrackLayer(l);
                } else if (l.m_Type == GILayer.EditableType.POI && l.m_layer_properties.editable.active) {
                    map.setPoiLayer(l);
                }
                map.AddEditableLayer(l);
            }
        }
    }

    @Override
    public void onComplited() {
        pbProgress.setVisibility(View.INVISIBLE);
        map.UpdateMap();
    }

    @Override
    public void onError() {

        map.ps = new GIProjectProperties(this);
        App.Instance().getPreference().setLastProjectPath(map.ps.m_path);
        touchControl.setMap(map);
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

        m_location_listener = new GIGPSLocationListener(this, this);
//        GIEditLayersKeeper.Instance().m_location_manager = m_location_listener.locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        m_Status = GIEditingStatus.STOPPED;
        m_TrackingStatus = GITrackingStatus.STOP;

        setupButtons();

        String path = App.Instance().getPreference().getLastProjectPath();
        LoadProject(path);

        // Setup pixel size to let scale work properly
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double screenPixels = Math.hypot(dm.widthPixels, dm.heightPixels);
        double screenInches = Math.hypot(dm.widthPixels / dm.xdpi,
                dm.heightPixels / dm.ydpi);
        GIMap.inches_per_pixel = screenInches / screenPixels;

        scaleControl.setMap(map);
        positionControl = new GIPositionControl(this, map);

    }


    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        GISensors.Instance(this).run(true);

    }


    @Override
    protected void onPause() {
        super.onPause();
        getMap().StopEditing();
        isPaused = true;
        GISensors.Instance(this).run(false);
        onSaveProject();
    }

    public void onSaveProject() {
        map.Synhronize();
        String SaveAsPath = App.Instance().getPreference().getNewProjectName();
        if (map != null && map.ps != null && map.ps.m_path != null && !map.ps.m_path.isEmpty()) {
            SaveAsPath = map.ps.m_path;
        }
        map.ps.SavePro(SaveAsPath);
    }

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

    public void setupButtons() {

        final FloatingActionButtonsCallback callback = this;
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


//        fbGPS.SetGPSEnabledStatus(m_location_listener.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));

        //-------------------------------------------------------------------
        // GPS AUTO_FOLL0W
        //--------------------------------------------------------------------
        final CheckBox m_btnAutoFollow = new CheckBox(this);
        m_btnAutoFollow.setButtonDrawable(R.drawable.auto_follow_status_);
        SubActionButton fbAutoFollow = itemBuilder.setContentView(m_btnAutoFollow).build();
        m_btnAutoFollow.setChecked(toAutoFollow);
        m_btnAutoFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAutoFollow = m_btnAutoFollow.isChecked();
                if (m_btnAutoFollow.isChecked()) {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        GILonLat go_to = GILonLat.fromLocation(location);
                        GILonLat go_to_map = GIProjection.ReprojectLonLat(go_to, GIProjection.WGS84(), GIProjection.WorldMercator());
                        map.SetCenter(go_to_map);
                    }
                }
//                GIEditLayersKeeper.Instance().GetPositionControl();
            }
        });

        //--------------------------------------------------------------------
        // GPS TRACK_CONTROL
        //--------------------------------------------------------------------
        final CheckBox m_btnTrackControl = new CheckBox(this);
        m_btnTrackControl.setTextSize(0);
        m_btnTrackControl.setButtonDrawable(R.drawable.stop_start_track_button);
        SubActionButton fbTrackControl = itemBuilder.setContentView(m_btnTrackControl).build();
        m_btnTrackControl.setChecked(m_TrackingStatus == GITrackingStatus.WRITE);
        m_btnTrackControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_TrackingStatus == GITrackingStatus.STOP) {
                    if (!map.CreateTrack()) {
                        m_TrackingStatus = GITrackingStatus.STOP;
                        m_btnTrackControl.setChecked(false);
                    }
                } else {
                    m_TrackingStatus = GITrackingStatus.STOP;
                    map.StopTrack();
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
                if (map.getCurrentTrackControl() != null) {
                    map.getCurrentTrackControl().Show(m_btnShowTrack.isChecked());
                    getMap().UpdateMap();
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
                if (getState() != GIEditingStatus.EDITING_POI && getState() != GIEditingStatus.EDITING_GEOMETRY) {
                    map.CreatePOI();
                } else {
                    map.StopEditing();
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
                callback.ProjectSelectorDialogClicked();
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
                callback.SettingsDialogClicked();

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
                callback.EditableLayersDialogClicked();
            }
        });
        //--------------------------------------------------------------------
        // COMPASS_MARKERS
        //--------------------------------------------------------------------
        final ImageButton btnMarkers = new ImageButton(this);
        btnMarkers.setImageResource(R.drawable.poi);
        btnMarkers.setBackgroundDrawable(null);
        SubActionButton fbMarkers = itemBuilder.setContentView(btnMarkers).build();
        btnMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.MarkersDialogClicked();
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
        btnEditCreate = new AppCompatCheckBox(this);
        btnEditCreate.setTextSize(0);
//		Drawable d = AppCompatDrawableManager.get().getDrawable(this, R.drawable.edit_create_bg);
        btnEditCreate.setButtonDrawable(R.drawable.edit_create_bg);
        btnEditCreate.setBackgroundDrawable(null);
        fbEditCreate = itemBuilder.setContentView(btnEditCreate).build();


        btnEditGeometry = new AppCompatCheckBox(this);
        btnEditGeometry.setTextSize(0);
        btnEditGeometry.setButtonDrawable(R.drawable.edit_geometry_bg);
        btnEditGeometry.setBackgroundDrawable(null);
        fbEditGeometry = itemBuilder.setContentView(btnEditGeometry).build();


        btnEditAttributes = new AppCompatCheckBox(this);
        btnEditAttributes.setTextSize(0);
        btnEditAttributes.setButtonDrawable(R.drawable.edit_attributes_bg);
        btnEditAttributes.setBackgroundDrawable(null);
        fbEditAttributes = itemBuilder.setContentView(btnEditAttributes).build();

        btnEditDelete = new AppCompatCheckBox(this);
        btnEditDelete.setTextSize(0);
        btnEditDelete.setButtonDrawable(R.drawable.edit_delete_bg);
        btnEditDelete.setBackgroundDrawable(null);
        fbEditDelete = itemBuilder.setContentView(btnEditDelete).build();


        btnEditCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((getState() != GIEditingStatus.WAITING_FOR_OBJECT_NEWLOCATION) && (btnEditCreate.isChecked())) {
                    if (!map.CreateNewObject()) {
                        return;
                    }
                    setState(GIEditingStatus.WAITING_FOR_OBJECT_NEWLOCATION);
                    fbEditAttributes.setEnabled(false);
                    fbEditGeometry.setEnabled(false);
                    fbEditDelete.setEnabled(false);
                    btnEditAttributes.setChecked(false);
                    btnEditGeometry.setChecked(false);
                    btnEditDelete.setChecked(false);
                    map.UpdateMap();
                } else {
                    setState(GIEditingStatus.RUNNING);
                    map.FillAttributes();
                    fbEditCreate.setEnabled(false);
                }
            }
        });
        btnEditGeometry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map.getCurrentLayer() == map.getTrackLayer()) {
                    return;
                }

                if ((getState() == GIEditingStatus.EDITING_GEOMETRY) || (getState() == GIEditingStatus.WAITING_FOR_SELECT_GEOMETRY_TO_EDITING) || (getState() == GIEditingStatus.WAITING_FOR_NEW_POINT_LOCATION)) {
                    setState(GIEditingStatus.RUNNING);
                    map.StopEditingGeometry();
                    fbEditCreate.setEnabled(true);
                    fbEditAttributes.setEnabled(true);
                    fbEditDelete.setEnabled(true);
                    btnEditCreate.setChecked(false);
                    btnEditAttributes.setChecked(false);
                    btnEditDelete.setChecked(false);
                } else {
                    setState(GIEditingStatus.WAITING_FOR_SELECT_GEOMETRY_TO_EDITING);
                    fbEditCreate.setEnabled(false);
                    fbEditAttributes.setEnabled(false);
                    fbEditDelete.setEnabled(false);
                }
            }
        });
        btnEditAttributes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getState() != GIEditingStatus.WAITIN_FOR_SELECT_OBJECT) {
                    setState(GIEditingStatus.WAITIN_FOR_SELECT_OBJECT);
                    fbEditCreate.setEnabled(false);
                    fbEditGeometry.setEnabled(false);
                    fbEditDelete.setEnabled(false);
                    btnEditCreate.setChecked(false);
                    btnEditGeometry.setChecked(false);
                    btnEditDelete.setChecked(false);
                } else {
                    setState(GIEditingStatus.RUNNING);
                    fbEditCreate.setEnabled(true);
                    fbEditGeometry.setEnabled(true);
                    fbEditDelete.setEnabled(true);
                }
            }
        });

        btnEditDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(GIEditingStatus.WAITING_FOR_TO_DELETE);
            }
        });

        fbEdit = new ImageButton(this);
        FloatingActionButton.LayoutParams edit_menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(96), ScreenUtils.dpToPx(96));
        edit_menu_params.setMargins(ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2));
        fbEdit.setImageResource(R.drawable.edit);
        fbEdit.setBackgroundDrawable(null);
        fbEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map.getCurrentLayer() == map.getTrackLayer()) {
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

        editActionMenu = new FloatingActionMenu.Builder(this)

                .addSubActionView(fbEditCreate)
                .addSubActionView(fbEditGeometry)
                .addSubActionView(fbEditAttributes)
                .addSubActionView(fbEditDelete)
                .attachTo(fbEditButton)
                .setRadius(ScreenUtils.dpToPx(144))
                .setStartAngle(180)
                .setEndAngle(270)
                .build();


    }

    public GIEditingStatus getState() {
        return m_Status;
    }

    public void setState(GIEditingStatus status) {
        m_Status = status;
        if (IsRunning()) {
            touchControl.SetMeasureState(false, false);
        }

    }

    public boolean IsRunning() {
        return !(m_Status == GIEditingStatus.STOPPED);
    }

    public GITrackingStatus getTrackingStatus() {
        return m_TrackingStatus;
    }

    public void setTrackingStatus(GITrackingStatus status) {
        m_TrackingStatus = status;
    }


    public GIMap getMap() {
        return map;
    }

    public GIControlFloating getMarkerPoint() {

        if (m_marker_point == null) {
            m_marker_point = new GIControlFloating(this);
            root.addView(m_marker_point);
            m_marker_point.setMap(getMap());
        }
        return m_marker_point;
    }

    @Override
    public void onLocationChanged(Location location) {
        GILonLat deg = null;
        float accurancy = 100;

        if (location == null) {
            deg = GIProjection.ReprojectLonLat(getMap().Center(), getMap().Projection(), GIProjection.WGS84());
        } else {
            deg = new GILonLat(location.getLongitude(), location.getLatitude());
            accurancy = location.getAccuracy();
        }

        if (!isPaused && toAutoFollow) {
            GILonLat mercator = GIProjection.ReprojectLonLat(deg, GIProjection.WGS84(), getMap().Projection());
            Point new_center = getMap().MapToScreen(mercator);
            double distance = Math.hypot(new_center.y - getMap().Height() / 2, new_center.x - getMap().Width() / 2);
            //TODO uncomment
            if (distance > 20) {
                getMap().SetCenter(mercator);
            }
        }

        if (getTrackingStatus() == GITrackingStatus.WRITE) {
            if (getMap().getTrackLayer() != null) {
                GI_WktPoint point = new GI_WktPoint();
                point.Set(deg);
                getMap().AddPointToTrack(deg, accurancy);
            }
        }

    }

    public void onSelectPoint(GIGeometryPointControl control) {
        //m_CurrentTarget = control.m_WKTPoint;
        //GIDirectionToPOIArrow arrow = new GIDirectionToPOIArrow(m_CurrentTarget);

        if (getMap().getCurrentEditingControl() == null) {
            return;
        }

        boolean checked_yet = control.getChecked();
        if (getMap().getCurrentEditingControl() != null) {
            for (GIGeometryPointControl other : getMap().getCurrentEditingControl().m_points) {
                other.setChecked(false);
            }
        }
        control.setChecked(checked_yet);
        if (checked_yet) {
            setState(GIEditingStatus.WAITING_FOR_NEW_POINT_LOCATION);
        } else {
            setState(GIEditingStatus.EDITING_GEOMETRY);
        }
    }
}
