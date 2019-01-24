package ru.tcgeo.application.data.gilib.layer;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import ru.tcgeo.application.data.GIMap;
import ru.tcgeo.application.data.gilib.models.GIBounds;
import ru.tcgeo.application.data.gilib.parser.GIRange;
import ru.tcgeo.application.data.gilib.requestor.GIDataRequestor;


public class GIGroupLayer extends GILayer
{
	//TODO: make private
	public ArrayList<GILayer> m_list;

	public GIGroupLayer ()
    {
		type = GILayerType.LAYER_GROUP;
		m_list = new ArrayList<GILayer>();
	}

	@Override
	public void Redraw (GIBounds area, Bitmap bitmap, Integer opacity, double scale)
	{
		double _scale = GIMap.getScale(area, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
		if(_scale == 0){return;}
		if(scale == 0){return;}
		float scale_factor = (float) (scale/_scale);
		for (int i = 0; i < m_list.size(); ++i)
		{
			if(Thread.currentThread().isInterrupted())
			{
				//Log.d("LogsThreads", "Thread " + Thread.currentThread().getId() + "Redraw canceled at " + i + " of " + m_list.size());
				return;
			}
			if (m_list.get(i).m_layer_properties.m_enabled)
			{
				if (m_list.get(i).m_layer_properties.m_range.IsWithinRange(_scale / scale_factor)) {//_scale/scale_factor)
					m_list.get(i).Redraw(area, bitmap, opacity, scale);
					Log.d("LogsThreads", "Redraw " + m_list.get(i).m_name);
				}
			}

		}
		if(scale_factor != 1)
		{
			return;
		}
		// wkbMultiPoint, wkbPolygon, wkbLineString, any unknown&undefined
		// in RedrawLabels order
		int[] types = {4 ,3, 2, 0}; //0
		for(int t = 0; t < types.length; t++)
		{
			int type = types[t];
			for (int i = 0; i < m_list.size(); ++i)
			{
				if(Thread.currentThread().isInterrupted())
				{
					//Log.d("LogsThreads", "Thread " + Thread.currentThread().getId() + "Redraw canceled at " + i + " of " + m_list.size());
					return;
				}
				if (m_list.get(i).getType() == type)
				{
					if (m_list.get(i).m_layer_properties.m_enabled && m_list.get(i).m_layer_properties.m_range.IsWithinRange(_scale / scale_factor))
					{
						m_list.get(i).RedrawLabels(area, bitmap, scale_factor, scale);//Redraw(area, bitmap, opacity, scale);
						Log.d("LogsThreads", "Redraw labels of " + i);
					}
				}
			}
		}
	}

	public GILayer AddLayer(GILayer layer) {
		if (!m_list.contains(layer)) {
			m_list.add(layer);
			layer.position = m_list.indexOf(layer);
		}
		return layer;
	}

	public GILayer AddLayer(GILayer layer, GIRange range, boolean visible) {
		if (!m_list.contains(layer))
		{
			m_list.add(layer);
			layer.position = m_list.indexOf(layer);
		}
		return layer;
	}

	public GILayer InsertLayerAt(GILayer layer, int position) {
		if (!m_list.contains(layer))
		{
			m_list.add(position, layer);
			layer.position = m_list.indexOf(layer);
		}
		return layer;
	}

	@Override
    public GIDataRequestor RequestDataIn(GIBounds point, GIDataRequestor requestor, double scale) {
		for (GILayer layer : m_list)
		{
			if (!layer.m_layer_properties.m_enabled)
				continue;
			if (!layer.m_layer_properties.m_range.IsWithinRange(scale))
				continue;

			requestor = layer.RequestDataIn(point, requestor, scale);
		}
		
		return requestor;
	}
	
	@Override
	public boolean RemoveAll()
	{
		for(int i = m_list.size() - 1; i >= 0; i--)
		{
			GILayer layer = m_list.get(i);
			layer.RemoveAll();
			m_list.remove(layer);
			
		}

		m_list.clear();
		return true;
	}

}
