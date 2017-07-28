package ru.tcgeo.application.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

import ru.tcgeo.application.R;


public class SPUtils {
    final static private String SAVED_PATH = "default_project_path";

    private final SharedPreferences preferences;
    private Context context;

    public SPUtils(Context context) {
        preferences = context.getSharedPreferences(
                "user.xml", Context.MODE_PRIVATE);
        this.context = context;
    }

    public String getLastProjectPath() {
        return preferences.getString(SAVED_PATH, getNewProjectName());
    }

    public void setLastProjectPath(String path) {
        preferences.edit().putString(SAVED_PATH, path).apply();
    }

    public String getNewProjectName() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator
                + context.getString(R.string.default_project_name)
                + context.getString(R.string.project_file_extention);
        int i = 1;
        while (new File(path).exists()) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator
                    + context.getString(R.string.default_project_name) + i
                    + context.getString(R.string.project_file_extention);
            i++;
        }
        return path;
    }

}
