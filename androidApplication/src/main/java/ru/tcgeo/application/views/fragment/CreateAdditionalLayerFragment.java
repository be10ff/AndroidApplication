package ru.tcgeo.application.views.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.data.gilib.layer.GILayer;
import ru.tcgeo.application.interfaces.IFolderItemListener;

public class CreateAdditionalLayerFragment extends Fragment {
    private static final int READ_REQUEST_CODE = 42;

    @OnClick(R.id.tvAddTraffic)
    public void onAddTraffic(){
        folderListener.OnFileClicked(new File("Yandex.traffic"));
    }

    @OnClick(R.id.tvAddPoints)
    public void onAddPointsLayer(){
        //todo
        folderListener.onAddPointsLayer(GILayer.EditableType.POI, "POI");
    }

    @OnClick(R.id.tvFromFile)
    public void onAddFromFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);


    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                File file = new File(uri.getPath());
                folderListener.OnFileClicked(file);
//                showImage(uri);
            }
        }

    }
}
