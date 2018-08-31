package ru.tcgeo.application;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.tcgeo.application.control.GIControlFloating;
import ru.tcgeo.application.control.GIGeometryPointControl;
import ru.tcgeo.application.control.GIPositionControl;
import ru.tcgeo.application.control.GIScaleControl;
import ru.tcgeo.application.control.GITouchControl;
import ru.tcgeo.application.data.GIEditingStatus;
import ru.tcgeo.application.data.GIGPSLocationListener;
import ru.tcgeo.application.data.interactors.LoadProjectInteractor;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.gps.GICompassView;
import ru.tcgeo.application.gilib.gps.GIDirectionToPOIArrow;
import ru.tcgeo.application.gilib.gps.GIGPSButtonView;
import ru.tcgeo.application.gilib.gps.GILocatorFragment;
import ru.tcgeo.application.gilib.gps.GISensors;
import ru.tcgeo.application.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.gilib.layer.GILayer;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.LonLatEvent;
import ru.tcgeo.application.gilib.models.Marker;
import ru.tcgeo.application.gilib.parser.GIEditable;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.utils.PermissionManager;
import ru.tcgeo.application.utils.ScreenUtils;
import ru.tcgeo.application.view.FloatingActionButtonsCallback;
import ru.tcgeo.application.view.MapView;
import ru.tcgeo.application.views.callback.EditableLayerCallback;
import ru.tcgeo.application.views.callback.LayerCallback;
import ru.tcgeo.application.views.callback.MarkerCallback;
import ru.tcgeo.application.views.callback.ProjectsCallback;
import ru.tcgeo.application.views.dialog.AddPointsDialog;
import ru.tcgeo.application.views.dialog.EditAttributesDialog;
import ru.tcgeo.application.views.dialog.ReEditableLayersDialog;
import ru.tcgeo.application.views.dialog.ReMarkersDialog;
import ru.tcgeo.application.views.dialog.ReProjectDialog;
import ru.tcgeo.application.views.dialog.ReSettingsDialog;
import ru.tcgeo.application.wkt.GI_WktGeometry;
import ru.tcgeo.application.wkt.GI_WktPoint;


public class Geoinfo extends Activity
        implements MapView,
        FloatingActionButtonsCallback {

    final static public String locator_view_tag = "LOCATOR_TAG";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public AppCompatCheckBox btnEditCreate;
    public AppCompatCheckBox btnEditAddPoints;
    public AppCompatCheckBox btnEditGeometry;
    public AppCompatCheckBox btnEditAttributes;
    public AppCompatCheckBox btnEditDelete;
    public SubActionButton fbEditCreate;
    public SubActionButton fbEditAddPoints;
    public SubActionButton fbEditGeometry;
    public SubActionButton fbEditAttributes;
    public SubActionButton fbEditDelete;
    public FloatingActionButton fbEditButton;

    protected CompositeDisposable subscription = new CompositeDisposable();

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
    private FloatingActionMenu editActionMenu;
    GIControlFloating m_marker_point;
    GIPositionControl positionControl;
    private GIEditingStatus m_Status = GIEditingStatus.STOPPED;
    private GIGPSLocationListener locationListener;
//    private boolean isPaused = false;

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
                    public GILayer onAddLayer(GILayer.EditableType type, String name) {
                        return map.addLayer(type, name);
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
                        if(layer.m_layer_properties.editable == null) {
                            layer.m_layer_properties.editable = new GIEditable();
                        }
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

    public void showAddPointsFragment() {
        AddPointsDialog dialog = new AddPointsDialog();
        dialog.show(getFragmentManager(), "add_points");
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

    public Observable<LonLatEvent> getPositionObservable() {
        if (locationListener != null) {
            return locationListener.getPositionObservable();
        } else {
            return Observable.empty();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        requestRuntimePermission(PermissionManager.PERMISSION_LOCATION_CODE, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION});
        requestRuntimePermission(PermissionManager.PERMISSION_READ_EXTERNAL_CODE, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});

        locationListener = new GIGPSLocationListener(this);
        positionControl = new GIPositionControl(this, map, locationListener);

        //writing track
        subscription.add(locationListener.getTrackObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LonLatEvent>() {
                    @Override
                    public void accept(LonLatEvent lonLatEvent) {
                        if (getMap().getTrackLayer() != null) {
                            GI_WktPoint point = new GI_WktPoint();
                            point.Set(lonLatEvent.lonlat);
                            getMap().AddPointToTrack(lonLatEvent.lonlat, lonLatEvent.accurancy);
                        }
                    }
                }));

        //autofollow
        subscription.add(
                locationListener.getFollowObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<LonLatEvent>() {
                            @Override
                            public void accept(LonLatEvent lonLatEvent) {
                                GILonLat mercator = GIProjection.ReprojectLonLat(lonLatEvent.lonlat, GIProjection.WGS84(), getMap().Projection());
                                Point new_center = getMap().MapToScreen(mercator);
                                double distance = Math.hypot(new_center.y - getMap().Height() / 2, new_center.x - getMap().Width() / 2);
                                if (distance > 20) {
                                    getMap().SetCenter(mercator);
                                }
                            }
                        }));


        m_Status = GIEditingStatus.STOPPED;
        locationListener.getTrackSubject().onNext(0);

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
        //todo
//        positionControl = new GIPositionControl(this, map, locationListener);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationListener.getRunningSubject().onNext(LonLatEvent.FLAG_RUNNING);
        locationListener.getFollowSubject().onNext(0);
        GISensors.Instance(this).run(true);

    }


    @Override
    protected void onPause() {
        super.onPause();
        getMap().StopEditing();
        locationListener.getRunningSubject().onNext(0);
        locationListener.getFollowSubject().onNext(0);
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
        FloatingActionButton.LayoutParams gps_menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(80), ScreenUtils.dpToPx(80));
        gps_menu_params.setMargins(ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2));

        FloatingActionButton gps_action_button = new FloatingActionButton.Builder(this)
                .setContentView(fbGPS)
                .setBackgroundDrawable(R.drawable.menu_circle_button_background)
                .setPosition(FloatingActionButton.POSITION_TOP_LEFT)
                .setLayoutParams(gps_menu_params)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        FloatingActionButton.LayoutParams action_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(64), ScreenUtils.dpToPx(64));
        action_params.gravity = Gravity.CENTER_HORIZONTAL;
        itemBuilder.setLayoutParams(action_params);


        //-------------------------------------------------------------------
        // GPS AUTO_FOLL0W
        //--------------------------------------------------------------------
        final CheckBox m_btnAutoFollow = new CheckBox(this);
        m_btnAutoFollow.setButtonDrawable(R.drawable.auto_follow_status_);
        SubActionButton fbAutoFollow = itemBuilder.setContentView(m_btnAutoFollow).setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_circle_button_background)).build();
        locationListener.getFollowSubject().onNext(0);
        m_btnAutoFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationListener.getFollowSubject().onNext(m_btnAutoFollow.isChecked() ? LonLatEvent.FLAG_FOLLOW : 0);
            }
        });
        subscription.add(locationListener.getFollowSubject().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                m_btnAutoFollow.setChecked((integer & LonLatEvent.FLAG_FOLLOW) != 0 );
            }
        }));

        //--------------------------------------------------------------------
        // GPS TRACK_CONTROL
        //--------------------------------------------------------------------
        final CheckBox m_btnTrackControl = new CheckBox(this);
        m_btnTrackControl.setTextSize(0);
        m_btnTrackControl.setButtonDrawable(R.drawable.stop_start_track_button);
        SubActionButton fbTrackControl = itemBuilder.setContentView(m_btnTrackControl).build();

        subscription.add(locationListener.getTrackSubject().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                m_btnTrackControl.setChecked((integer & LonLatEvent.FLAG_TRACK) != 0 );
            }
        }));

        m_btnTrackControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    map.CreateTrack();
                } else {
                    map.StopTrack();
                }
            }
        });
        m_btnTrackControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = m_btnTrackControl.isChecked();
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

        final CheckBox m_btnPoiControl = new CheckBox(this);
        m_btnShowTrack.setTextSize(0);
        m_btnPoiControl.setButtonDrawable(R.drawable.poi_status);
//        m_btnPoiControl.setBackgroundDrawable(null);
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
        FloatingActionButton.LayoutParams compass_menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(80), ScreenUtils.dpToPx(80));
        compass_menu_params.setMargins(ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2));

        FloatingActionButton compass_action_button = new FloatingActionButton.Builder(this)
                .setContentView(fbCompass)
//                .setBackgroundDrawable(null)
                .setBackgroundDrawable(R.drawable.menu_circle_button_background)
                .setPosition(FloatingActionButton.POSITION_TOP_RIGHT)
                .setLayoutParams(compass_menu_params)
                .build();
        //--------------------------------------------------------------------
        // COMPASS_OPEN_BUTTON
        //--------------------------------------------------------------------
        final ImageButton btnProjectSelectorButton = new ImageButton(this);
        btnProjectSelectorButton.setImageResource(R.drawable.ic_folder_open);
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
        btnLayers.setImageResource(R.drawable.ic_settings);
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
        btnEditLayers.setImageResource(R.drawable.edit_geometry);
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
        btnMarkers.setImageResource(R.drawable.ic_place);
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

        btnEditAddPoints = new AppCompatCheckBox(this);
        btnEditAddPoints.setTextSize(0);
        btnEditAddPoints.setButtonDrawable(R.drawable.edit_add_points_bg);
        btnEditAddPoints.setBackgroundDrawable(null);
        fbEditAddPoints = itemBuilder.setContentView(btnEditAddPoints).build();


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

        btnEditAddPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEditAddPoints.setChecked(false);
                fbEditAttributes.setEnabled(false);
                fbEditGeometry.setEnabled(false);
                fbEditDelete.setEnabled(false);
                btnEditAttributes.setChecked(false);
                btnEditGeometry.setChecked(false);
                btnEditDelete.setChecked(false);

//                    map.UpdateMap();
                showAddPointsFragment();
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

//                    getMap().removeEditingControls();
//                    getMap().disableEditingControls();

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
//                    getMap().disableEditingControls();
//                    getMap().removeEditingControls();

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
        FloatingActionButton.LayoutParams edit_menu_params = new FloatingActionButton.LayoutParams(ScreenUtils.dpToPx(80), ScreenUtils.dpToPx(80));
        edit_menu_params.setMargins(ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2), ScreenUtils.dpToPx(2));
        fbEdit.setImageResource(R.drawable.edit_geometry);
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
//                .setBackgroundDrawable(null)
                .setBackgroundDrawable(R.drawable.menu_circle_button_background)
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
                .addSubActionView(fbEditAddPoints)
                .addSubActionView(fbEditGeometry)
                .addSubActionView(fbEditAttributes)
                .addSubActionView(fbEditDelete)
                .attachTo(fbEditButton)
                .setRadius(ScreenUtils.dpToPx(196))
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


    public void setTrackingStatus(int status) {
        locationListener.getTrackSubject().onNext(status);
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


    public void onSelectPoint(GIGeometryPointControl control) {

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

    public BehaviorSubject<Boolean> getEnabledBehaviorSubject() {
        return locationListener.getEnabledBehaviorSubject();
    }

    public BehaviorSubject<GpsStatus> getStatusBehaviorSubject() {
        return locationListener.getStatusBehaviorSubject();
    }

    public BehaviorSubject<String> getNmeaBehaviorSubject() {
        return locationListener.getNmeaBehaviorSubject();
    }

    public BehaviorSubject<Location> getLocationBehaviorSubject() {
        return locationListener.getLocationBehaviorSubject();
    }

    public Subject<Location> getLocation() {
        return locationListener.getLocation();
    }

    public PublishSubject<Integer> getRunnigSubject() {
        return locationListener.getRunningSubject();
    }

    public void requestRuntimePermission(int code, String[] permission){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onRequestPermissionsResult(code, permission, PermissionManager.grantedResults(permission));
        } else if(!PermissionManager.checkRuntimePermissions( this, permission)) {
            ActivityCompat.requestPermissions(this, permission, code);
        } else {
            onRequestPermissionsResult(code, permission, PermissionManager.grantedResults(permission));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);


        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

//        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            // The document selected by the user won't be returned in the intent.
//            // Instead, a URI to that document will be contained in the return intent
//            // provided to this method as a parameter.
//            // Pull that URI using resultData.getData().
//            Uri uri = null;
//            if (resultData != null) {
//                uri = resultData.getData();
//                Log.i(TAG, "Uri: " + uri.toString());
//                showImage(uri);
//            }
//        }
    }

}
