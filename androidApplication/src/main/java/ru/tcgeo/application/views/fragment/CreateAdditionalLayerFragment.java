package ru.tcgeo.application.views.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.view.IFolderItemListener;

public class CreateAdditionalLayerFragment extends Fragment {

//    @BindView(R.id.path)
//    TextView m_PathTextView;
//
//    @BindView(R.id.filelist)
//    ListView m_ListView;
IFolderItemListener folderListener;
    //	https://stackoverflow.com/questions/40068984/universal-way-to-write-to-external-sd-card-on-android
    public CreateAdditionalLayerFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState) {

        View v = inflater.inflate(R.layout.fragment_create_layer, null);
        ButterKnife.bind(this, v);

        RelativeLayout.LayoutParams m_param;
        m_param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        m_param.addRule(RelativeLayout.CENTER_VERTICAL | RelativeLayout.CENTER_HORIZONTAL);

        v.setLayoutParams(m_param);


        return v;
    }


    public void setIFolderItemListener(IFolderItemListener folderItemListener) {
        this.folderListener = folderItemListener;
    }

}
