package ru.tcgeo.application.data.gilib.parser;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

import ru.tcgeo.application.data.gilib.layer.GILayer;

public class GIEditable {

    public GILayer.EditableType enumType;
    public boolean active;

    public GIEditable() {
        enumType = GILayer.EditableType.POI;
        active = false;
    }

    public GIEditable(GILayer.EditableType enumType, boolean active) {
        this.enumType = enumType;
        this.active = active;
    }

    public String ToString() {
        String Res = "Editable \n";
        Res += "enumType=" + enumType.name() + "\n";
        Res += "active=" + active + "\n";
        return Res;
    }

    public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag("", "Editable");
        serializer.attribute("", "type", enumType.name());
        serializer.attribute("", "active", String.valueOf(active));
        serializer.endTag("", "Editable");
        return serializer;
    }


    public static class Builder {
        public GILayer.EditableType editable;
        public boolean active;

        GIEditable source;

        Builder(GIEditable source) {
            if (source == null) {
                source = new GIEditable();
            }
            this.source = source;
            editable = source.enumType;
            active = source.active;
        }

        Builder editable(GILayer.EditableType editable) {
            this.editable = editable;
            return this;
        }

        Builder active(boolean active) {
            this.active = active;
            return this;
        }

        GIEditable build() {
            source.enumType = editable;
            source.active = active;

            return source;
        }
    }
}
