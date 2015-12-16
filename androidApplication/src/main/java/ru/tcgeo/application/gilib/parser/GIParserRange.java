package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;



public class GIParserRange extends GIParser
{
	GIRange m_root;

	public GIParserRange(XmlPullParser parent, GIRange root)
	{
		super(parent);
		section_name = "Range";
		m_root = root;		
	}
	
	@Override	
	protected void ReadSectionsValues()
	{
		for(int  i = 0; i < m_ParserCurrent.getAttributeCount(); i++)
		{
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("from"))
			{
				if(!m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase("nan"))
				{
					m_root.m_from = Integer.parseInt(m_ParserCurrent.getAttributeValue(i));
				}
				else
				{
					//m_root.m_nan = m_ParserCurrent.getAttributeValue(i);
					m_root.m_from = -1;
				}
			}
			if(m_ParserCurrent.getAttributeName(i).equalsIgnoreCase("to"))
			{
				if(!m_ParserCurrent.getAttributeValue(i).equalsIgnoreCase("nan"))
				{
					m_root.m_to = Integer.parseInt(m_ParserCurrent.getAttributeValue(i));;
				}
				else
				{
					//m_root.m_nan = m_ParserCurrent.getAttributeValue(i);
					m_root.m_to = -1;
				}
			}			
		}
	}
	
	@Override
	protected void readSectionEnties() throws XmlPullParserException 
	{
		return;
	}
	
	@Override
	protected void FinishSection()
	{
		//m_root.m_icon = m_current;
		return;
	}
	//-----------------------------------------------------------------

}
