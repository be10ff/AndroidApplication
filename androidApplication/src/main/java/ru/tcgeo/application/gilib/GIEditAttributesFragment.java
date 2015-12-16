package ru.tcgeo.application.gilib;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.tcgeo.application.R;
import ru.tcgeo.application.wkt.GIDBaseField;
import ru.tcgeo.application.wkt.GI_WktGeometry;

public class GIEditAttributesFragment extends Fragment implements
		OnClickListener{

	public Button m_btnYes;
	public Button m_btnNo;
	LayersAttributeAdapter adapter;
	ListView m_attributes_list;
	EditText new_attribute_name;
	EditText new_attribute_value;
	
	public GIEditAttributesFragment() 
	{

	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState)
	{
		View v = inflater.inflate(R.layout.edit_attributes_fragmint, null);
		m_btnYes = (Button)v.findViewById(R.id.Save);
		m_btnNo = (Button)v.findViewById(R.id.Discard);

		m_btnYes.setOnClickListener(this);
		m_btnNo.setOnClickListener(this);
		m_attributes_list = (ListView)v.findViewById(R.id.attributes_list_view);
		adapter = new LayersAttributeAdapter(this.getActivity(), R.layout.edit_attriute_item, R.id.field_name);
		AddAttributes(GIEditLayersKeeper.Instance().m_geometry, adapter);
		/**/
		if(GIEditLayersKeeper.Instance().m_layer.type_ == GILayer.GILayerType.XML)
		{
			View header = inflater.inflate(R.layout.edit_attributes_header, null);
			new_attribute_name = (EditText) header.findViewById(R.id.new_attribute_field_name);
			new_attribute_value = (EditText) header.findViewById(R.id.new_attribute_field_value);
			m_attributes_list.addFooterView(header);

			if(!GIEditLayersKeeper.Instance().m_geometry.m_attributes.containsKey("Name"))
			{
				new_attribute_name.setText("Name");
			}
		}
		/**/
		m_attributes_list.setAdapter(adapter);
		m_attributes_list.setSelection(0);
		m_attributes_list.smoothScrollToPosition(0);
		//
		RelativeLayout.LayoutParams m_param;
		m_param = new RelativeLayout.LayoutParams(400, LayoutParams.WRAP_CONTENT);
		m_param.addRule(RelativeLayout.ALIGN_PARENT_TOP|RelativeLayout.CENTER_HORIZONTAL);
		m_param.setMargins(0, 100, 0, 0);

		v.setLayoutParams(m_param);
		//setCanceledOnTouchOutside(true);
		return v;
	}
	public void onClick(View v)
	{
		if(v.getId() == R.id.Save)
		{
			for(int i = 0; i < m_attributes_list.getCount(); i++)
			{
				TextView name_text = (TextView)m_attributes_list.getChildAt(i).findViewById(R.id.field_name);
				EditText value_text = (EditText)m_attributes_list.getChildAt(i).findViewById(R.id.field_value);
				if(value_text != null && name_text != null)
				{
					GIEditLayersKeeper.Instance().m_geometry.m_attributes.get(String.valueOf(name_text.getText())).m_value = String.valueOf(value_text.getText());
				}
				if(GIEditLayersKeeper.Instance().m_layer.type_ == GILayer.GILayerType.XML)
				{
					if(new_attribute_value.getText().length() > 0)
					{
						GIDBaseField new_field = new GIDBaseField();
						new_field.m_value = new_attribute_value.getText();
						//TODO
						new_field.m_name = new_attribute_name.getText().toString();
						GIEditLayersKeeper.Instance().m_geometry.m_attributes.put(new_attribute_name.getText().toString(), new_field);
					}
				}
			}
			GIEditLayersKeeper.Instance().m_geometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;
			GIEditLayersKeeper.Instance().m_layer.Save();
			getActivity().getFragmentManager().beginTransaction().remove(this).commit();

		}
		if(v.getId() == R.id.Discard)
		{
			getActivity().getFragmentManager().beginTransaction().remove(this).commit();
		}
		if(GIEditLayersKeeper.Instance().m_EditLayerDialog != null)
		{
			if(GIEditLayersKeeper.Instance().m_EditLayerDialog.isAdded())
			{
				GIEditLayersKeeper.Instance().m_EditLayerDialog.m_btnNew.setEnabled(true);
				GIEditLayersKeeper.Instance().m_EditLayerDialog.m_btnAttributes.setEnabled(true);
				GIEditLayersKeeper.Instance().m_EditLayerDialog.m_btnGeometry.setEnabled(true);
				GIEditLayersKeeper.Instance().m_EditLayerDialog.m_btnDelete.setEnabled(true);
			}
		}
		GIEditLayersKeeper.Instance().UpdateMap();

	}
	public class LayersAttributeItem
	{
		final public String m_field_name;
		final public String m_field_value;

		LayersAttributeItem (String name, String value)
		{
			m_field_name = name;
			m_field_value = value;
		}
		@Override
		public String toString ()
		{
		    return m_field_name;

		}
	}
	public class LayersAttributeAdapter extends ArrayAdapter<LayersAttributeItem>
	{
		@Override
        public View getView (int position, View convertView, final ViewGroup parent)
        {
			final LayersAttributeItem item = getItem(position);
			View v = LayoutInflater.from(getContext()).inflate(R.layout.edit_attriute_item, null);
			TextView text_field_name = (TextView)v.findViewById(R.id.field_name);
			TextView text_field_value = (TextView)v.findViewById(R.id.field_value);
			
			text_field_name.setText(item.m_field_name);
			text_field_value.setText(item.m_field_value);
			return v;
        }
		
		public LayersAttributeAdapter (Context context, int resource, int textViewResourceId)
        {
	        super(context, resource, textViewResourceId);
        }
	}
	public void AddAttributes (GI_WktGeometry obj, ArrayAdapter<LayersAttributeItem> adapter)
	{
		if(adapter.isEmpty())
		{
			for (String name : obj.m_attributes.keySet())
			{
				GIDBaseField value = obj.m_attributes.get(name);
				LayersAttributeItem item = new LayersAttributeItem(name, value.m_value.toString());
				adapter.add(item);
			}
		}
	}
}
