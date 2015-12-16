package ru.tcgeo.application.gilib;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import ru.tcgeo.application.R;

public class GIEditLayerDialog extends Fragment implements
		OnClickListener 
{
	public ToggleButton m_btnNew;
	public ToggleButton m_btnAttributes;
	public ToggleButton m_btnGeometry;
	public ToggleButton m_btnDelete;
	
	public GIEditLayerDialog() 
	{
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState)
	{
		View v = inflater.inflate(R.layout.edit_layer_layout, null);
		m_btnNew = (ToggleButton)v.findViewById(R.id.edit_layer_new);
		m_btnAttributes = (ToggleButton)v.findViewById(R.id.edit_layer_attributes);
		m_btnGeometry = (ToggleButton)v.findViewById(R.id.edit_layer_geometry);
		m_btnDelete = (ToggleButton)v.findViewById(R.id.edit_layer_delete);

		m_btnNew.setOnClickListener(this);
		m_btnAttributes.setOnClickListener(this);
		m_btnGeometry.setOnClickListener(this);
		m_btnDelete.setOnClickListener(this);

		if(GIEditLayersKeeper.Instance().m_layer == GIEditLayersKeeper.Instance().m_TrackLayer)
		{
			m_btnGeometry.setVisibility(View.GONE);
			m_btnNew.setVisibility(View.GONE);
		}
		else
		{
			m_btnGeometry.setVisibility(View.VISIBLE);
			m_btnNew.setVisibility(View.VISIBLE);
		}

		RelativeLayout.LayoutParams m_param;
		m_param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		m_param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		m_param.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		m_param.setMargins(10, 80, 0, 0);
		v.setLayoutParams(m_param);

		return v;
	}

	public void onClick(View v)
	{
		if(v.getId() == R.id.edit_layer_new)
		{
			if((GIEditLayersKeeper.Instance().getState() != GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_OBJECT_NEWLOCATION)&&(m_btnNew.isChecked()))
			{
				if(!GIEditLayersKeeper.Instance().CreateNewObject())
				{
					return;
				}
				GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_OBJECT_NEWLOCATION);

				m_btnAttributes.setEnabled(false);
				m_btnGeometry.setEnabled(false);
				m_btnDelete.setEnabled(false);
				m_btnAttributes.setChecked(false);
				m_btnGeometry.setChecked(false);
				m_btnDelete.setChecked(false);
				GIEditLayersKeeper.Instance().UpdateMap();
			}
			else
			{
				GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.RUNNING);
				GIEditLayersKeeper.Instance().FillAttributes();
				m_btnNew.setEnabled(false);

			}
		}
		if(v.getId() == R.id.edit_layer_attributes)
		{
			if(GIEditLayersKeeper.Instance().getState() != GIEditLayersKeeper.GIEditingStatus.WAITIN_FOR_SELECT_OBJECT)
			{
				GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.WAITIN_FOR_SELECT_OBJECT);
				m_btnNew.setEnabled(false);
				m_btnGeometry.setEnabled(false);
				m_btnDelete.setEnabled(false);
				m_btnNew.setChecked(false);
				m_btnGeometry.setChecked(false);
				m_btnDelete.setChecked(false);
			}
			else
			{
				GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.RUNNING);
				m_btnNew.setEnabled(true);
				m_btnGeometry.setEnabled(true);
				m_btnDelete.setEnabled(true);
			}
		}
		if(v.getId() == R.id.edit_layer_geometry)
		{
			if(GIEditLayersKeeper.Instance().m_layer == GIEditLayersKeeper.Instance().m_TrackLayer)
			{
				return;
			}
			if((GIEditLayersKeeper.Instance().getState() == GIEditLayersKeeper.GIEditingStatus.EDITING_GEOMETRY)||(GIEditLayersKeeper.Instance().getState() == GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_SELECT_GEOMETRY_TO_EDITING)||(GIEditLayersKeeper.Instance().getState() == GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_NEW_POINT_LOCATION))
			{
				GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.RUNNING);
				GIEditLayersKeeper.Instance().StopEditingGeometry();
				m_btnNew.setEnabled(true);
				m_btnAttributes.setEnabled(true);
				m_btnDelete.setEnabled(true);
				m_btnNew.setChecked(false);
				m_btnAttributes.setChecked(false);
				m_btnDelete.setChecked(false);
			}
			else
			{
				GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_SELECT_GEOMETRY_TO_EDITING);

				m_btnNew.setEnabled(false);
				m_btnAttributes.setEnabled(false);
				m_btnDelete.setEnabled(false);
			}


		}
		if(v.getId() == R.id.edit_layer_delete)
		{
			GIEditLayersKeeper.Instance().setState(GIEditLayersKeeper.GIEditingStatus.WAITING_FOR_TO_DELETE);
		}
	}
}
