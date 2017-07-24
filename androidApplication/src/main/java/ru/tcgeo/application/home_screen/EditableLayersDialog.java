package ru.tcgeo.application.home_screen;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import ru.tcgeo.application.App;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.home_screen.adapter.EditableLayersAdapter;

/**
 * Created by a_belov on 23.07.15.
 */
@Deprecated
public class EditableLayersDialog extends DialogFragment{

    ListView markers_list;
    private GIMap mMap;
//    ReEditableLayersAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.getInstance().getEventBus().register(this);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mMap = ((Geoinfo)getActivity()).getMap();
        View v = inflater.inflate(R.layout.markers_dialog, container, false);
        markers_list = (ListView)v.findViewById(R.id.markers_list);
        View header = inflater.inflate(R.layout.editable_layers_stop_edit, null);
        header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GIEditLayersKeeper.Instance().StopEditing();
                dismiss();
            }
        });
        markers_list.addHeaderView(header);

        EditableLayersAdapter adapter = new EditableLayersAdapter((Geoinfo)getActivity(), R.layout.markers_list_item, R.id.markers_list_item_text);

        adapter.AddEditableLayers(mMap.m_layers);

        markers_list.setAdapter(adapter);

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

}
