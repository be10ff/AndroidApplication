package ru.tcgeo.application.gilib.gps;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import ru.tcgeo.application.R;

public class GICompassFragment extends Fragment
{
	
	public GICompassFragment() 
	{
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.compass_view, null);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		param.addRule(RelativeLayout.CENTER_HORIZONTAL);
		v.setLayoutParams(param);
		return v;
	}
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
