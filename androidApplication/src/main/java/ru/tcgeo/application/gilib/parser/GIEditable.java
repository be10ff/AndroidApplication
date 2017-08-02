package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

import ru.tcgeo.application.gilib.GILayer;

public class GIEditable {
    //	public String type;
    public GILayer.EditableType enumType;
    public boolean active;

    public GIEditable() {

    }

    public String ToString() {
        String Res = "Editable \n";
//		Res += "type=" + type + "\n";
        Res += "enumType=" + enumType.name() + "\n";
        Res += "active=" + active + "\n";
        return Res;
    }

    public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag("", "Editable");
//		serializer.attribute("", "type", type);
        serializer.attribute("", "type", enumType.name());
        serializer.attribute("", "active", String.valueOf(active));
        serializer.endTag("", "Editable");
        return serializer;
    }


    public static class Builder {
        public GILayer.EditableType type;
        public boolean active;

        GIEditable source;

        Builder() {
        }

        Builder(GIEditable source) {
            this.source = source;
        }

        Builder type(GILayer.EditableType type) {
            this.type = type;
            return this;
        }

        Builder active(boolean active) {
            this.active = active;
            return this;
        }

        GIEditable build() {
            if (source == null) {
                source = new GIEditable();
            }

            source.enumType = type;

            return source;
        }
    }
}
