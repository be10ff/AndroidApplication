package ru.tcgeo.application.views.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.interfaces.IFolderItemListener;

public class OpenFileFragment extends Fragment implements OnItemClickListener {

    protected File file;
    IFolderItemListener folderListener;
    protected List<String> m_item = null;
    protected List<String> m_path = null;
    protected List<String> m_ext = null;
    protected String m_root = "/";//Environment.getExternalStorageDirectory().getAbsolutePath();

    private long lastExitAttemptTime = 0;

    @BindView(R.id.path)
    TextView m_PathTextView;

    @BindView(R.id.filelist)
    ListView m_ListView;

    //	https://stackoverflow.com/questions/40068984/universal-way-to-write-to-external-sd-card-on-android
    public OpenFileFragment() {
    }

    public static String getExtention(File file) {
        String filenameArray[] = file.getName().split("\\.");
        return filenameArray[filenameArray.length - 1];
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState) {
//		m_context = container.getContext();
        View v = inflater.inflate(R.layout.fragment_open_file, null);
        ButterKnife.bind(this, v);
//        m_PathTextView = (TextView) v.findViewById(R.id.path);
//        m_ListView = (ListView) v.findViewById(R.id.filelist);
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
        setDir();
        //getDir(m_root, m_ListView);

        return v;
    }

    protected void setDir() {
        setDir(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onListItemClick((ListView) parent, parent, position, id, false);
    }


    public void onListItemClick(ListView l, View v, int position, long id, boolean open) {
//        file = new File(m_path.get(position));
//        if (System.currentTimeMillis() - lastExitAttemptTime < 500) {
//            if (folderListener != null) {
//                folderListener.OnFileClicked(file);
//                folderListener.onDissmiss();
//            }
//        } else {
//            lastExitAttemptTime = System.currentTimeMillis();
//
//
//        }

        file = new File(m_path.get(position));
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
