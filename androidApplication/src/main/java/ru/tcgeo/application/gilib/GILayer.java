package ru.tcgeo.application.gilib;

import android.graphics.Bitmap;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIColor;
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
				if(path.equalsIgnoreCase("Yandex"))
					return new GIYandexLayer(path);
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
				if(path.equalsIgnoreCase("Yandex"))
					return new GIYandexLayer(path);
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
				if(path.equalsIgnoreCase("Yandex"))
					return new GIYandexLayer(path);
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

	public void free()
	{

	}
	public static class Builder {
		private String name;
		private GILayerType type;
		GIPropertiesLayer.Builder builder;
		GILayer layer;

		Builder(){}

		public Builder(GILayer layer){
			this.layer = layer;
		}


        public Builder name(String name) {
			this.name = name;
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.name(name);
			return this;
		}

        public Builder type(GILayer.GILayerType type) {
			this.type = type;
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.type(type);
			return this;
		}

        public Builder enabled(boolean enabled) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.enabled(enabled);
			return this;
		}

        public Builder sourceLocation(String location) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.sourceLocation(location);
			return this;
		}

        public Builder sourceName(String name) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.name(name);
			return this;
		}

        public Builder styleType(String type) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.styleType(type);
			return this;
		}

        public Builder styleLineWidth(double width) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.styleLineWidth(width);
			return this;
		}

        public Builder styleOpacity(double opacity) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.styleOpacity(opacity);
			return this;
		}

        public Builder styleColor(GIColor color) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.styleColor(color);
			return this;
		}

        public Builder rangeFrom(int from) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.rangeFrom(from);
			return this;
		}

        public Builder rangeTo(int to) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.rangeTo(to);
			return this;
		}

        public Builder sqldbMaxZ(int maxZ) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.sqldbMaxZ(maxZ);
			return this;
		}

        public Builder sqldbMinZ(int minZ) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.sqldbMinZ(minZ);
			return this;
		}

        public Builder sqldbZoomType(String zoomType) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.sqldbZoomType(zoomType);
			return this;
		}

        public Builder sqldbRatio(int ratio) {
			if (builder == null) {
				builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
			}
			builder.sqldbRatio(ratio);
			return this;
		}


        public GILayer build(){
            if(name != null){
                layer.m_name = name;
                if(builder == null){
                    builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
                }
                builder.name(name);

            }
            if(type != null){
                if(builder == null){
                    builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
                }
                builder.type(type);
            }
            if(builder!= null){
                layer.m_layer_properties = builder.build();
            }
            return layer;
        }
	}

}
