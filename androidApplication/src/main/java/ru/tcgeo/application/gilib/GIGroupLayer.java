package ru.tcgeo.application.gilib;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIScaleRange;


public class GIGroupLayer extends GILayer
{
	//TODO: make private
	public ArrayList<GITuple> m_list;

	public GIGroupLayer ()
    {
		type_ = GILayerType.LAYER_GROUP;
	    m_list = new ArrayList<GITuple>();
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
			if(m_list.get(i).visible && m_list.get(i).scale_range.IsWithinRange(_scale/scale_factor)) //_scale/scale_factor
			{
				m_list.get(i).layer.Redraw(area, bitmap, opacity, scale);
				Log.d("LogsThreads", "Redraw " + m_list.get(i).layer.m_name);
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
				if(m_list.get(i).layer.getType() == type)
				{
					if(m_list.get(i).visible && m_list.get(i).scale_range.IsWithinRange(_scale/scale_factor))
					{
						m_list.get(i).layer.RedrawLabels(area, bitmap, scale_factor, scale);//Redraw(area, bitmap, opacity, scale);
						Log.d("LogsThreads", "Redraw labels of " + i);
					}
				}
			}
		}
	}

	public int AddLayer(GILayer layer)
	{
		if(!m_list.contains(layer))
		{
			if(layer.type_ == GILayerType.SQL_LAYER )
			{
				m_list.add(0, new GITuple(layer, true, new GIScaleRange()));
				return 0;
			}
			m_list.add(new GITuple(layer, true, new GIScaleRange()));
		}
		return 0;
	}

	public int AddLayer(GILayer layer, GIScaleRange range, boolean visible)
	{
		if(!m_list.contains(layer))
		{
			m_list.add(new GITuple(layer, visible, range));
		}
		return 0;
	}
	public int InsertLayerAt(GILayer layer, int position)
	{
		if(!m_list.contains(layer))
		{
			m_list.add(position, new GITuple(layer, true, new GIScaleRange()));
		}
		return 0;
	}
	@Override
	GIDataRequestor RequestDataIn (GIBounds point, GIDataRequestor requestor, double scale)
	{
		for (GITuple tuple : m_list)
		{
			if (!tuple.visible)
				continue;
			if (!tuple.scale_range.IsWithinRange(scale))
				continue;
			
			requestor = tuple.layer.RequestDataIn(point, requestor, scale);			
		}
		
		return requestor;
	}
	
	@Override
	public boolean RemoveAll()
	{
		for(int i = m_list.size() - 1; i >= 0; i--)
		{
			GITuple tuple = m_list.get(i);
			tuple.layer.RemoveAll();
			m_list.remove(tuple);			
			
		}
		/*for (GITuple tuple : m_list)
		{
			tuple.layer.RemoveAll();
			m_list.remove(tuple);
		}*/
		m_list.clear();
		return true;
	}

	public void moveUp(GITuple tuple){
		int index = m_list.indexOf(tuple);
		if(index != -1 && index > 0 ){
			GITuple tmp = m_list.get(index - 1);
			m_list.set(index - 1 ,m_list.get(index));
			m_list.set(index, tmp);
		}
	}

	public void moveDown(GITuple tuple){
		int index = m_list.indexOf(tuple);
		if(index != -1 && index < m_list.size() - 1 ){
			GITuple tmp = m_list.get(index + 1);
			m_list.set(index + 1 ,m_list.get(index));
			m_list.set(index, tmp);
		}
	}
}
