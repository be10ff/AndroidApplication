package ru.tcgeo.application.home_screen;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;

import ru.tcgeo.application.App;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.home_screen.adapter.ProjectsAdapter;
import ru.tcgeo.application.home_screen.adapter.ProjectsAdapterItem;

/**
 * Created by a_belov on 23.07.15.
 */
@Deprecated
public class ProjectDialog extends DialogFragment{

    ListView mProjectsList;
    ProjectsAdapter projects_adapter;
    private GIMap mMap;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.getInstance().getEventBus().register(this);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mMap = ((Geoinfo)getActivity()).getMap();
        View v = inflater.inflate(R.layout.open_project_dialog, container, false);
        mProjectsList = (ListView)  v.findViewById(R.id.projects_list);

        projects_adapter = new ProjectsAdapter((Geoinfo)getActivity(),
                R.layout.project_selector_list_item,
                R.id.project_list_item_path);
        AddProjects(projects_adapter);
        mProjectsList.setAdapter(projects_adapter);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
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

}
