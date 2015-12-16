package ru.tcgeo.application.home_screen;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.io.File;

import ru.tcgeo.application.App;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.IFolderItemListener;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIGroupLayer;
import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.GISQLLayer;
import ru.tcgeo.application.gilib.GITuple;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.application.gilib.parser.GIRange;
import ru.tcgeo.application.gilib.parser.GISQLDB;
import ru.tcgeo.application.gilib.parser.GISource;
import ru.tcgeo.application.local_project_management.SettingsFragment;
import ru.tcgeo.application.utils.ProjectChangedEvent;
import ru.tcgeo.application.views.OpenFileDialog;

/**
 * Created by a_belov on 23.07.15.
 */
public class SettingsDialog extends DialogFragment implements IFolderItemListener {

    private GIMap mMap;
    ListView mProjectsList;
    ListView mLayersList;
    FrameLayout mProperties;
    LinearLayout.LayoutParams projectsParams;
    LinearLayout.LayoutParams propertiesParams;
    ProjectsAdapter projects_adapter;
    LayersAdapter layersAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.getInstance().getEventBus().register(this);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mMap = ((Geoinfo)getActivity()).getMap();
        View v = inflater.inflate(R.layout.project_settings_dialog, container, false);
        mProjectsList = (ListView)  v.findViewById(R.id.projects_list);
        mLayersList = (ListView)  v.findViewById(R.id.layers_list);
        mProperties = (FrameLayout)v.findViewById(R.id.content);

        propertiesParams = (LinearLayout.LayoutParams) mProperties.getLayoutParams();
        projectsParams = (LinearLayout.LayoutParams) mProjectsList.getLayoutParams();
        propertiesParams.weight = 0;
        projects_adapter = new ProjectsAdapter((Geoinfo)getActivity(),
                R.layout.project_selector_list_item,
                R.id.project_list_item_path);
        AddProjects(projects_adapter);
        mProjectsList.setAdapter(projects_adapter);
        mProjectsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                propertiesParams.weight = 0;
                projectsParams.weight = 2;
                mProjectsList.requestLayout();
                mProperties.requestLayout();
            }
        });

        layersAdapter = new LayersAdapter((Geoinfo)getActivity(),
                R.layout.layers_list_item, R.id.layers_list_item_text);

        View header = inflater.inflate(
                R.layout.add_layer_header_layout, null);
        header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                OpenFileDialog dlg = new OpenFileDialog();
                dlg.setIFolderItemListener(SettingsDialog.this);
                dlg.show(getChildFragmentManager(), "tag");
            }
        });
        mLayersList.addHeaderView(header);

        addLayers((GIGroupLayer) mMap.m_layers, layersAdapter);
        mLayersList.setAdapter(layersAdapter);
        mLayersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                projectsParams.weight = 1;
                propertiesParams.weight = 2;
                mProjectsList.requestLayout();
                mProperties.requestLayout();

                final LayersAdapterItem item = layersAdapter.getItem(position - mLayersList.getHeaderViewsCount());

                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.content, new SettingsFragment(mMap, item)).commit();
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//        ft.replace(R.id.content, new SettingsFragment()).commit();
    }
//
    @Override public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
//        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);

        int dialogWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int dialogHeight = (int)(getActivity().getWindowManager().getDefaultDisplay().getHeight()*0.9f);

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    public void addLayers(GIGroupLayer layer,
                          ArrayAdapter<LayersAdapterItem> adapter) {
        for (GITuple tuple : layer.m_list) {
            if (GILayer.GILayerType.LAYER_GROUP == tuple.layer.type_)
                addLayers((GIGroupLayer) tuple.layer, adapter);
            else {
                adapter.add(new LayersAdapterItem(tuple));
            }
        }
    }

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

    @Override
    public void OnCannotFileRead(File file) {
        Toast.makeText(getActivity(), "can't be read!",
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void OnFileClicked(File file) {

//        App.getInstance().getEventBus().post(new ProjectChangedEvent());
        addLayer(file);
        refresh(new ProjectChangedEvent());
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
            properties_layer.m_type = GILayer.GILayerType.SQL_LAYER;
            properties_layer.m_strType = "SQL_LAYER";
            GILayer layer;
            //TODO
            layer = GILayer.CreateLayer(properties_layer.m_source.GetAbsolutePath(), GILayer.GILayerType.SQL_LAYER);
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

            mMap.ps.m_Group.addEntry(properties_layer);
            layer.setName(file.getName());
            layer.m_layer_properties = properties_layer;
            mMap.InsertLayerAt(layer, 0);
        }
        //else if(extention.equalsIgnoreCase("xml"))
        mMap.UpdateMap();
    }
    @Subscribe  public void refresh(ProjectChangedEvent e){
        projects_adapter.clear();
//         projects_adapter = new ProjectsAdapter((Geoinfo)getActivity(),
//                R.layout.project_selector_list_item,
//                R.id.project_list_item_path);
        AddProjects(projects_adapter);
        layersAdapter.clear();
//        adapter = new LayersAdapter((Geoinfo)getActivity(),
//                R.layout.layers_list_item, R.id.layers_list_item_text);
        addLayers((GIGroupLayer) mMap.m_layers, layersAdapter);
        mMap.UpdateMap();
    }
}
