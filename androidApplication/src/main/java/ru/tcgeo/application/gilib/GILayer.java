package ru.tcgeo.application.gilib;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;

import java.io.File;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.gilib.models.GIEncoding;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIStyle;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.application.gilib.parser.GIPropertiesStyle;
import ru.tcgeo.application.gilib.parser.GIRange;
import ru.tcgeo.application.gilib.parser.GISource;
import ru.tcgeo.application.wkt.GIGPSPointsLayer;

public abstract class GILayer
{
	public GILayerType type;
	public long m_id;
	public GIPropertiesLayer m_layer_properties;
	protected GIBounds     m_maxExtent;
	protected GIProjection m_projection;
	protected GIRenderer   m_renderer;
	protected String       m_name;

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
				layer.type = type;
				return layer;
			}
			case SQL_YANDEX_LAYER:
			{
				GISQLLayer layer = new GISQLLayer(path);
				layer.type = type;
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
			case ZIP:
			{
				return new GIGPSPointsLayer(path, new GIVectorStyle());
			}
			case FOLDER:
			{
				GIFolderLayer layer = new GIFolderLayer(path);
				layer.type = type;
				return layer;
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
				layer.type = type;
				return layer;
			}
			case SQL_YANDEX_LAYER:
			{
				GISQLLayer layer = new GISQLLayer(path);
				layer.type = type;
				return layer;
			}
			case FOLDER:
			{
				GIFolderLayer layer = new GIFolderLayer(path);
				layer.type = type;
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
			case FOLDER:
			{
				GIFolderLayer layer = new GIFolderLayer(path);
				layer.type = type;
				return layer;
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

	public static GIEditableLayer createTrack(String projectName, String date) {
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + projectName);
		if (!dir.exists()) {
			dir.mkdir();
		}

		File file = new File(projectName + "_" + date + "_track.xml");
		GIPropertiesLayer properties_layer = new GIPropertiesLayer();
		properties_layer.m_enabled = true;
		properties_layer.m_name = file.getName();
		properties_layer.m_range = new GIRange();
		properties_layer.m_source = new GISource("absolute", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + projectName + File.separator + file.getName());
		properties_layer.m_type = GILayer.GILayerType.XML;
		properties_layer.m_strType = "XML";
		GILayer layer;
		//
		Paint fill = new Paint();
		Paint line = new Paint();

		GIColor color_fill = new GIColor.Builder().description("fill").name("red").build();
		GIColor color_line = new GIColor.Builder().description("line").name("red").build();

		line.setColor(color_line.Get());
		line.setStyle(Paint.Style.STROKE);
		line.setStrokeWidth(2);

		fill.setColor(color_fill.Get());
		fill.setStrokeWidth(2);
		fill.setStyle(Paint.Style.FILL);

		GIVectorStyle vstyle = new GIVectorStyle(line, fill, 1);

		properties_layer.m_style = new GIPropertiesStyle.Builder()
				.type("vector")
				.lineWidth(2)
				.opacity(1)
				.color(color_line)
				.color(color_fill)
				.build();

		layer = GILayer.CreateLayer(properties_layer.m_source.GetAbsolutePath(), GILayer.GILayerType.XML, vstyle);
		Paint editing_fill = new Paint();
		editing_fill.setColor(Color.CYAN);
		editing_fill.setAlpha(96);
		editing_fill.setStyle(Paint.Style.FILL);

		Paint editing_stroke = new Paint();
		editing_stroke.setColor(Color.CYAN);
		editing_stroke.setStrokeWidth(2);
		editing_fill.setAlpha(128);
		editing_stroke.setStyle(Paint.Style.STROKE);
		GIVectorStyle vstyle_editing = new GIVectorStyle(
				editing_stroke, editing_fill,
				1);
		layer.AddStyle(vstyle_editing);

		layer.setName(file.getName());
		layer.m_layer_properties = properties_layer;
		return (GIEditableLayer) layer;
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

	public String getName()
	{
		return m_name;
	}

	public void setName(String name)
	{
		m_name = name;
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

	public enum GILayerType {
		LAYER_GROUP, RASTER_LAYER, VECTOR_LAYER, TILE_LAYER, ON_LINE, SQL_LAYER, DBASE, XML, SQL_YANDEX_LAYER, PLIST, ZIP, FOLDER
	}

	public enum EditableType {
		TRACK, POI, LINE, POLYGON, UNSET
	}

	public static class Builder {
		GIPropertiesLayer.Builder builder;
		GILayer layer;
		private String name;
		private GILayerType type;
        private EditableType editable;

		public Builder(GILayer layer){
			this.layer = layer;
			name = layer.m_name;
			type = layer.type;
			builder = new GIPropertiesLayer.Builder(layer.m_layer_properties);
		}


        public Builder name(String name) {
			this.name = name;
			builder.name(name);
			return this;
		}

        public Builder type(GILayer.GILayerType type) {
			this.type = type;
			builder.type(type);
			return this;
		}

        public Builder enabled(boolean enabled) {
			builder.enabled(enabled);
			return this;
		}

        public Builder sourceLocation(String location) {
			builder.sourceLocation(location);
			return this;
		}

        public Builder sourceName(String name) {
			builder.name(name);
			return this;
		}

        public Builder styleType(String type) {
			builder.styleType(type);
			return this;
		}

        public Builder editable(EditableType editable) {
            this.editable = editable;
            builder.editable(editable);
            return this;
        }

        public Builder active(boolean active) {
            builder.active(active);
            return this;
        }

        public Builder styleLineWidth(double width) {
			builder.styleLineWidth(width);
			return this;
		}

        public Builder styleOpacity(double opacity) {
			builder.styleOpacity(opacity);
			return this;
		}

        public Builder styleColor(GIColor color) {
			builder.styleColor(color);
			return this;
		}

        public Builder rangeFrom(int from) {
			builder.rangeFrom(from);
			return this;
		}

        public Builder rangeTo(int to) {
			builder.rangeTo(to);
			return this;
		}

        public Builder sqldbMaxZ(int maxZ) {
			builder.sqldbMaxZ(maxZ);
			return this;
		}

        public Builder sqldbMinZ(int minZ) {
			builder.sqldbMinZ(minZ);
			return this;
		}

        public Builder sqldbZoomType(String zoomType) {
			builder.sqldbZoomType(zoomType);
			return this;
		}

        public Builder sqldbRatio(int ratio) {
			builder.sqldbRatio(ratio);
			return this;
		}


        public GILayer build(){
            if(name != null){
                layer.m_name = name;
                builder.name(name);

            }
            if(type != null){
				layer.type = type;
				builder.type(type);
			}
			if (builder != null) {
				layer.m_layer_properties = builder.build();
			}
            return layer;
        }
	}

}
