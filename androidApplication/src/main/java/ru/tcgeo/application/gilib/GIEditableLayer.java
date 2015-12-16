package ru.tcgeo.application.gilib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.graphics.Bitmap;
import android.util.Log;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIEncoding;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.wkt.GIDBaseField;
import ru.tcgeo.application.wkt.GI_WktGeometry;

public abstract class GIEditableLayer extends GILayer
{
	protected String m_path;
	GIVectorStyle m_style;
	ArrayList<GIVectorStyle> m_additional_styles;
	GIEncoding m_encoding;

	public  ArrayList<GI_WktGeometry> m_shapes;
	protected Map<String, GIDBaseField> m_attributes;

	public enum GIEditableLayerStatus //цвет в списке слоев
	{
		UNEDITED,			// после конструктора, отрисовывается как слой, состояние не_редактируемого
		EDITED,				// выбран для редактирования. отрисовывается контролами
		UNSAVED;//,			// после завершения редактирования. отрисовывается контролами
	}
	public enum GIEditableLayerType
	{
		POINT, LINE, POLYGON, RING, TRACK;
	}
	public GIVectorStyle getPaint(GI_WktGeometry.GIWKTGeometryStatus status)
	{
		if((status == GI_WktGeometry.GIWKTGeometryStatus.GEOMETRY_EDITING) || (status == GI_WktGeometry.GIWKTGeometryStatus.NEW))
		{
			return ((GIEditableRenderer)m_renderer).m_additional_styles.get(0);
		}
		else
		{
			return ((GIEditableRenderer)m_renderer).m_style;
		}
	}
	public GIEditableLayerStatus m_Status;
	public GIEditableLayerType m_Type;
	@Override
	public void Redraw(GIBounds area, Bitmap bitmap, Integer opacity, double scale)
	{
		if(m_Status == GIEditableLayerStatus.UNEDITED)
		{
			synchronized(this)
			{
				m_renderer.RenderImage(this, area, opacity, bitmap, scale);
			}
		}
	}
	public GIEditableLayer(String path)
	{
		m_path = path;
		m_style = new GIVectorStyle();
		m_additional_styles = new ArrayList<GIVectorStyle>();
		m_renderer = new GIEditableRenderer(m_style);
		m_projection = GIProjection.WGS84();
		m_Status = GIEditableLayerStatus.UNEDITED;
		m_shapes = new ArrayList<GI_WktGeometry>();
		m_attributes = new HashMap<String, GIDBaseField>();
	}
	public GIEditableLayer(String path, GIVectorStyle style)
	{
		m_path = path;
		m_style = style;
		m_additional_styles = new ArrayList<GIVectorStyle>();
		m_renderer = new GIEditableRenderer(m_style);
		m_projection = GIProjection.WGS84();
		m_Status = GIEditableLayerStatus.UNEDITED;
		m_shapes = new ArrayList<GI_WktGeometry>();
		m_attributes = new HashMap<String, GIDBaseField>();
		Load();
	}

	public GIEditableLayer(String path, GIVectorStyle style, GIEncoding encoding)
	{
		Log.d("LOG_TAG", "new :" + path);
		m_path = path;
		m_style = style;
		m_additional_styles = new ArrayList<GIVectorStyle>();
		m_renderer = new GIEditableRenderer(m_style);
		m_projection = GIProjection.WGS84();
		m_encoding = encoding;
		m_Status = GIEditableLayerStatus.UNEDITED;
		m_shapes = new ArrayList<GI_WktGeometry>();
		m_attributes = new HashMap<String, GIDBaseField>();
		Load();
	}

	public void setType(GIEditableLayerType type)
	{
		m_Type = type;
	}

	GIDataRequestor RequestDataIn(GIBounds point, GIDataRequestor requestor, double scale)
	{
		requestor.StartLayer(this);
		for(GI_WktGeometry geom : m_shapes)
        {
    		if(geom.isTouch(point))
    		{
	    		requestor.StartObject(new GIGeometry(geom.m_ID));
	    		for(String key : geom.m_attributes.keySet())
	    		{
				    requestor.ProcessSemantic(key,  geom.m_attributes.get(key).m_value.toString());
	    		}
	    		requestor.EndObject(new GIGeometry(geom.m_ID));
    		}
        }
		requestor.EndLayer(this);	
		return requestor;
	}
	abstract public void DeleteObject(GI_WktGeometry geometry);
	abstract public void AddGeometry(GI_WktGeometry geometry);
	abstract public void Save();
	abstract public void Load();


}

