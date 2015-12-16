package ru.tcgeo.application.gilib;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;

public class GIDataRequestorImp implements GIDataRequestor {

	private boolean m_debug;
	private Context m_context;
	GIProjectProperties m_project_settings;
	//GIControlInfoInPoint m_info_control;
	public GIMap m_map;

	public class Pair
	{
		public String m_name;
		public String m_value;

		public Pair(String n, String v)
		{
			m_name = n;
			m_value = v;
		}
	}
	public class Feature
	{
		public ArrayList<Pair> m_pairs;
		public GIGeometry m_feature;
		Feature(GIGeometry f)
		{
			m_pairs = new ArrayList<Pair>();
			m_feature = f;
		}
		public boolean Process(Pair pair)
		{
			return m_pairs.add(pair);
		}
		public boolean Has()
		{
			return (m_pairs.size() > 0);
		}
		public String GetTextInfo()
		{
			String res = new String();
			if(Has() || m_debug)
			{
				res = String.format("  %d%n", m_feature.m_id);
				for(int i = 0; i < m_pairs.size(); i++)
				{
					res += String.format("   %s %s%n", m_pairs.get(i).m_name, m_pairs.get(i).m_value);
				}
			}
			return res;
		}

	}
	public class Layer
	{
		public  ArrayList<Feature> m_geometries;
		public GILayer m_layer;
		public Feature m_current_feature;
		Layer(GILayer l)
		{
			m_geometries = new ArrayList<Feature>();
			m_layer = l;
		}
		public void StartObject(GIGeometry feature)
		{
			m_current_feature = new Feature(feature);
		}

		public boolean EndObject()
		{
			return m_geometries.add(m_current_feature);
		}
		public boolean Has()
		{
			for(int i = 0; i < m_geometries.size(); i++)
			{
				if(m_geometries.get(i).Has())
				{
					return true;
				}
			}
			return false;
		}
		public String GetTextInfo()
		{
			String res = new String();
			if(Has() || m_debug)
			{
				res = String.format(" in layer %s%n", m_layer.getName());
				for(int i = 0; i < m_geometries.size(); i++)
				{
					res += m_geometries.get(i).GetTextInfo();
				}
			}
			return res;
		}

	}
	private class InfPoint
	{
		public ArrayList<Layer> m_layers;
		public GILonLat m_point;
		Layer m_current_layer;

		InfPoint(GILonLat p)
		{
			m_layers = new ArrayList<Layer>();
			m_point = p;
		}

		public void StartLayer(GILayer layer)
		{
			m_current_layer = new Layer(layer);
		}

		public boolean EndLayer()
		{
			return m_layers.add(m_current_layer);
		}
		public boolean Has()
		{
			for(int i = 0; i < m_layers.size(); i++)
			{
				if(m_layers.get(i).Has())
				{
					return true;
				}
			}
			return false;
		}
		public String GetTextInfo()
		{
			String res = new String();
			res = String.format("At (%f. %f)%n", m_point.lon(),  m_point.lat());
			if(Has() || m_debug)
			{
				for(int i = 0; i < m_layers.size(); i++)
				{
					res += m_layers.get(i).GetTextInfo();
				}
			}
			else
			{
				res += "found nothing.";
			}
			return res;
		}
	}

	private Point m_show_point;
	private InfPoint m_point;

	public GILonLat getRequestPoint()
	{
		return m_point.m_point;
	}
	public boolean isLayerPresent(String layer_name)
	{
		for(int i = 0; i < m_point.m_layers.size(); i++)
		{
			//String nname = m_point.m_layers.get(i).m_layer.getName();
			if(layer_name.equalsIgnoreCase(m_point.m_layers.get(i).m_layer.getName()))
			{
				return true;
			}
		}
		return false;
	}
	public Layer LayerByName(String layer_name) //GILayer
	{
		for(int i = 0; i < m_point.m_layers.size(); i++)
		{
			if(layer_name.equalsIgnoreCase(m_point.m_layers.get(i).m_layer.getName()))
			{
				return m_point.m_layers.get(i);//.m_layer;
			}
		}
		return null;
	}

	public String GetText()
	{
		return m_point.GetTextInfo();
	}


	public GIDataRequestorImp(Context context, Point point, GIProjectProperties project_settings)
	{

		m_show_point = point;
		m_context = context;
		m_project_settings = project_settings;
		m_debug = false;
	}

	public boolean needsHierarchicalView() {
		return false;
	}

	public GIDataRequestor StartGatheringData(GILonLat point) {
		m_point = new InfPoint(point);
		return this;
	}

	public GIDataRequestor EndGatheringData(GILonLat point) {
		return this;
	}

	public GIDataRequestor StartHierarchyLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	public GIDataRequestor EndHierarchyLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	public GIDataRequestor StartLayer(GILayer layer) {
		m_point.StartLayer(layer);
		return this;
	}

	public GIDataRequestor EndLayer(GILayer layer) {
		m_point.EndLayer();
		return this;
	}

	public GIDataRequestor StartObject(GIGeometry geometry) {
		m_point.m_current_layer.StartObject(geometry);
		return this;
	}

	public GIDataRequestor EndObject(GIGeometry geometry) {
		m_point.m_current_layer.EndObject();
		return this;
	}

	public GIDataRequestor ProcessSemantic(String name, String value) {
		Pair current = new Pair(name, value);
		m_point.m_current_layer.m_current_feature.Process(current);
		return this;
	}
	public void ShowDialog(Context context, Point show_point, GIMap map)
	{
		//TODO
		//String res = GetText();
		m_map = map;
		//main working
		//GIScriptExpression err = (GIScriptExpression) m_project_settings.m_scriptparser_info.Eval(this);

		//ShowDlg(context, show_point,  GetText());
		//ShowControl("ID", GetText());
		String res = String.format(Locale.ENGLISH, "(%.0f : %.0f)", m_point.m_point.lon(), m_point.m_point.lat());
		ShowControl(GetText(), "");

	}
	public void ShowControl(String info_text, String caption_text)
	{
		//Log.v("ScriptLogs", "test");
		info_text = info_text.replace("\\n", System.getProperty("line.separator"));
		caption_text = caption_text.replace("\\n", System.getProperty("line.separator"));
		GIControlInfoInPoint  info_control = GIControlInfoInPoint.Instance(m_context, m_map, m_point.m_point, info_text, caption_text);
		//info_control.setMap(m_map);
	}
	
	public void LogMsg(String tag, String text)
	{
		Log.d(tag, text);
	}
	
	public void Show()
	{
		
	}
	
	private void ShowDlg(Context context, Point show_point, String info_text)
	{
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
		Dialog info_dialog = new Dialog(context, R.style.Theme_layers_dialog);
		info_dialog.setContentView(R.layout.info_dialog);
		info_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		info_dialog.setCanceledOnTouchOutside(false);
		info_dialog.setCancelable(true);
		
        
		info_dialog.setOnDismissListener(new OnDismissListener()
		{
			public void onDismiss (DialogInterface dialog)
			{
			}
		});

		//info_dialog.getWindow().setGravity(Gravity.TOP | Gravity.LEFT);
    	android.view.WindowManager.LayoutParams parameters = info_dialog.getWindow().getAttributes();
		if(show_point.x > dm.widthPixels - 483)
		{
			if(show_point.y > dm.heightPixels - 265)
			{
				info_dialog.getWindow().setBackgroundDrawableResource(R.drawable.point_info_panel_bottom_right_nine);
		    	parameters.x = show_point.x - 483 +16;
				parameters.y = show_point.y - 265 + 28;				
			}
			else
			{
				info_dialog.getWindow().setBackgroundDrawableResource(R.drawable.point_info_panel_top_right_nine);
		    	parameters.x = show_point.x - 483 +16 ;
				parameters.y = show_point.y -28;				
			}
		}
		else
		{
			if(show_point.y > dm.heightPixels - 265)
			{
				info_dialog.getWindow().setBackgroundDrawableResource(R.drawable.point_info_panel_bottom_left_nine);
		    	parameters.x = show_point.x - 16;
				parameters.y = show_point.y - 265 + 28;				
			}
			else
			{
				info_dialog.getWindow().setBackgroundDrawableResource(R.drawable.point_info_panel_top_left_nine);
		    	parameters.x = show_point.x - 16;
				parameters.y = show_point.y - 28;
			}
			
		}
		parameters.y += 65 + 5;
		parameters.x += 5;		
       	info_dialog.getWindow().setAttributes(parameters);
       	
       	TextView tv = (TextView)info_dialog.findViewById(R.id.textView1);
		tv.setText(info_text);

        info_dialog.show();

	}
	public Context GetContext()
	{
		return m_context;
	}
}
