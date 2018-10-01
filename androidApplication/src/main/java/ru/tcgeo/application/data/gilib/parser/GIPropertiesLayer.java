package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

import ru.tcgeo.application.gilib.layer.GILayer;
import ru.tcgeo.application.gilib.layer.GISQLLayer;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.gilib.models.GIEncoding;
//import ru.tcgeo.application.data.gilib.models.GIIcon;

public class GIPropertiesLayer implements ILayersRoot
{
	public String m_name;
	public GILayer.GILayerType m_type;
	public String m_strType;
	public boolean m_enabled;
	public ArrayList<GIPropertiesLayer> m_Entries;
//	public GIIcon m_icon;
	public GISource m_source;
	public GIEncoding m_encoding;
	public GIPropertiesStyle m_style;
	public GIRange m_range;
	public GISQLDB m_sqldb;
    public GIEditable editable;

    public GIPropertiesLayer()
	{
		m_Entries = new ArrayList<GIPropertiesLayer>();
	}

	public void addEntry(GIPropertiesLayer layer)
	{
        boolean present = false;
//        for (GIPropertiesLayer l: m_Entries) {
//            if(l.m_source.name.equals(layer.m_source.name)){
//                present = true;
//            }
//        }
        //todo
        if(!present){
            m_Entries.add(layer);
        }
//		m_Entries.add(layer);
	}
	
	public String ToString()
	{
		String Res = "Layer \n";
		Res += "name=" + m_name + " type=" + m_type + "\n";
//		if(m_icon != null)
//		{
//		Res += m_icon.ToString() + "\n";
//		}
		if(m_source != null)
		{
			Res += m_source.ToString() + "\n" ;
		}
		if(m_sqldb != null)
		{
			Res += m_sqldb.ToString() + "\n" ;
		}
		if(m_encoding != null)
		{
		Res += m_encoding.ToString() + "\n" ;
		}
		if(m_style != null)
		{
		Res += m_style.ToString() + "\n" ;
		}
		if(m_range != null)
		{
		Res += m_range.ToString() + "\n";
		}
		for(GIPropertiesLayer lr : m_Entries)
		{
			Res += lr.ToString() + "\n";
		}
		return Res;
	}
	public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", "Layer");
		serializer.attribute("", "name", m_name);
		serializer.attribute("", "type", m_strType);
		serializer.attribute("", "enabled", String.valueOf(m_enabled));
		
		serializer = m_source.Save(serializer);
		if(m_sqldb != null)
		{
			serializer = m_sqldb.Save(serializer);
		}
		if(m_range != null)
		{
			serializer = m_range.Save(serializer);
		}
		if(m_style != null)
		{
			serializer.startTag("", "Style");
			serializer.attribute("", "type", m_style.m_type);
			serializer.attribute("", "lineWidth",String.valueOf(m_style.m_lineWidth));
			serializer.attribute("", "opacity", String.valueOf(m_style.m_opacity));
			for(GIColor color : m_style.m_colors)
			{
				serializer = color.Save(serializer);
			}
			serializer.endTag("", "Style");
		}
		if(m_encoding != null)
		{
			serializer.startTag("", "Encoding");
            serializer.attribute("", "type", m_encoding.m_encoding);
            serializer.endTag("", "Encoding");
        }
        if (editable != null) {
            serializer = editable.Save(serializer);
        }

		
//		if(m_icon != null)
//		{
//			serializer.startTag("", "Icon");
//			if(m_icon.m_source != null )
//			{
//				serializer =m_icon.m_source.Save(serializer);
//			}
//			serializer.endTag("", "Icon");
//		}

		serializer.endTag("", "Layer");
		return serializer;
	}

	public static class Builder{
        public GISource.Builder sourceBuilder;
        public GIPropertiesStyle.Builder styleBuilder;
        public GIRange.Builder rangeBuilder;
        public GISQLDB.Builder sqldbBuilder;
        //		private GISource source;
//		private GIPropertiesStyle style;
//		private GIRange range;
//		private GISQLDB sqldb;
        GIPropertiesLayer layer;
        private GIEditable.Builder editableBuilder;
        private String name;
        private GILayer.GILayerType type;
        private String strType;
        private boolean enabled;
        private GILayer.EditableType editable;

		public Builder(){}
		public Builder(GIPropertiesLayer layer){
			this.layer = layer;
			enabled = layer.m_enabled;
            sourceBuilder = new GISource.Builder(layer.m_source);
            styleBuilder = new GIPropertiesStyle.Builder(layer.m_style);
            rangeBuilder = new GIRange.Builder(layer.m_range);
            sqldbBuilder = new GISQLDB.Builder(layer.m_sqldb);
            editableBuilder = new GIEditable.Builder(layer.editable);
        }

        public Builder name(String name){
			this.name = name;
			return this;
		}

        public Builder type(GILayer.GILayerType type){
            this.type = type;
            this.strType = type.name();
            return this;
        }

        public Builder enabled(boolean enabled){
            this.enabled = enabled;
            return this;
        }

        public Builder sourceLocation(String location){
            sourceBuilder.location(location);
            return this;
        }

        public Builder sourceName(String name){
            sourceBuilder.name(name);
            return this;
        }

        public Builder styleType(String type){
            styleBuilder.type(type);
            return this;
        }

        public Builder styleLineWidth(double width){
            styleBuilder.lineWidth(width);
            return this;
        }

        public Builder styleOpacity(double opacity){
            styleBuilder.opacity(opacity);
            return this;
        }

        public Builder styleColor(GIColor color){
            styleBuilder.color(color);
            return this;
        }

        public Builder rangeFrom(int from){
            rangeBuilder.from(from);
            return this;
        }

        public Builder rangeTo(int to){
            rangeBuilder.to(to);
            return this;
        }

        public Builder sqldbMaxZ(int maxZ){
            sqldbBuilder.maxZ(maxZ);
            return this;
        }

        public Builder sqldbMinZ(int minZ){
            sqldbBuilder.minZ(minZ);
            return this;
        }

        public Builder sqldbZoomType(GISQLLayer.GISQLiteZoomingType zoomType) {
            sqldbBuilder.zoomType(zoomType);
            return this;
        }

        public Builder sqldbRatio(int ratio){
            sqldbBuilder.ratio(ratio);
            return this;
        }

        public Builder editable(GILayer.EditableType editable) {
            editableBuilder.editable(editable);
            this.editable = editable;
            return this;
        }

        public Builder active(boolean active) {
            editableBuilder.active(active);
            return this;
        }

        public GIPropertiesLayer build(){
            if(layer == null){
                layer = new GIPropertiesLayer();
            }
            if(name != null){
                layer.m_name = name;
            }
            if(type != null){
                layer.m_type = type;
            }
            if(strType != null){
                layer.m_strType = strType;
            }

            layer.m_enabled = enabled;

            if (layer.m_source != null) {
                layer.m_source = sourceBuilder.build();
            }
            if (layer.m_style != null) {
                layer.m_style = styleBuilder.build();
            }
            if (layer.m_range != null) {
                layer.m_range = rangeBuilder.build();
            }
            if (layer.m_sqldb != null) {
                layer.m_sqldb = sqldbBuilder.build();
            }
            if (layer.editable != null) {
                layer.editable = editableBuilder.build();
            }

            return layer;
        }


    }
}
