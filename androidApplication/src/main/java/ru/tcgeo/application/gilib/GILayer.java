package ru.tcgeo.application.gilib;

import android.graphics.Bitmap;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIEncoding;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIStyle;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.application.wkt.GIGPSPointsLayer;

public abstract class GILayer
{
	public GILayerType type_;

	public enum GILayerType
	{
		// SQL_LAYER -> GITileInfoOSM
		// SQL_YANDEX_LAYER -> GISQLYandexTile

		LAYER_GROUP, RASTER_LAYER, VECTOR_LAYER, TILE_LAYER, ON_LINE, SQL_LAYER, DBASE, XML, SQL_YANDEX_LAYER, PLIST;
	}

	protected GIBounds     m_maxExtent;
	protected GIProjection m_projection;
	protected GIRenderer   m_renderer;
	protected String       m_name;

	// holds address of OGRLayer/GDALDataset
	public long            m_id;
	public GIPropertiesLayer m_layer_properties;



	public static GILayer CreateLayer (String path, GILayerType type)
	{
		switch (type)
		{
			case ON_LINE:
			{
//				if(path.equalsIgnoreCase("OSM"))
//					return new GIOSMLayer(path);
//				if(path.equalsIgnoreCase("Google"))
//					return new GIGoogleLayer(path);
//				if(path.equalsIgnoreCase("GeoPortal"))
//					return new GIWMSLayer(path);
//				if(path.equalsIgnoreCase("Yandex"))
//					return new GIYandexLayer(path);
			}
			case SQL_LAYER:
			{
				GISQLLayer layer = new GISQLLayer(path);
				layer.type_ = type;
				return layer;
			}
			case SQL_YANDEX_LAYER:
			{
				GISQLLayer layer = new GISQLLayer(path);
				layer.type_ = type;
				return layer;
			}
			case DBASE:
			{
				return new GIEditableSQLiteLayer(path, new GIVectorStyle());
			}
			case XML:
			{
				return new GIGPSPointsLayer(path, new GIVectorStyle());
			}
//			case PLIST:
//			{
//				return new GISPECSLayer(path, new GIVectorStyle());
//			}
			default:
			{
				return null;
			}
		}
	}

	public static GILayer CreateLayer (String path, GILayerType type,
			GIStyle style)
	{
		switch (type)
		{
			case ON_LINE:
			{
//				if(path.equalsIgnoreCase("OSM"))
//					return new GIOSMLayer(path);
//				if(path.equalsIgnoreCase("Google"))
//					return new GIGoogleLayer(path);
//				if(path.equalsIgnoreCase("GeoPortal"))
//					return new GIWMSLayer(path);
//				if(path.equalsIgnoreCase("Yandex"))
//					return new GIYandexLayer(path);
			}

			case SQL_LAYER:
			{
				GISQLLayer layer = new GISQLLayer(path);
				layer.type_ = type;
				return layer;
			}
			case SQL_YANDEX_LAYER:
			{
				GISQLLayer layer = new GISQLLayer(path);
				layer.type_ = type;
				return layer;
			}
			case DBASE:
			{
				return new GIEditableSQLiteLayer(path, (GIVectorStyle)style);
			}
			case XML:
			{
				return new GIGPSPointsLayer(path, (GIVectorStyle)style);
			}
//			case PLIST:
//			{
//				return new GISPECSLayer(path, (GIVectorStyle)style);
//			}
			default:
			{
				return null;
			}
		}
	}

	public static GILayer CreateLayer (String path, GILayerType type,
	        GIStyle style, GIEncoding encoding)
	{
		switch (type)
		{
			case ON_LINE:
			{
//				if(path.equalsIgnoreCase("OSM"))
//					return new GIOSMLayer(path);
//				if(path.equalsIgnoreCase("Google"))
//					return new GIGoogleLayer(path);
//				if(path.equalsIgnoreCase("GeoPortal"))
//					return new GIWMSLayer(path);
//				if(path.equalsIgnoreCase("Yandex"))
//					return new GIYandexLayer(path);
			}
			case SQL_LAYER:
			{
				return new GISQLLayer(path);
			}
			case DBASE:
			{
				return new GIEditableSQLiteLayer(path, (GIVectorStyle)style, encoding);
			}
			case XML:
			{
				return new GIGPSPointsLayer(path, (GIVectorStyle)style, encoding);
			}
//			case PLIST:
//			{
//				return new GISPECSLayer(path, (GIVectorStyle)style);
//			}
			default:
			{
				return null;
			}
		}
	}

	public abstract void Redraw (GIBounds area, Bitmap bitmap, Integer opacity, double scale);

	public void RedrawLabels (GIBounds area, Bitmap bitmap, float scale_factor, double s)
	{
		// TODO
	}

	public void AddStyle(GIStyle style)
	{
		m_renderer.AddStyle(style);
	}

	public Boolean LabelByCharacteristic (String name)
	{
		return null;
		// TODO
	}

	public void DeleteLabel ()
	{
		// TODO
	}

	public void setName (String name)
	{
		m_name = name;
	}

	public String getName ()
	{
		return m_name;
	}

	public GIBounds maxExtent ()
	{
		return null;
		// TODO
	}

	public GIProjection projection ()
	{
		return m_projection;
	}

	public GIRenderer renderer ()
	{
		return m_renderer;
	}
	
	GIDataRequestor RequestDataIn (GIBounds point, GIDataRequestor requestor, double scale)
	{
	
		return requestor;
	}
	public boolean RemoveAll()
	{
		return true;

	}
	public int getType()
	{
		return 0;
	}

}
