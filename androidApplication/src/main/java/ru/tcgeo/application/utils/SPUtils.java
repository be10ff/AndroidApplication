package ru.tcgeo.application.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

import ru.tcgeo.application.R;


public class SPUtils {
    private static final String SP_FIRST_INT = "sp_first_int";
    final static private String SAVED_PATH = "default_project_path";
    private static final String SP_EVENTS_SHOWN = "sp_events_shown";
    private static final String SP_FIRST = "sp_first";
    private static final String SP_COOKIES = "sp_cookie";
    private static final String SP_USER_ID = "sp_user_id";
    private static final String SP_LOGIN = "sp_login";
    private static final String SP_LOCALE = "sp_locale";
    private static final String SP_CHANGED = "sp_changed";
    private static final String SP_FIRST_TEAM = "sp_first_team";
    private static final String SP_RULES = "sp_utils";
    private static final String SP_PUSHSTATE = "sp_pushstate";
    private static final String SP_USER = "sp_user";
    private static final String SP_TOUR = "sp_tour";

    private static final String RUNS_COUNT = "runs_count";
    private static final String ASK_AGAINE_AFTER = "ask_againe_after";
    private static final String ASK_LATER = "ask_later";
    private static final String MARKET_RATED = "market_rated";
    private static final String LAST_VERSION = "last_version";

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
