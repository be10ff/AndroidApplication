package ru.tcgeo.application.gilib.planimetry;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

public class Vertex extends PointF implements GIGeometryObject
{
	public static final float delta = 0.1f;
	public Point m_morton_codes;
	public int _m_original;
	private int code;
	public String m_string;

	public Vertex clone()
	{
		Vertex res = new Vertex(this);
		return res;
	}
	public TYPE getType()
	{
		return TYPE.vertex;
	}
	
	public Vertex(Vertex p)
	{
		x = p.x;
		y = p.y;
		code = p.code;
		_m_original = p._m_original;
	}
	
	public Vertex(PointF p)
	{
		x = p.x;
		y = p.y;
		code = -1;
		_m_original = 0;
	}
	public Vertex(float coord_x, float coord_y)
	{
		x = coord_x;
		y = coord_y;
		code = -1;
		_m_original = 0;
	}
	
	public Vertex(PointF p, String string)
	{
		x = p.x;
		y = p.y;
		code = -1;
		m_string = string;
		_m_original = 0;
	}	

	@Override
	public boolean equals(Object o)
	{
		if (this == o) 
		{
			return true;
		}
		if (!(o instanceof Vertex))
		{
			return false;
		}
		Vertex obj = (Vertex)o;
		return (Math.abs(obj.x - this.x) < delta) && (Math.abs(obj.y - this.y) < delta);
	}
	public boolean GetOriginVisiblity()
	{
		if(code > 0)
		{
			return false;
		}
		return true;
	}
	public void SetOriginVisiblity(int  c)
	{
		code = c;
	}
	/**
	 * совпадают ли точки
	 * @param o интересующая точка
	 * @return true если координаты отличаются меньше чем на delta
	 */
	public boolean equals(PointF o)
	{
		return (Math.abs(o.x - this.x) < delta) && (Math.abs(o.y - this.y) < delta);		
	}	
	
	public double distanceTo(Vertex point)
	{
		return Math.sqrt(Math.pow((x - point.x), 2) + Math.pow((y - point.y), 2));
	}
	
	public static boolean IsPoint_C_Between_A_B(PointF a, PointF b, PointF c)
	{
		float minX = Math.min(a.x, b.x);
		float maxX = Math.max(a.x, b.x);		
		float minY = Math.min(a.y, b.y);
		float maxY = Math.max(a.y, b.y);		
		
		if( minX <= c.x  && c.x <= maxX && minY <= c.y && c.y <= maxY)
		{
			return true;
		}
		return false;
	}
	/*
	public static int getCode(PointF point, Rect rect)
	{
		int _code = 0;
		if(point.x < rect.left)
		{
			_code = _code | 8;//1xxx
		}
		if(point.x > rect.right)
		{
			_code = _code | 4;//x1xx
		}
		if(point.y < rect.top)
		{
			_code = _code | 2;//xx1x
		}
		if(point.y > rect.bottom)
		{
			_code = _code | 1;//xxx1
		}
		return _code;
	}
*/
	/**
	 *  возвращает код точки по Коэну-Сазерленду
	 *  для rect 
	 *  изключительное(строгие неравенства)
	 */
	public int getCode(RectF rect)
	{
		if(code == -1)
		{
			int _code = 0;
			if(x < rect.left)
			{
				_code = _code | 8;//1xxx
			}
			if(x > rect.right)
			{
				_code = _code | 4;//x1xx
			}
			if(y < rect.top)
			{
				_code = _code | 2;//xx1x
			}
			if(y > rect.bottom)
			{
				_code = _code | 1;//xxx1
			}
			code = _code;
			}
		return code;
	}
	/**
	 *  возвращает код точки по Коэну-Сазерленду
	 *  для rect 
	 *  включительное(нестрогие неравенства)
	 */
	public int getQuarte(RectF rect)
	{
		int _code = 0;
		if(x <= rect.left)
		{
			_code = _code | 8;//1xxx
		}
		if(x >= rect.right)
		{
			_code = _code | 4;//x1xx
		}
		if(y <= rect.top)
		{
			_code = _code | 2;//xx1x
		}
		if(y >= rect.bottom)
		{
			_code = _code | 1;//xxx1
		}
		return _code;
	}
	/*public Vertex projectOnRect_oldd(Vertex vertex, Rect rect)
	{
		code = getCode(rect);
		switch(code)
		{
			case 0:
				return vertex;
			case 1:
				//vertex.y = rect.bottom;
				return new Vertex(new PointF(vertex.x, rect.bottom));
			case 2:
				return new Vertex(new PointF(vertex.x, rect.top));				
			case 4:
				return new Vertex(new PointF(rect.right, vertex.y));		
			case 5:
				return new Vertex(new PointF(rect.right, rect.bottom));//
			case 6:
				return new Vertex(new PointF(rect.right, rect.top));//			
			case 8:
				return new Vertex(new PointF(rect.left, vertex.y));		
			case 9:
				return new Vertex(new PointF(rect.left, rect.bottom));//	
			case 10:
				return new Vertex(new PointF(rect.left, rect.top));//				
			default:
				return vertex;
		}
	}*/
    /** unchecked 
     * возвращает Vertex: vertex если vertex лежит внутри rect
     * лежащий на ближайшей стороне rect если может быть спроецирован на нее
     * совпадающий с ближайщей вершиной если может быть на сторону
     */
	public Vertex projectOnRect(Vertex vertex, RectF rect)
	{
		code = getCode(rect);
		switch(code)
		{
			case 0:
				vertex._m_original = 0;
				return vertex;
			case 1:
				vertex._m_original = 1;
				vertex.y = rect.bottom;
				return vertex;
			case 2:
				vertex._m_original = 2;
				vertex.y = rect.top;
				return vertex;
			case 4:
				vertex._m_original = 4;
				vertex.x = rect.right;
				return vertex;
			case 5:
				vertex._m_original = 5;
				vertex.x = rect.right;
				vertex.y = rect.bottom;
				return vertex;
			case 6:
				vertex._m_original = 6;
				vertex.x = rect.right;
				vertex.y = rect.top;
				return vertex;
			case 8:
				vertex._m_original = 8;
				vertex.x = rect.left;
				return vertex;
			case 9:
				vertex._m_original = 9;
				vertex.x = rect.left;
				vertex.y = rect.bottom;
				return vertex;
			case 10:
				vertex._m_original = 10;
				vertex.x = rect.left;
				vertex.y = rect.top;
				return vertex;
			default:
				vertex._m_original = 0;
				return vertex;
		}
	}
	public String toString()
	{
		return "[" + x + ", " + y + "]" + _m_original;
	}

	public RectF getBounds() 
	{
		return new RectF(x, y, x, y);
	}
	public Point getMortonCodes()
	{
		return m_morton_codes;
	}
	
	public void setMortonCodes(Point codes)
	{
		m_morton_codes = codes;
	}

}
