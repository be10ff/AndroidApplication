package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

//import ru.tcgeo.application.gilib.models.GIIcon;
import ru.tcgeo.application.gilib.GILayer;


public class GIParserGroup extends GIParser {

	GIPropertiesLayer m_root;
	GIPropertiesGroup m_current;
	public GIParserGroup(XmlPullParser parent, GIPropertiesLayer root, GIProjectProperties ps)
	{
		super(parent, ps);
		section_name = "Group";
		m_root = root;	
		m_current = new GIPropertiesGroup();
	}
	//<Group name="" opacity="1.0" enabled="true" obscure="false">
	//--------------------------------------------------
	@Override
	protected void ReadSectionsValues()
	{
		m_current.m_type = GILayer.GILayerType.LAYER_GROUP;
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("name"))
			{
				m_current.m_name = m_ParserCurrent.getAttributeValue(i);
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("opacity"))
			{
				m_current.m_opacity = Double.valueOf(m_ParserCurrent.getAttributeValue(i));
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("enabled"))
			{
				m_current.m_enabled = Boolean.valueOf(m_ParserCurrent.getAttributeValue(i));
			}	
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("obscure"))
			{
				m_current.m_obscure = Boolean.valueOf(m_ParserCurrent.getAttributeValue(i));
			}	
		}
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		String CurrentSectionName = m_ParserCurrent.getName();
		if(CurrentSectionName.equalsIgnoreCase("group"))
		{
			GIParserGroup parser = new GIParserGroup(m_ParserCurrent, m_current, m_ps);
			m_ParserCurrent = parser.ReadSection();
		}
		if(CurrentSectionName.equalsIgnoreCase("Layer"))
		{
			//m_current.m_Entries = new ArrayList<LayerProperties>();
			GIParserLayer parser = new GIParserLayer(m_ParserCurrent, m_current);
			m_ParserCurrent = parser.ReadSection();
		}
//		if(CurrentSectionName.equalsIgnoreCase("Icon"))
//		{
//			m_current.m_icon = new GIIcon();
//			GIParserIcon parser = new GIParserIcon(m_ParserCurrent, m_current.m_icon);
//			m_ParserCurrent = parser.ReadSection();
//		}
	}
	
	@Override
	protected void FinishSection()
	{
		if(m_root == null)
		{
			m_ps.m_Group = m_current;
		}
		else
		{
			//m_root.m_Entries.add(m_current);
			m_root.addEntry(m_current);
		}
	}
	//-----------------------------------------------------------------
}
