package ru.tcgeo.application.wkt;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.Map;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIVectorStyle;


public abstract class GI_WktGeometry
{
	public enum GIWKTGeometryType
	{
		POINT, LINE, POLYGON, RING, TRACK;
	}
	public enum GIWKTGeometryStatus
	{
		NEW, // создан новый. для добавления в таблицу. после конструктора. отрисовывается контролами как выбранный объект. при создании новой
		MODIFIED, //изменен существующий. для update таблицы. ставится после редактирования аттрибутов или геометрии
		//DELETED,
		SAVED, //после чтения из таблицы или сохранения
		GEOMETRY_EDITING; //при редактировании геометрии . отрисовывается контролами как выбранный объект. после первого сохранения только что создавнного
	}

	public GIWKTGeometryType m_type;
	public GIWKTGeometryStatus m_status;
	public long m_ID;
	public Map<String, GIDBaseField> m_attributes;
	
	public GI_WktGeometry() 
	{
		m_ID = -1;
	}

	public abstract String toWKT();
	public abstract void Draw(Canvas canvas, GIBounds area, float scale, Paint paint);
	public abstract void Paint(Canvas canvas, GIVectorStyle style);
	public abstract boolean IsEmpty();
	public abstract void Delete();
	public XmlSerializer Serialize(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("",  m_type.name());
		serializer.attribute("", "id", String.valueOf(m_ID));
		serializer.attribute("", "Geometry", SerializedGeometry());
		for(String key : m_attributes.keySet())
		{
			serializer.attribute("", key, m_attributes.get(key).m_value.toString());
		}
		serializer.endTag("",  m_type.name());
		return serializer;
	}
	abstract public String SerializedGeometry();
	@Override
	public String toString()
	{
		return toWKT();
	}
	public abstract boolean isTouch(GIBounds point);
}
