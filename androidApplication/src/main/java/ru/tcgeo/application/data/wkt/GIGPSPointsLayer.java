package ru.tcgeo.application.data.wkt;

import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import ru.tcgeo.application.App;
import ru.tcgeo.application.data.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.data.gilib.models.GIEncoding;
import ru.tcgeo.application.data.gilib.models.GIVectorStyle;

public class GIGPSPointsLayer extends GIEditableLayer {
    private boolean isMarkersSource = false;

//    public GIGPSPointsLayer(String path) {
//        super(path);
//        type = GILayerType.XML;
//    }
//
//    public GIGPSPointsLayer(String path, GIVectorStyle style) {
//        super(path, style);
//        type = GILayerType.XML;
//    }

    public GIGPSPointsLayer(String path, GIVectorStyle style, GIEncoding encoding) {
        super(path, style);
        type = GILayerType.XML;
    }

    public void DeleteObject(GI_WktGeometry geometry) {

    }


    public void AddGeometry(GI_WktGeometry geometry) {
        geometry.m_ID = m_shapes.size();
        m_shapes.add(geometry);
    }

    public boolean isMarkersSource() {
        return isMarkersSource;
    }

    public void setMarkersSource(boolean markersSource) {
        isMarkersSource = markersSource;
    }

    public void Load() {
        try {
            XmlPullParser parser;
            FileInputStream xmlFile = null;
            try {
                xmlFile = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                Log.d("LOG_TAG", e.toString());
                return;
            }
            XmlPullParserFactory factiry = XmlPullParserFactory.newInstance();
            factiry.setNamespaceAware(true);
            parser = factiry.newPullParser();
            parser.setInput(xmlFile, null);

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if (parser.getName().equalsIgnoreCase("Geometries")) {
                        GIGPSParser parser_layer = new GIGPSParser(parser, this);
                        parser = parser_layer.ReadSection();
                    }
                }
                try {
                    parser.next();
                } catch (IOException e) {
                    Log.d("LOG_TAG", e.toString());
                } finally {

                }
            }
            xmlFile.close();
        } catch (Exception e) {
            Log.d("LOG_TAG", e.toString());
        }
    }

    public void Save() {
        try {
            String path = this.path;
            FileOutputStream xmlFile = new FileOutputStream(path);
            XmlSerializer serializer = Xml.newSerializer();

            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            StringWriter writer = new StringWriter();

            serializer.setOutput(writer);

            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Geometries");
            for (GI_WktGeometry geometry : m_shapes) {
                serializer = geometry.Serialize(serializer);
            }
            serializer.endTag("", "Geometries");
            serializer.endDocument();

            xmlFile.write(writer.toString().getBytes());
            xmlFile.flush();
            xmlFile.close();
        } catch (Exception e) {
            Log.d("LOG_TAG", e.toString());
            Toast.makeText(App.Instance(), e.toString(), Toast.LENGTH_SHORT);
        }
    }


}
