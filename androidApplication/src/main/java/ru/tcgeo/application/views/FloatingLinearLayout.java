package ru.tcgeo.application.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by a_belov on 03.07.15.
 */

//ru.tcgeo.application.views.FloatingLinearLayout
public class FloatingLinearLayout extends LinearLayout {
    private Context mContext;
    public FloatingLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }
    public FloatingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    public FloatingLinearLayout(Context context) {
        super(context);
        mContext = context;
    }
    @Override
    public void setVisibility(int visibility) {
        for(int i = 0; i < getChildCount(); i++){
            getChildAt(i).setVisibility(visibility);
        }
        super.setVisibility(visibility);
    }
}
