package ru.tcgeo.application.views.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.interfaces.IFolderItemListener;
import ru.tcgeo.application.views.adapter.DirAdapter;
import ru.tcgeo.application.views.callback.DirCallback;
import ru.tcgeo.application.views.viewholder.DirHolder;

public class ReOpenFileFragment extends Fragment {

    protected List<String> m_ext = null;
    protected String m_root = "/";//Environment.getExternalStorageDirectory().getAbsolutePath();
    //    protected File file;
    IFolderItemListener folderListener;
    @BindView(R.id.rvFiles)
    RecyclerView rvFiles;
    DirAdapter adapter;
    private long lastExitAttemptTime = 0;

    //	https://stackoverflow.com/questions/40068984/universal-way-to-write-to-external-sd-card-on-android
    public ReOpenFileFragment() {
    }

    public static String getExtention(File file) {
        String filenameArray[] = file.getName().split("\\.");
        return filenameArray[filenameArray.length - 1];
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState) {
        View v = inflater.inflate(R.layout.re_fragment_open_file, null);
        ButterKnife.bind(this, v);
        m_ext = new ArrayList<String>();
        String[] exts = getActivity().getResources().getStringArray(R.array.extentions);
        for (String ext : exts) {
            m_ext.add(ext);
        }
        LinearLayout.LayoutParams m_param;
        m_param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        m_param.addRule(RelativeLayout.CENTER_VERTICAL | RelativeLayout.CENTER_HORIZONTAL);
        v.setLayoutParams(m_param);


        adapter = new DirAdapter(new DirCallback() {
            @Override
            public void onOpen(DirHolder holder) {
                File file = adapter.getItem(holder.getAdapterPosition());
                folderListener.OnFileClicked(file);
            }

            @Override
            public void onExit(DirHolder holder) {
                File file = adapter.getItem(holder.getAdapterPosition()).getParentFile();
                if (file.isDirectory()) {
                    if (file.canRead()) {
                        getDir(file.getPath());
                    } else {
                        if (folderListener != null) {
                            folderListener.OnCannotFileRead(file);
                        }
                    }
                }
            }

            @Override
            public void onEnter(DirHolder holder) {
                File file = adapter.getItem(holder.getAdapterPosition());
                if (file.isDirectory()) {
                    if (file.canRead()) {
                        getDir(file.getPath());
                    } else {
                        if (folderListener != null) {
                            folderListener.OnCannotFileRead(file);
                        }
                    }
                }
            }
        });

        rvFiles.setHasFixedSize(true);
        rvFiles.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvFiles.setAdapter(adapter);


        setDir();
        return v;
    }

    protected void setDir() {
        getDir(Environment.getExternalStorageDirectory().getAbsolutePath());
    }


    protected void getDir(String dirPath) {
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

        List<File> fileList = new ArrayList<>();

        if (!dirPath.equals(m_root)) {
//            fileList.add(new File(m_root));
            fileList.add(f);
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isDirectory()) {
                fileList.add(file);
            } else {
                if (m_ext.contains(getExtention(file))) {
                    fileList.add(file);
                }
            }
        }

        adapter.setData(fileList);

    }

    public void setIFolderItemListener(IFolderItemListener folderItemListener) {
        this.folderListener = folderItemListener;
    }

}
