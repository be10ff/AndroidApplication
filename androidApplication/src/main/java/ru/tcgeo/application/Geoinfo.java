package ru.tcgeo.application;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.tcgeo.application.data.interactors.LoadProjectInteractor;
import ru.tcgeo.application.gilib.GIControlFloating;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.GIEditableLayer;
import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.gps.GIDirectionToPOIArrow;
import ru.tcgeo.application.gilib.gps.GILocatorFragment;
import ru.tcgeo.application.gilib.gps.GISensors;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.Marker;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.view.FloatingActionButtonsCallback;
import ru.tcgeo.application.view.MapView;
import ru.tcgeo.application.views.GIScaleControl;
import ru.tcgeo.application.views.callback.EditableLayerCallback;
import ru.tcgeo.application.views.callback.LayerCallback;
import ru.tcgeo.application.views.callback.MarkerCallback;
import ru.tcgeo.application.views.callback.ProjectsCallback;
import ru.tcgeo.application.views.dialog.ReEditableLayersDialog;
import ru.tcgeo.application.views.dialog.ReMarkersDialog;
import ru.tcgeo.application.views.dialog.ReProjectDialog;
import ru.tcgeo.application.views.dialog.ReSettingsDialog;
import ru.tcgeo.application.wkt.GI_WktPoint;


public class Geoinfo extends FragmentActivity implements MapView, FloatingActionButtonsCallback {
    final static public String locator_view_tag = "LOCATOR_TAG";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


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

    GIControlFloating m_marker_point;

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
                if (l instanceof GIEditableLayer) {
                    editableLayers.add((GIEditableLayer) l);
                }
            }
        }

        new ReEditableLayersDialog.Builder(this)
                .callback(new EditableLayerCallback() {
                    @Override
                    public void onStartEdit(GIEditableLayer layer) {
                        GIEditLayersKeeper.Instance().StartEditing(layer);
                    }

                    @Override
                    public void onStopEdit() {
                        GIEditLayersKeeper.Instance().StopEditing();
                        if (touchControl.editActionMenu != null) {
                            touchControl.editActionMenu.close(true);
                        }

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
                            App.getInstance().getPreference().setLastProjectPath(project.m_path);
                        }
                    }

                    @Override
                    public void onNewProject() {
                        onSaveProject();
                        getMap().Clear();

                        map.ps = new GIProjectProperties(Geoinfo.this);
                        App.getInstance().getPreference().setLastProjectPath(map.ps.m_path);
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
                        GIEditLayersKeeper.Instance().m_POILayer = layer;
                    }


                })
                .data(map.getLayers())
                .project(map.ps)
                .build().show();
    }

    public void LoadProject(String path) {

        pbProgress.setVisibility(View.VISIBLE);
        LoadProjectInteractor interactor = new LoadProjectInteractor();
        interactor.setView(this);
        interactor.loadProject(path);
    }

    @Override
    public void onProject(GIProjectProperties ps) {
        map.ps = ps;
        GIEditLayersKeeper.Instance().ClearLayers();
        GIBounds temp = new GIBounds(ps.m_projection, ps.m_left,
                ps.m_top, ps.m_right, ps.m_bottom);

        map.InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
        touchControl.setMap(map);
        map.ps = ps;
    }

    @Override
    public void onLayer(LoadProjectInteractor.Layer layer) {
        map.AddLayer(layer.giLayer, layer.giRange, layer.enabled);
    }

    @Override
    public void onComplited() {
        pbProgress.setVisibility(View.INVISIBLE);
        map.UpdateMap();
    }

    @Override
    public void onError() {

        map.ps = new GIProjectProperties(this);
        App.getInstance().getPreference().setLastProjectPath(map.ps.m_path);
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

        touchControl.setupButtons(this);


        String path = App.getInstance().getPreference().getLastProjectPath();
        LoadProject(path);
        GIEditLayersKeeper.Instance().setFragmentManager(getFragmentManager());
        GIEditLayersKeeper.Instance().setTouchControl(touchControl);
        GIEditLayersKeeper.Instance().setMap(map);
        GIEditLayersKeeper.Instance().setActivity(this);

        // Setup pixel size to let scale work properly
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double screenPixels = Math.hypot(dm.widthPixels, dm.heightPixels);
        double screenInches = Math.hypot(dm.widthPixels / dm.xdpi,
                dm.heightPixels / dm.ydpi);
        GIMap.inches_per_pixel = screenInches / screenPixels;

        scaleControl.setMap(map);


    }


    @Override
    protected void onResume() {
        super.onResume();
        GIEditLayersKeeper.Instance().onResume();
        GISensors.Instance(this).run(true);

    }


    @Override
    protected void onPause() {
        super.onPause();
        GIEditLayersKeeper.Instance().onPause();
        GISensors.Instance(this).run(false);
        onSaveProject();
    }

    public void onSaveProject() {
        map.Synhronize();
        String SaveAsPath = App.getInstance().getPreference().getNewProjectName();
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

}
