package ru.tcgeo.application.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by a_belov on 10.07.15.
 */
public class ScreenUtils {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

//    public static int getWidth(Context context){
//        return  context.getWindowManager().getDefaultDisplay().getWidth();
//    }
//
//    public static int getHeight(){
//        return  getWindowManager().getDefaultDisplay().getHeight();
//    }
}
