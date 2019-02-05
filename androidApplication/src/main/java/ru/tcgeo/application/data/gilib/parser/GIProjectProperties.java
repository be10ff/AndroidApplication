package ru.tcgeo.application.data.gilib.parser;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.apache.commons.io.FileUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import ru.tcgeo.application.App;
import ru.tcgeo.application.R;
import ru.tcgeo.application.data.gilib.models.GIProjection;


public class GIProjectProperties {
    public String m_path;
    public String m_name;
    public String m_decription;
    public String m_markers;
    public String m_markers_source;
    public String m_str_projection;
    public GIProjection m_projection;
    public double m_top;
    public double m_bottom;
    public double m_left;
    public double m_right;
    public GIPropertiesGroup m_Group;

    private Context context;

    public GIProjectProperties(Context context) {
        this.context = context;

        m_name = context.getString(R.string.default_project_name);
        m_decription = context.getString(R.string.default_project_name);
        m_path = App.Instance().getPreference().getNewProjectName();
        m_str_projection = "WGS84";
        m_markers = "";
        m_top = 65;
        m_bottom = 46;
        m_left = 28;
        m_right = 48;

        m_Group = new GIPropertiesGroup();
    }

    public GIProjectProperties(String path) {
        this.m_path = path;
        this.LoadPro(path);
    }

    public GIProjectProperties(InputStream stream) {
        this.m_path = null;
        this.LoadPro(stream);
    }

    public GIProjectProperties(String path, boolean info_only) {
        this.m_path = path;
        this.LoadInfo(path);
    }

    public String ToString() {
        String res = "";
        res = res + "Project \n\r name=" + m_name + "\n Map \n";
        if (m_Group != null) {
            res = res + m_Group.ToString();
        }
        res = res + "Description text=" + m_decription + "\nBounds projection=" + m_str_projection + " top=" + m_top + " left=" + m_left + " bottom=" + m_bottom + " right=" + m_right + "\n";
        res = res + "Markers file=" + m_markers + "\n";
        return res;
    }

    public boolean LoadInfo(String path) {
        m_path = path;
        boolean res = false;
        try {
            XmlPullParser parser;
            FileInputStream xmlFile = null;
            try {
                xmlFile = new FileInputStream(path);
            } catch (FileNotFoundException e) {
            }
            XmlPullParserFactory factiry = XmlPullParserFactory.newInstance();
            factiry.setNamespaceAware(true);
            parser = factiry.newPullParser();
            parser.setInput(xmlFile, null);

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if (parser.getName().equalsIgnoreCase("Project")) {
                        GIParser parser_project = new GIParser(parser, this);
                        parser = parser_project.ReadInfo();
                    }
                }
                try {
                    parser.next();
                } catch (IOException e) {
                } finally {
                }


            }
        } catch (XmlPullParserException e) {
        }
        return res;
    }

    public boolean LoadPro(String path) {
        m_path = path;
        boolean res = false;
        try {
            XmlPullParser parser;
            FileInputStream xmlFile = null;
            try {
                xmlFile = new FileInputStream(path);
            } catch (FileNotFoundException e) {
            }
            XmlPullParserFactory factiry = XmlPullParserFactory.newInstance();
            factiry.setNamespaceAware(true);
            parser = factiry.newPullParser();
            parser.setInput(xmlFile, null);

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if (parser.getName().equalsIgnoreCase("Project")) {
                        GIParser parser_project = new GIParser(parser, this);
                        parser = parser_project.ReadSection();
                    }
                }
                try {
                    parser.next();
                } catch (IOException e) {
                } finally {
                }
            }
        } catch (XmlPullParserException e) {
        }
        return res;
    }

    public boolean LoadPro(InputStream xmlFile) {
        boolean res = false;
        try {
            XmlPullParser parser;
            XmlPullParserFactory factiry = XmlPullParserFactory.newInstance();
            factiry.setNamespaceAware(true);
            parser = factiry.newPullParser();
            parser.setInput(xmlFile, null);

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if (parser.getName().equalsIgnoreCase("Project")) {
                        GIParser parser_project = new GIParser(parser, this);
                        parser = parser_project.ReadSection();
                    }
                }
                try {
                    parser.next();
                } catch (IOException e) {
                } finally {
                }
            }
        } catch (XmlPullParserException e) {
        }
        return res;
    }

    //----------------------------------------------------------------------------------------
    public void SavePro(String path) {
        try {

            String sourcePath = new File(path).getParent() + "/temp.pro";
            FileOutputStream tmpFile = new FileOutputStream(sourcePath);
            XmlSerializer serializer = Xml.newSerializer();

            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            StringWriter writer = new StringWriter();

            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Project");
            serializer.attribute("", "name", m_name);

            serializer.startTag("", "Map");
            serializer = m_Group.Save(serializer);

            serializer.endTag("", "Map");

            serializer.startTag("", "Description");
            serializer.attribute("", "text", m_decription);
            serializer.endTag("", "Description");

            serializer.startTag("", "Bounds");
            serializer.attribute("", "projection", m_str_projection);
            serializer.attribute("", "top", String.valueOf(m_top));
            serializer.attribute("", "bottom", String.valueOf(m_bottom));
            serializer.attribute("", "left", String.valueOf(m_left));
            serializer.attribute("", "right", String.valueOf(m_right));
            serializer.endTag("", "Bounds");

            if (m_markers_source != null) {
                serializer.startTag("", "Markers");
                serializer.attribute("", "file", m_markers);
                serializer.attribute("", "source", m_markers_source);
                serializer.endTag("", "Markers");
            }

            serializer.endTag("", "Project");
            serializer.endDocument();
            writer.toString();
            tmpFile.write(writer.toString().getBytes());
            tmpFile.flush();
            tmpFile.close();

            FileUtils.copyFile(new File(sourcePath), new File(path));

        } catch (Exception e) {
            Log.d("LOG_TAG", e.toString());
        }

    }
}

