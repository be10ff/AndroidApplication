package ru.tcgeo.application.gilib.gps;

import android.app.Fragment;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.widget.FrameLayout.LayoutParams;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
@Deprecated
public class GIGPSDialog extends Fragment implements OnClickListener, OnLongClickListener {

	//public ToggleButton m_btnGPSStatus;
	public ToggleButton m_btnAutoFollow;
	public ToggleButton m_btnTrack;
	public ToggleButton m_btnShowTrack;
	public ToggleButton m_btnPOI;
	//public TextView m_StatusText;
	//public TextView m_GPSOnOff;
	//public TextView m_GPSAccurancy;
	
	//public ImageButton m_ImageButtonStatus;
	public GIGPSDialog() 
	{

	}

	public void onClick(View v) 
	{

		if(v.getId() == R.id.auto_follow)
		{
			GIEditLayersKeeper.Instance().m_AutoFollow = m_btnAutoFollow.isChecked();

			if(m_btnAutoFollow.isChecked())
			{
				Location location = GIEditLayersKeeper.Instance().m_location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(location != null)
				{
					GILonLat go_to = GILonLat.fromLocation(location);
					GILonLat go_to_map = GIProjection.ReprojectLonLat(go_to, GIProjection.WGS84(), GIProjection.WorldMercator());
					GIEditLayersKeeper.Instance().getMap().SetCenter(go_to_map);
					//TODO
					GIEditLayersKeeper.Instance().GetPositionControl();
				}
			}
			GIEditLayersKeeper.Instance().GetPositionControl();
		}
		if(v.getId() == R.id.track_control)
		{
			if(GIEditLayersKeeper.Instance().m_TrackingStatus == GIEditLayersKeeper.GITrackingStatus.STOP)
			{
				if(!GIEditLayersKeeper.Instance().CreateTrack())
				{
					GIEditLayersKeeper.Instance().m_TrackingStatus = GIEditLayersKeeper.GITrackingStatus.STOP;
					m_btnTrack.setChecked(false);
				}
			}
			else
			{
				GIEditLayersKeeper.Instance().m_TrackingStatus = GIEditLayersKeeper.GITrackingStatus.STOP;
				GIEditLayersKeeper.Instance().StopTrack();
			}
		}
		if(v.getId() == R.id.show_track)
		{
			if(GIEditLayersKeeper.Instance().m_current_track_control != null) {
				GIEditLayersKeeper.Instance().m_current_track_control.Show(m_btnShowTrack.isChecked());
				GIEditLayersKeeper.Instance().getMap().UpdateMap();
			}
		}
		if(v.getId() == R.id.poi_control)
		{
			if(GIEditLayersKeeper.Instance().getState() != GIEditLayersKeeper.GIEditingStatus.EDITING_POI && GIEditLayersKeeper.Instance().getState() != GIEditLayersKeeper.GIEditingStatus.EDITING_GEOMETRY)
			{
				GIEditLayersKeeper.Instance().CreatePOI();
			}
			else
			{
				GIEditLayersKeeper.Instance().StopEditing();
			}
			
		}

	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState)
	{
		View v = inflater.inflate(R.layout.gps_dialog_layout, null);
		//m_btnGPSStatus = (ToggleButton)v.findViewById(R.id.gps_status);
		m_btnAutoFollow = (ToggleButton)v.findViewById(R.id.auto_follow);		
		m_btnTrack = (ToggleButton)v.findViewById(R.id.track_control);
		m_btnShowTrack = (ToggleButton)v.findViewById(R.id.show_track);
		m_btnPOI = (ToggleButton)v.findViewById(R.id.poi_control);
		//m_StatusText = (TextView)v.findViewById(R.id.textViewStatus);
		//m_GPSOnOff = (TextView)v.findViewById(R.id.textViewGpsOnOff);
		//m_ImageButtonStatus = (ImageButton)v.findViewById(R.id.imageButtonStatus);
		//m_GPSAccurancy = (TextView)v.findViewById(R.id.textAccurancy);
		//
		m_btnTrack.setChecked(GIEditLayersKeeper.Instance().m_TrackingStatus == GIEditLayersKeeper.GITrackingStatus.WRITE);
		m_btnAutoFollow.setChecked(GIEditLayersKeeper.Instance().m_AutoFollow);
		
		//m_btnGPSStatus.setOnClickListener(this);
		m_btnAutoFollow.setOnClickListener(this);
		m_btnTrack.setOnClickListener(this);
		m_btnShowTrack.setOnClickListener(this);
		m_btnPOI.setOnClickListener(this);
		m_btnAutoFollow.setOnLongClickListener(this);
		
		//SetGPSEnabledStatus(GIEditLayersKeeper.Instance().m_location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER));
//		if(GIEditLayersKeeper.Instance().m_location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null)
//		{
//			m_GPSAccurancy.setText(String.format("±%02d m", ((int)GIEditLayersKeeper.Instance().m_location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getAccuracy())));//
//		}
		
		RelativeLayout.LayoutParams m_param;
		m_param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		m_param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		m_param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		//TODO 
		m_param.setMargins(10, 0, 0, 10);
		v.setLayoutParams(m_param);

		return v;
	}
	
//	public void SetGPSEnabledStatus(boolean enabeled)
//	{
//		if(enabeled)
//		{
//			m_ImageButtonStatus.setImageResource(R.drawable.gps_on);
//			//m_GPSOnOff.setText(R.string.gps_on);
//		}
//		else
//		{
//			//m_GPSOnOff.setText(R.string.gps_off);
//			m_ImageButtonStatus.setImageResource(R.drawable.gps_off);
//			m_GPSAccurancy.setText("-- m");
//		}
//	}
//	
//	public void ChangeGPSStatus(int status)
//	{
//		switch(status)
//		{
//			case 0:
//			{
//				m_StatusText.setText(R.string.provider_out_of_service);
//				m_ImageButtonStatus.setImageResource(R.drawable.gps_off);
//				//m_GPSOnOff.setText(R.string.gps_on);
//				m_GPSAccurancy.setText("-- m");
//				break;
//			}
//			case 1:
//			{
//				m_StatusText.setText(R.string.provider_temporary_unavailable);
//				m_ImageButtonStatus.setImageResource(R.drawable.gps_off);
//				//m_GPSOnOff.setText(R.string.gps_on);
//				m_GPSAccurancy.setText("-- m");
//				break;
//			}
//			case 2:
//			{
//				m_StatusText.setText(R.string.provider_available);
//				m_ImageButtonStatus.setImageResource(R.drawable.gps_on);
//				//m_GPSOnOff.setText(R.string.gps_on);
//				break;
//			}
//
//		}
//	}

	public boolean onLongClick(View v) 
	{
		if(v.getId() == R.id.auto_follow) {
			GIEditLayersKeeper.Instance().m_current_track_control.Show(!GIEditLayersKeeper.Instance().m_current_track_control.mShow);
		}

//		if(v.getId() == R.id.auto_follow)
//		{
//			GIEditLayersKeeper.Instance().m_AutoFollow = m_btnAutoFollow.isChecked();
//			if(m_btnAutoFollow.isChecked())
//			{
//				Location location = GIEditLayersKeeper.Instance().m_location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//				if(location != null)
//				{
//					GILonLat go_to = GILonLat.fromLocation(location);
//					GILonLat go_to_map = GIProjection.ReprojectLonLat(go_to, GIProjection.WGS84(), GIProjection.WorldMercator());
//					GIEditLayersKeeper.Instance().getMap().SetCenter(go_to_map);
//					//TODO
//					GIEditLayersKeeper.Instance().GetPositionControl();
//				}
//			}
//		}

		return false;
	}
}
