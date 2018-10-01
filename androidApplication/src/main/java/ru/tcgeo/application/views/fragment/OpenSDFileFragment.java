package ru.tcgeo.application.views.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.interfaces.IFolderItemListener;
import ru.tcgeo.application.utils.ExternalStorageUtil;

public class OpenSDFileFragment extends Fragment implements OnItemClickListener {

    IFolderItemListener folderListener;
    private List<String> m_item = null;
    private List<String> m_path = null;
    private List<String> m_ext = null;
    private String m_root = "/storage";//Environment.getExternalStorageDirectory().getAbsolutePath();

    @BindView(R.id.path)
    TextView m_PathTextView;

    @BindView(R.id.filelist)
    ListView m_ListView;

    //	https://stackoverflow.com/questions/40068984/universal-way-to-write-to-external-sd-card-on-android
    public OpenSDFileFragment() {

        Map<String, File> externalLocations = ExternalStorageUtil.getAllStorageLocations();
        File sdCard = externalLocations.get(ExternalStorageUtil.SD_CARD);
        File externalSdCard = externalLocations.get(ExternalStorageUtil.EXTERNAL_SD_CARD);
//        if(sdCard != null && sdCard.exists()) {
//            m_root = sdCard.getAbsolutePath();
//        } else if(externalSdCard != null && externalSdCard.exists()) {
//            m_root = externalSdCard.getAbsolutePath();
//        }
    }

    public static String getExtention(File file) {
        String filenameArray[] = file.getName().split("\\.");
        return filenameArray[filenameArray.length - 1];
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState) {
//		m_context = container.getContext();
        View v = inflater.inflate(R.layout.fragment_open_file, null);
        ButterKnife.bind(this, v);

        m_ext = new ArrayList<String>();
        String[] exts = getActivity().getResources().getStringArray(R.array.extentions);
        for (String ext : exts) {
            m_ext.add(ext);
        }
        RelativeLayout.LayoutParams m_param;
        m_param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        m_param.addRule(RelativeLayout.CENTER_VERTICAL | RelativeLayout.CENTER_HORIZONTAL);
        //m_param.setMargins(64, 64, 64, 64);

        v.setLayoutParams(m_param);
        setDir(m_root);
        //getDir(m_root, m_ListView);

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onListItemClick((ListView) parent, parent, position, id);
    }


    public void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(m_path.get(position));
        if (file.isDirectory()) {
            if (file.canRead()) {
                getDir(m_path.get(position), l);
            } else {
                if (folderListener != null) {
                    folderListener.OnCannotFileRead(file);
                }
            }
        } else {
            if (folderListener != null) {
                folderListener.OnFileClicked(file);
                folderListener.onDissmiss();
            }
        }

    }

    private void getDir(String dirPath, ListView v) {
        m_PathTextView.setText("Location: " + dirPath);
        m_item = new ArrayList<String>();
        m_path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();

        Comparator comp = new Comparator() {
            public int compare(Object o1, Object o2) {
                File f1 = (File) o1;
                File f2 = (File) o2;
                if (f1.isDirectory() && !f2.isDirectory()) {
                    return -1;
                } else if (!f1.isDirectory() && f2.isDirectory()) {
                    return 1;
                } else {
                    return f1.compareTo(f2);

                }
            }
        };
        Arrays.sort(files, comp);

        if (!dirPath.equals(m_root)) {
            m_item.add(m_root);
            m_path.add(m_root);
            m_item.add("../");
            m_path.add(f.getParent());
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isDirectory()) {
                m_path.add(file.getPath());
                m_item.add(file.getName() + "/");
            } else {
                if (m_ext.contains(getExtention(file))) {
                    m_path.add(file.getPath());
                    m_item.add(file.getName());
                }
            }
        }
        setItemList(m_item);
    }

    public void setIFolderItemListener(IFolderItemListener folderItemListener) {
        this.folderListener = folderItemListener;
    }

    public void setDir(String dirPath) {
        getDir(dirPath, m_ListView);
    }

    public void setItemList(List<String> item) {
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(getActivity(), R.layout.file_list_row_layout, item);
        m_ListView.setAdapter(fileList);
        m_ListView.setOnItemClickListener(this);
    }

}
