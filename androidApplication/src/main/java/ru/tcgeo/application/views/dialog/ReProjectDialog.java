package ru.tcgeo.application.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.views.adapter.ReProjectsAdapter;
import ru.tcgeo.application.views.callback.ProjectsCallback;
import ru.tcgeo.application.views.callback.ProjectsHolderCallback;
import ru.tcgeo.application.views.viewholder.ProjectHolder;

/**
 * Created by a_belov on 23.07.15.
 */
public class ReProjectDialog extends Dialog {

    @Bind(R.id.rvProjects)
    RecyclerView rvProjects;

    private ReProjectsAdapter adapter;
    private Context context;

    private ProjectsCallback callback;

    public ReProjectDialog(Builder builder) {
        super(builder.context);
        this.callback = builder.callback;
        this.context = builder.context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.re_projects_dialog);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ReProjectsAdapter.Builder(context)
                .callback(new ProjectsHolderCallback() {
                    @Override
                    public void onClick(ProjectHolder holder) {
                        GIProjectProperties item = adapter.getItem(holder.getAdapterPosition());
                        if (!item.m_path.equalsIgnoreCase(((Geoinfo) context).getMap().ps.m_path)) {
                            callback.onClick(item);
                            dismiss();
                        }
                    }

                    @Override
                    public void onClose() {
                        dismiss();
                    }
                })
                .data(getProjects())
                .build();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        rvProjects.setLayoutManager(layoutManager);
        rvProjects.addItemDecoration(dividerItemDecoration);
        rvProjects.setAdapter(adapter);
    }

//
//    @Override public void onStart() {
//        super.onStart();
//
//        Window window = getDialog().getWindow();
////        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        WindowManager.LayoutParams windowParams = window.getAttributes();
//        windowParams.dimAmount = 0.0f;
//        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        window.setAttributes(windowParams);
//
//        int dialogWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
//        int dialogHeight = (int)(getActivity().getWindowManager().getDefaultDisplay().getHeight()*0.9f);
//
//        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
//    }


    public List<GIProjectProperties> getProjects() {
        List<GIProjectProperties> result = new ArrayList<>();
        File dir = (Environment.getExternalStorageDirectory());
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                if (file.getName().endsWith(".pro")) {
                    GIProjectProperties proj = new GIProjectProperties(file.getPath(), true);
                    if (proj != null) {
                        result.add(proj);
                    }
                }
            }
        }
        return result;
    }

    public static class Builder {
        private Context context;
        private ProjectsCallback callback;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(ProjectsCallback callback) {
            this.callback = callback;
            return this;
        }

        public ReProjectDialog build() {
            return new ReProjectDialog(this);
        }

    }

}
