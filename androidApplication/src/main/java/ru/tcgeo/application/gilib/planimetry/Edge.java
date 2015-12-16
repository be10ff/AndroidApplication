package ru.tcgeo.application.gilib.planimetry;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

public class Edge implements GIGeometryObject
{
	public Point m_morton_codes;
	public ru.tcgeo.application.gilib.planimetry.Vertex m_start;
	public ru.tcgeo.application.gilib.planimetry.Vertex m_end;
	public PointF m_intersection_point;
	public RectF m_bounds;
	private ArrayList<Edge> m_text_bounds;
	//public ArrayList<Edge> m_excluded_parts;
	public int m_ID;
	public boolean m_unique;
	
	/**
	 * конструктор по вершинам начала и конца
	 * @param s начало
	 * @param e конец
	 */
	public Edge(ru.tcgeo.application.gilib.planimetry.Vertex s, ru.tcgeo.application.gilib.planimetry.Vertex e)
	{
		m_start = s;
		m_end = e;
		m_unique = true;
	}
	/**
	 * конструктор по точкам начала и конца
	 * @param s начало
	 * @param e конец
	 */
	public Edge(PointF s, PointF e)
	{
		m_start = new ru.tcgeo.application.gilib.planimetry.Vertex(s);
		m_end = new ru.tcgeo.application.gilib.planimetry.Vertex(e);
		m_unique = true;
	}
	/**
	 * конструктор по координатам начала и конца
	 * @param x1 начало
	 * @param y1 начало 
	 * @param x2 конец
	 * @param y2 конец
	 */
	public Edge(float x1, float y1, float x2, float y2)
	{
		m_start = new ru.tcgeo.application.gilib.planimetry.Vertex(new PointF(x1, y1));
		m_end = new ru.tcgeo.application.gilib.planimetry.Vertex(new PointF(x2, y2));
		m_unique = true;
	}
	
	/**unchecked  
	 * Два Edge отстоящие от this на delta  
	 * возвращает заранее посчитанное если оно не null
     */
	public ArrayList<Edge> getTextBounds(float delta)
	{
		if(m_text_bounds == null)
		{
			m_text_bounds = Offset(delta);
		}
		return m_text_bounds;
	}
	public Edge clone()
	{
		return new Edge(m_start.clone(), m_end.clone());
	}
	
	/**
	 * edge
	 */
	public TYPE getType()
	{
		return TYPE.edge;
	}
	/*	@Override
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
	 */
	public boolean equal(Object o)
	{
		if (this == o) 
		{
			return true;
		}
		if (!(o instanceof Edge))
		{
			return false;
		}
		Edge obj = (Edge)o;
		if(HasPointAsEnd(obj.m_start)&&HasPointAsEnd(obj.m_end))
		{
			return true;
		}
		return false;
	}
	/*public boolean isConcurrent(Edge edge)
	{
		if(!IsCoLinear(edge))
		{
			return false;
		}
		if()
		return false;
	}*/
	
	public void DrawGeometry(Canvas canvas, Paint paint)
	{
		canvas.drawLine(m_start.x, m_start.y, m_end.x, m_end.y, paint);
	}
	/**
	 * ограничивающий нормализованный RectF. 
	 */
	public RectF getBounds()
	{
		if(m_bounds == null)
		{
			float left = Math.min(m_start.x, m_end.x);
			float right = Math.max(m_start.x, m_end.x);
			float top = Math.min(m_start.y, m_end.y);
			float bottom = Math.max(m_start.y, m_end.y);
			m_bounds = new RectF(left, top, right, bottom);
		}
		return m_bounds;
	}
	
	public Point getMortonCodes()
	{
		return m_morton_codes;
	}
	
	public void setMortonCodes(Point codes)
	{
		m_morton_codes = codes;
	}
	
	public String toString()
	{
		return "(" + m_start.toString() + ", " + m_end.toString() + ")";
	}

	/**
	 * 
	 * @return true если длина меньше Vertex.delta
	 */
	public boolean isAboutEmpty()
	{
		return Math.abs(m_start.x - m_end.x) +  Math.abs(m_start.y - m_end.y) < ru.tcgeo.application.gilib.planimetry.Vertex.delta;
		
	}
	
	/**
	 * @return длина отрезка по Пифагору
	 */
	public float Lenght()
	{
		return (float) Math.sqrt(Math.pow((m_start.x - m_end.x), 2) + Math.pow((m_start.y - m_end.y), 2));
	}
	
	/**
	 * центральная точка
	 * @return
	 */
	ru.tcgeo.application.gilib.planimetry.Vertex center_point()
	{
		PointF point = new PointF((m_start.x + m_end.x)/2, (m_start.y + m_end.y)/2);
		return new ru.tcgeo.application.gilib.planimetry.Vertex(point);
	}
	
	/**
	 * сонаправленность с отрезком(вектором)
	 * @param edge вектор для сравнения
	 * @return true если cos угла между ними положителен
	 */
	boolean isInOrder(Edge edge)
	{
		//Edge a = this;
		//Edge b = edge;
		//float a_b = vectorX()*edge.vectorX() + vectorY()*edge.vectorY();
		//if(a_b > 0)
		if(Edge.scalarMultiplication(this, edge) > 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * меняет местами начало и конец отрезка
	 */
	public void Swap()
	{
		Vertex tmp = m_start;
		m_start = m_end;
		m_end = tmp;
	}

	/**
	 *  отсечение отрезка ограничивающим rect
	 *  по Коэну-Сазерленду
	 */
	public static Edge Clipping(Edge edge, RectF rect)
	{
		Vertex start = new Vertex(edge.m_start);
		Vertex end = new Vertex(edge.m_end);
		while(!((start.getCode(rect) == 0) && (end.getCode(rect) == 0)))
		{
			Edge current = new Edge(start, end);
			if((start.getCode(rect) & end.getCode(rect)) != 0)
			{
				//invisible
				return null;
			}
			if(current.isAboutEmpty())
			{
				return null;
			}
			if(start.getCode(rect) == 0)
			{
				Vertex tmp = start;
				start = end;
				end = tmp;
				current.Swap();
			}

			Vertex intersect = Edge.Intersection(current, rect);
			if(intersect == null)
			{
				return null;
			}
			if((intersect.getQuarte(rect) & start.getQuarte(rect)) != 0 )
			{
				start = intersect;
			}
			else
			{
				end = intersect;
			}
		}
		start.SetOriginVisiblity(0);
		end.SetOriginVisiblity(0);
		Edge res = new Edge(start, end);
		if(!res.isInOrder(edge))
		{
			res.Swap();
		}
		return res;
	}
	// by dividing in the middle
	public static Vertex Intersection_old(Edge edge, RectF rect)
	{
		Vertex start = new Vertex(edge.m_start);
		Vertex end = new Vertex(edge.m_end);
		Edge current;
		do
		{
			current = new Edge(start, end);
			Vertex med = current.center_point();
	
			if((start.getCode(rect) == 0) && (med.getCode(rect) == 0))
			{
				start = med;
			}
			if((end.getCode(rect) == 0) && (med.getCode(rect) == 0))
			{
				end = med;
			}
			if((start.getCode(rect) & med.getCode(rect)) != 0)
			{
				start = med;
			}
			if((end.getCode(rect) & med.getCode(rect)) != 0)
			{
				end = med;
			}
			if((start.getCode(rect) != 0) && (end.getCode(rect) != 0))
			{
				start = med;
			}
		} while(!current.isAboutEmpty());
		
		Vertex res = new Vertex(new PointF(Math.round(start.x), Math.round(start.y)));
		//Vertex res = new Vertex(new PointF((start.m_point.x), (start.m_point.y)));
		return res;
	}
	//by calculating
	/**
	 *  точка пересечения edge с ребрами rect
	 *  возвращается первая же. Коэн-Сазерленд.
	 */
	public static Vertex Intersection(Edge edge, RectF rect)
	{
		Vertex start = new Vertex(edge.m_start);
		PointF result = null;
		int origin_code = 0;
		int code = start.getCode(rect);
		
		Vertex left_top = new Vertex(new PointF(rect.left, rect.top));
		Vertex right_top = new Vertex(new PointF(rect.right, rect.top));
		Vertex right_bottom = new Vertex(new PointF(rect.right, rect.bottom));
		Vertex left_bottom = new Vertex(new PointF(rect.left, rect.bottom));
		
		Edge left = new Edge(left_bottom, left_top);
		Edge top = new Edge(left_top, right_top);
		Edge right = new Edge(right_top, right_bottom);
		Edge bottom = new Edge(right_bottom, left_bottom);
		
		switch(code)
		{
			case 1:
			{
				result = edge.intersectionAsEdgesStrong(bottom);
				if(result != null)
				{
					result = new PointF(Math.round(result.x), Math.round(result.y));
				}
				origin_code = 1;
				break;
			}
			case 2:
			{
				origin_code = 2;
				result = edge.intersectionAsEdgesStrong(top);
				if(result != null)
				{
					result = new PointF(Math.round(result.x), Math.round(result.y));
				}
				break;			
			}
			case 4:
			{
				origin_code = 3;
				result = edge.intersectionAsEdgesStrong(right);
				if(result != null)
				{
					result = new PointF(Math.round(result.x), Math.round(result.y));
				}
				break;			
			}
			case 5:
			{
				origin_code = 5;
				result = edge.intersectionAsEdgesStrong(right);
				if(result != null)
				{
					result = new PointF(Math.round(result.x), Math.round(result.y));
				}
				if(result == null)
				{
					result = edge.intersectionAsEdgesStrong(bottom);
					if(result != null)
					{
						result = new PointF(Math.round(result.x), Math.round(result.y));
					}
				}
				break;			
			}
			case 6:
			{
				origin_code = 6;
				result = edge.intersectionAsEdgesStrong(right);
				if(result != null)
				{
					result = new PointF(Math.round(result.x), Math.round(result.y));
				}
				if(result == null)
				{
					result = edge.intersectionAsEdgesStrong(top);
					if(result != null)
					{
						result = new PointF(Math.round(result.x), Math.round(result.y));
					}
				}
				break;			
			}
		
			case 8:
			{
				origin_code = 8;
				result = edge.intersectionAsEdgesStrong(left);
				if(result != null)
				{
					result = new PointF(Math.round(result.x), Math.round(result.y));
				}
				break;			
			}
			case 9:
			{
				origin_code = 9;
				result = edge.intersectionAsEdgesStrong(left);
				if(result != null)
				{
					result = new PointF(Math.round(result.x), Math.round(result.y));
				}
				if(result == null)
				{
					result = edge.intersectionAsEdgesStrong(bottom);
					if(result != null)
					{
						result = new PointF(Math.round(result.x), Math.round(result.y));
					}
				}
				break;			
			}
			case 10:
			{
				origin_code = 10;
				result = edge.intersectionAsEdgesStrong(left);
				if(result != null)
				{
					result = new PointF(Math.round(result.x), Math.round(result.y));
				}
				if(result == null)
				{
					result = edge.intersectionAsEdgesStrong(top);
					if(result != null)
					{
						result = new PointF(Math.round(result.x), Math.round(result.y));
					}
				}
				break;			
			}			
		}
		if(result != null)
		{
			Vertex res = new Vertex(result);
			res._m_original = origin_code;
			return res;
		}
		return null;
	}
	/**
	 * статический. точка пересечения с горизонталью Y
	 * @param edge отрезок для поиска точки
	 * @param y координата
	 * @return точка пересечения ; null если ее нет ; центр если отрезок лежит на горизонтали
	 */
	public static PointF EdgeIntersectionHorizontal(Edge edge, float y)
	{
		
		//if((edge.m_start.m_point.y == y)&&(edge.m_end.m_point.y == y))
		if((Math.abs(edge.m_start.y - y) < Vertex.delta) && (Math.abs(edge.m_end.y - y) < Vertex.delta))
		{
			return edge.center_point();
			//return new PointF(Math.max(edge.m_start.m_point.x, edge.m_end.m_point.x), y);
		}
		if(IsEdgeIntersectHorizontal(edge, y))
		{
			PointF a = edge.m_start;
			PointF b = edge.m_end;
			//Ax + By + C = 0
			float A = (a.y - b.y);
			float B = (b.x - a.x);
			float C = a.x*b.y - b.x*a.y;
			if(A != 0)
			{
				float x = (B*y + C)/(-1 * A);
				return new PointF(x, y);
			}
			else
			{
				return new PointF(Math.max(edge.m_start.x, edge.m_end.x), y);
			}
		}
		return null;
	}
	
	/**
	 * совпадает ли точка с одним из концов отрезка
	 * @param point интересующая точка
	 * @return да если совпадают с точностью Vertex.delta
	 */
	public boolean HasPointAsEnd(PointF point)
	{
		if(m_start.equals(point))
		{
			return true;
		}
		if(m_end.equals(point))
		{
			return true;
		}
		return false;
	}
	/**
	 * пересекает ли отрезок горизонталь
	 * @param edge интересующий отрезок
	 * @param y горизонталь
	 * @return true если концы отрезка лежат по разные стороны или на горизонтали
	 */
	public static boolean IsEdgeIntersectHorizontal(Edge edge, float y)
	{
		return (edge.m_start.y <= y &&  edge.m_end.y >= y) || (edge.m_start.y >= y &&  edge.m_end.y <= y);
	}
	/**
	 * пересекают ли отрезоки горизонталь
	 * @param a, b интересующие отрезоки
	 * @param y горизонталь
	 * @return true если концы отрезков лежат по разные стороны или на горизонтали включительно
	 */
	public static boolean IsCrossingHorizontal(Edge a, Edge b, float y)
	{
		if((a.m_start.y >= y && a.m_end.y >= y && b.m_start.y >= y && b.m_end.y >= y)|| (a.m_start.y <= y && a.m_end.y <= y && b.m_start.y <= y&& b.m_end.y <= y))
		{
			return false;
		}
		return true;
	}
	/**
	 * статический 
	 * @param a отрезок
	 * @param y горизонталь
	 * @return true если Y концов отстоит не больше чем на Vertex.delta от Y
	 */
	public static boolean IsCoincidesHorizontal(Edge a, float y)
	{
		if( (Math.abs(a.m_start.y - y) < Vertex.delta) && (Math.abs(a.m_end.y - y) < Vertex.delta) )
		{
			return true;
		}
		return false;
	}

	/**
	 * можно ли объеденить с отрезком. проверки на принадлежность одной линии нет. для горизонтальных отрезков
	 * @param edge интересующий отрезок
	 * @return 
	 */
	public boolean CanBeJoin(Edge edge)
	{
		if(!((Math.abs(edge.m_start.y - m_start.y) < Vertex.delta) && (Math.abs(edge.m_end.y - m_end.y) < Vertex.delta) && (Math.abs(edge.m_start.y - edge.m_end.y) < Vertex.delta)))
		{
			return false;
		}
		
		if((Math.max(edge.m_start.x, edge.m_end.x) + Vertex.delta < Math.min(m_start.x, m_end.x) - Vertex.delta)||(Math.min(edge.m_start.x, edge.m_end.x) -Vertex.delta > Math.max(m_start.x, m_end.x)+Vertex.delta))
		{
			return false;
		}
		return true;
	}
	
	/**
	 * объединение с отрезком 
	 * @param edge отрезок для объединения
	 * @return объединенный
	 */
	public Edge Join(Edge edge)
	{
		float left = Math.min(m_start.x, edge.m_start.x);
		left = Math.min(left, m_end.x);
		left = Math.min(left,  edge.m_end.x);
		
		float right = Math.max( m_end.x, edge.m_end.x);
		right = Math.max( right, m_start.x);		
		right = Math.max( right,  edge.m_start.x);		
		float y = m_start.y;
		return new Edge(new Vertex(new PointF(left, y)), new Vertex(new PointF(right, y)));
	}
	public static Edge LeftOfTwo(Edge a, Edge b)
	{
		if(a.CanBeJoin(b))
		{
			return a.Join(b);
		}
		else
		{
			if(a.m_end.x < b.m_start.x)
			{
				return a;
			}
			else
			{
				return b;
			}
		}
	}
	public static Edge RightOfTwo(Edge a, Edge b)
	{
		if(a.CanBeJoin(b))
		{
			return a.Join(b);
		}
		else
		{
			if(a.m_end.x > b.m_start.x)
			{
				return a;
			}
			else
			{
				return b;
			}
		}
	}
	public static Edge NearestToPointOfTwo(Edge a, Edge b, PointF point)
	{
		/*if(a.CanBeJoin(b))
		{
			return a.Join(b);
		}
		else
		{*/
			Vertex a_c = a.center_point();
			Vertex b_c = b.center_point();
			Vertex compare = new Vertex(point);
			if(compare.distanceTo(a_c) < compare.distanceTo(b_c))
			{
				return a;
			}
			else
			{
				return b;
			}
		//}
	}
	/**
	 * координата Х вектора отрезка
	 * @return m_end.x - m_start.x
	 */
	public static float vectorX(Edge edge)
	{
		return edge.m_end.x - edge.m_start.x;
	}
	/**
	 * координата Y вектора отрезка
	 * @return m_end.y - m_start.y
	 */
	public static float vectorY(Edge edge)
	{
		return edge.m_end.y -edge.m_start.y;
	}
	/**
	 * колинеарность отрезков
	 * @param edge отрезок для сравнения
	 * @return скалярное произведение < Vertex.delta
	 */
	public  boolean IsCoLinear(Edge edge)
	{
		//if(Math.abs(vectorX()*edge.vectorY() - vectorY()*edge.vectorX()) < Vertex.delta)
		if(Math.abs(Edge.vectorMultiplication(this, edge)) < Vertex.delta)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * включает ли в себя интересующий отрезок
	 * @param edge интересующий отрезок
	 * @return по скалярным координатам. (в параметрических проще)
	 */
	public boolean IsIncluded(Edge edge)
	{

		if(IsCoLinear(edge))
		{
			PointF a1 = m_start;
			PointF b1 = m_end;
			//Ax + By  = C
			float A1 = (a1.y - b1.y);
			float B1 = (b1.x - a1.x);
			float C1 = b1.x*a1.y - a1.x*b1.y;
			PointF a2 = edge.m_start;
			PointF b2 = edge.m_end;
			float A2 = (a2.y - b2.y);
			float B2 = (b2.x - a2.x);
			float C2 = b2.x*a2.y - a2.x*b2.y;
			
	
			if((Math.abs(B1) > Math.abs(A1))&&(Math.abs(B2) > Math.abs(A2)))
			//if((Math.abs(B1) > 0.001)&&(Math.abs(B2) > 0.001))
			{
				float Y1 = C1/B1;
				float Y2 = C2/B2;
				if(Math.abs(Y1 - Y2) < Vertex.delta)
				{
					if((a2.x > Math.min(a1.x, b1.x))&&(a2.x < Math.max(a1.x, b1.x)))
					{
						if((b2.x > Math.min(a1.x, b1.x)) && (b2.x < Math.max(a1.x, b1.x)))
						{
							return true;
						}
					}
				}
			}
			else //if((Math.abs(A1) > 0.001)&&(Math.abs(A2) > 0.001))
			{
				float X1 = C1/A1;
				float X2 = C2/A2;
				if(Math.abs(X1 - X2) < Vertex.delta)
				{
					if((a2.y > Math.min(a1.y, b1.y))&&(a2.y < Math.max(a1.y, b1.y)))
					{
						if((b2.y > Math.min(a1.y, b1.y)) && (b2.y < Math.max(a1.y, b1.y)))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
/**
 * проверка принадлежности точки отрезку
 * @param point проверяемая точка
 * @return true если точка point принадлежит отрезку c точностью   Vertex.delta
 */
	public boolean IsIncluded(PointF point)
	{
			PointF a = m_start;
			PointF b = m_end;
			PointF c = point;
			//Ax + By  = C
			float A = (a.y - b.y);
			float B = (b.x - a.x);
			float C = b.x*a.y - a.x*b.y;
			
			if(Math.abs(A*c.x + B*c.y - C) < Vertex.delta)
			{
				//if((Math.abs(B) > 0.001))
				if((Math.abs(B) > Math.abs(A)))
				{
					//if((c.x >= (Math.min(a.x, b.x) -0.1))&&(c.x <= (Math.max(a.x, b.x) + 0.1)))
					if((c.x > (Math.min(a.x, b.x) + Vertex.delta))&&(c.x < (Math.max(a.x, b.x) - Vertex.delta)))
					{
						return true;
					}
				}
				else //if((Math.abs(A) > 0.001))
				{
					//if((c.y >= (Math.min(a.y, b.y) - 0.1 ))&&(c.y <= (Math.max(a.y, b.y) + 0.1)))
					if((c.y > (Math.min(a.y, b.y) + Vertex.delta ))&&(c.y < (Math.max(a.y, b.y) - Vertex.delta)))
					{
						return true;
					}
				}
			}

		return false;
	}	
	public boolean IsAboutIncluded(PointF point)
	{
			PointF a = m_start;
			PointF b = m_end;
			PointF c = point;
			//Ax + By  = C
			float A = (a.y - b.y);
			float B = (b.x - a.x);
			float C = b.x*a.y - a.x*b.y;
			
			if(Math.abs(A*c.x + B*c.y - C) < Vertex.delta)
			{
				if((Math.abs(B) > Math.abs(A)))
				//if((Math.abs(B) > 0.0001))
				{
					if((c.x > (Math.min(a.x, b.x) - Vertex.delta))&&(c.x < (Math.max(a.x, b.x) + Vertex.delta)))
					{
						return true;
					}
				}
				else //if((Math.abs(A) > 0.0001))
				{
					if((c.y > (Math.min(a.y, b.y) - Vertex.delta ))&&(c.y < (Math.max(a.y, b.y) + Vertex.delta)))
					{
						return true;
					}
				}
			}

		return false;
	}	
	public boolean IsOnLine(Edge edge)
	{

		if(IsCoLinear(edge))
		{
			PointF a1 = m_start;
			PointF b1 = m_end;
			//Ax + By  = C
			float A1 = (a1.y - b1.y);
			float B1 = (b1.x - a1.x);
			float C1 = b1.x*a1.y - a1.x*b1.y;
			PointF a2 = edge.m_start;
			PointF b2 = edge.m_end;
			float A2 = (a2.y - b2.y);
			float B2 = (b2.x - a2.x);
			float C2 = b2.x*a2.y - a2.x*b2.y;
			
			
			//if((Math.abs(B1) > 0.001)&&(Math.abs(B2) > 0.001))
			if((Math.abs(B1) > Math.abs(A1))&&(Math.abs(B2) > Math.abs(A2)))
			{
				float Y1 = C1/B1;
				float Y2 = C2/B2;
				if(Math.abs(Y1 - Y2) < Vertex.delta)
				{
					return true;
				}
			}
			else //if((Math.abs(A1) > 0.001)&&(Math.abs(A2) > 0.001))
			{
				float X1 = C1/A1;
				float X2 = C2/A2;
				if(Math.abs(X1 - X2) < Vertex.delta)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public  ArrayList<Edge> Difference(Edge edge)
	{
		if(!IsOnLine(edge))
		{
			return null;
		}
		ArrayList<Edge> result = new ArrayList<Edge>();
		if( !isInOrder(edge))
		{
			edge.Swap();
		}
		//---------------------------------------------
		boolean start_in_edge = IsIncluded(edge.m_start);
		boolean end_in_edge = IsIncluded(edge.m_end);
		if(start_in_edge)
		{
			//1
			if(end_in_edge)
			{
				Edge part_start = new Edge(m_start, edge.m_start);
				Edge part_end = new Edge(edge.m_end, m_end);
				result.add(part_start);
				result.add(part_end);
				return result;
			}
			//7
			if(HasPointAsEnd(edge.m_end))
			{
				Edge one_summ = new Edge( m_start, edge.m_start);
				result.add(one_summ);
				return result;
			}
			//3
			//if(!IsIncluded(edge.m_end.m_point))
			Edge part_start = new Edge(m_start, edge.m_start);
			Edge part_end = new Edge(m_end, edge.m_end);
			result.add(part_start);
			result.add(part_end);
			return result;
		}
		if(end_in_edge)
		{
			//6
			if(HasPointAsEnd(edge.m_start))
			{
				Edge one_summ = new Edge(edge.m_end, m_end);
				result.add(one_summ);
				return result;
			}
			//2
			Edge part_start = new Edge(edge.m_start, m_start);
			Edge part_end = new Edge(edge.m_end, m_end);
			result.add(part_start);
			result.add(part_end);
			return result;
		}
		if(HasPointAsEnd(edge.m_start))
		{
			//5.1
			if(edge.IsIncluded(this.m_end))
			{
				Edge one_summ = new Edge(m_end, edge.m_end);
				result.add(one_summ);
				return result;
			}
			//5
			else
			{
				//10
				if(HasPointAsEnd(edge.m_end))
				{
					return new ArrayList<Edge>();
				}
				//5
				Edge one_summ = new Edge(m_start, edge.m_end);
				result.add(one_summ);
				return result;
			}
		}
		if(HasPointAsEnd(edge.m_end))
		{
			//4.1
			if(edge.IsIncluded(this.m_start))
			{
				Edge one_summ = new Edge(m_start, edge.m_start);
				result.add(one_summ);
				return result;
			}
			//4
			else
			{
				Edge one_summ = new Edge( edge.m_start, m_end);
				result.add(one_summ);
				return result;
			}
		}
		//8
		if(edge.IsIncluded(m_start) && edge.IsIncluded(m_end))
		{
			Edge part_start = new Edge(edge.m_start, m_start);
			Edge part_end = new Edge(m_end, edge.m_end);
			result.add(part_start);
			result.add(part_end);
			return result;
		}
		return null;
	}
	public  ArrayList<Edge> Substraction(Edge edge)
	{
		ArrayList<Edge> result = new ArrayList<Edge>();
		if(!IsOnLine(edge))
		{
			return null;
		}
		if(edge.Lenght() < Vertex.delta)
		{
			return null;
		}
		if( !isInOrder(edge))
		{
			edge.Swap();
		}
		//---------------------------------------------
		boolean start_in_edge = IsIncluded(edge.m_start);
		boolean end_in_edge = IsIncluded(edge.m_end);
		if(start_in_edge)
		{
			//1
			// |----------|
			//    |----|
			if(end_in_edge)
			{
				Edge part_start = new Edge(m_start, edge.m_start);
				Edge part_end = new Edge(edge.m_end, m_end);
				result.add(part_start);
				result.add(part_end);
				return result;
			}
			//7
			// |----------|
			//       |----|
			if(HasPointAsEnd(edge.m_end))
			{
				Edge one_summ = new Edge( m_start, edge.m_start);
				result.add(one_summ);
				result.add(null);
				return result;
			}
			//3
			// |----------|
			//         |----|
			//if(!IsIncluded(edge.m_end.m_point))
			Edge part_start = new Edge(m_start, edge.m_start);
			//Edge part_end = new Edge(m_end, edge.m_end);
			result.add(part_start);
			result.add(null);
			//result.add(part_end);
			return result;
		}
		if(end_in_edge)
		{
			//6
			// |----------|
			// |----|
			if(HasPointAsEnd(edge.m_start))
			{
				Edge one_summ = new Edge(edge.m_end, m_end);
				result.add(null);
				result.add(one_summ);
				return result;
			}
			//2
			//    |----------|
			// |----|
			//Edge part_start = new Edge(edge.m_start, m_start);
			Edge part_end = new Edge(edge.m_end, m_end);
			//result.add(part_start);
			result.add(null);
			result.add(part_end);
			return result;
		}
		if(HasPointAsEnd(edge.m_start))
		{
			//5.1
			// |-----|
			// |----------|
			if(edge.IsIncluded(this.m_end))
			{
				//Edge one_summ = new Edge(m_end, edge.m_end);
				//result.add(one_summ);
				result.add(null);
				result.add(null);
				return result;
				//return new ArrayList<Edge>();
			}
			//5
			// |----------||----|
			else
			{
				//10
				// |----------|
				// |----------|
				if(HasPointAsEnd(edge.m_end))
				{
					result.add(null);
					result.add(null);
					return result;
					//return new ArrayList<Edge>(); 
				}
				//5
				// |----------||----|
				//Edge one_summ = new Edge(m_start, edge.m_end);
				result.add(this);
				result.add(null);
				return result;
			}
		}
		if(HasPointAsEnd(edge.m_end))
		{
			//4.1
			//     |------|
			// |----------|
			if(edge.IsIncluded(this.m_start))
			{
				//Edge one_summ = new Edge(m_start, edge.m_start);
				//result.add(one_summ);
				result.add(null);
				result.add(null);
				return result;
				//return new ArrayList<Edge>();
			}
			//4
			// |---||----------|
			else
			{
				//Edge one_summ = new Edge( edge.m_start, m_end);
				result.add(null);
				result.add(this);
				return result;
			}
		}
		//8
		//    |----|
		// |----------|
		if(edge.IsIncluded(m_start) && edge.IsIncluded(m_end))
		{
			//Edge part_start = new Edge(edge.m_start, m_start);
			//Edge part_end = new Edge(m_end, edge.m_end);
			//result.add(part_start);
			//result.add(part_end);
			result.add(null);
			result.add(null);
			return result;
			//return new ArrayList<Edge>();
		}
		return null;
	}	
	/*public  ArrayList<Edge> Differenceold(Edge edge)
	{
		if(!IsOnLine(edge))
		{
			return null;
		}
		ArrayList<Edge> result = new ArrayList<Edge>();
		if( !isInOrder(edge))
		{
			edge.Swap();
		}
		//IsIncluded(edge)
		if(IsIncluded(edge.m_start) && IsIncluded(edge.m_end))
		{
			Edge part_start = new Edge(m_start, edge.m_start);
			Edge part_end = new Edge(edge.m_end, m_end);
			if(!part_start.isAboutEmpty())
			{
				result.add(part_start);
			}
			if(!part_end.isAboutEmpty())
			{
				result.add(part_end);
			}
			return result;
		}
		else
		{
			if(IsIncluded(edge.m_start))
			{
				//if((Math.abs(edge.m_start.x - m_end.x) < 0.1)&&(Math.abs(edge.m_start.y - m_end.y) < 0.1))
				//TODO
				if(m_end.equals(edge.m_start))
				{
					Edge one_summ = new Edge(m_start, edge.m_end);
					result.add(one_summ);
					return result;
				}
				Edge part_start = new Edge(m_start, edge.m_start);
				Edge part_end = new Edge(m_end, edge.m_end);
				if(!part_start.isAboutEmpty())
				{
					result.add(part_start);
				}
				if(!part_end.isAboutEmpty())
				{
					result.add(part_end);
				}
				return result;
			}
			if(IsIncluded(edge.m_end))
			{
				//if((Math.abs(edge.m_end.x - m_start.x) < 0.1)&&(Math.abs(edge.m_end.x - m_start.x) < 0.1))
				//TODO 
				if(m_start.equals(edge.m_end))
				{
					Edge one_summ = new Edge(edge.m_start, edge.m_end);
					result.add(one_summ);
					return result;
				}

				Edge part_end = new Edge(edge.m_end, m_end);
				Edge part_start = new Edge(edge.m_start, m_start);
				if(!part_start.isAboutEmpty())
				{
					result.add(part_start);
				}
				if(!part_end.isAboutEmpty())
				{
					result.add(part_end);
				}
				return result;
			}
		}
		if(edge.IsIncluded(m_start) && edge.IsIncluded(m_end))
		{
			Edge part_start = new Edge(edge.m_start, m_start);
			Edge part_end = new Edge(m_end, edge.m_end);
			if(!part_start.isAboutEmpty())
			{
				result.add(part_start);
			}
			if(!part_end.isAboutEmpty())
			{
				result.add(part_end);
			}
			return result;
		}

		if(!IsIncluded(edge.m_start) && !IsIncluded(edge.m_end))
		{
			if(!isAboutEmpty())
			{
				result.add(this);
			}
			if(!edge.isAboutEmpty())
			{
				result.add(edge);
			}
			return null;
		}
		return null;
	}*/


	/**
	 *  точка включающего пересечения линий содержащих this и edge. 
	 *  null если почти паралельны
	 *  (Math.abs(Determinant) < 0.0001)
	 */
	public PointF IntersectionAsLines(Edge edge)
	{
		PointF a1 = m_start;
		PointF b1 = m_end;
		//Ax + By  = C
		float A1 = (a1.y - b1.y);
		float B1 = (b1.x - a1.x);
		float C1 = b1.x*a1.y - a1.x*b1.y;
		
		PointF a2 = edge.m_start;
		PointF b2 = edge.m_end;
		//Ax + By  = C
		float A2 = (a2.y - b2.y);
		float B2 = (b2.x - a2.x);
		float C2 = b2.x*a2.y - a2.x*b2.y;
		
		float Determinant = A1*B2 - A2*B1;
		
		if(Math.abs(Determinant) < 0.0001)
		{
			return null;
		}
		
		float DeterminantX = C1*B2 - C2*B1;
		float DeterminantY = A1*C2 - A2*C1;
		
		float x = DeterminantX/Determinant;
		float y = DeterminantY/Determinant;

		PointF res = new PointF(x, y);
		
		return res;
	}
	public PointF intersectionAsEdgesSmooth(Edge edge)
	{
		PointF res = intersectionAsEdges(edge);
		if(res != null)
		{
			if(edge.IsAboutIncluded(res)&&IsAboutIncluded(res))
			{
				return res;
			}
		}
		return null;
		
	}
	/*public PointF intersectionAsEdgesSmoothRound(Edge edge)
	{
		PointF res = intersectionAsEdgesSmooth(edge);
		if(res != null)
		{
			float x_round = Math.round(res.x);
			float y_round = Math.round(res.y);
			PointF result = new PointF(x_round, y_round);
			return result;
		}
		return null;
		
	}*/
	/**
	 *  точка исключительного пересечения this  с edge. 
	 * @param edge 
	 * @return точка исключительного пересечения this  с edge.
	 * null если такой нет (или она совпадает с концом одного из отрезков)
	 */
	public PointF intersectionAsEdgesStrong(Edge edge)
	{
		PointF res = intersectionAsEdges(edge);
		if(res != null)
		{
			if(edge.IsIncluded(res)&&IsIncluded(res))
			{
				return res;
			}
		}
		return null;
	}
	/*public PointF intersectionAsLinesRound(Edge edge)
	{
		PointF res = IntersectionAsLines(edge);

		if(res != null)
		{
			float x_round = Math.round(res.x);
			float y_round = Math.round(res.y);
			PointF result = new PointF(x_round, y_round);
			return result;
		}
		return null;
	}*/
	/**
	 *  точка включающего пересечения this  с edge. 
	 *  null если такой нет 
	 */
	private PointF intersectionAsEdges(Edge edge)
	{
		//PointF a1 = m_start;
		//PointF b1 = m_end;
		//PointF a2 = edge.m_start;
		//PointF b2 = edge.m_end;
		//RectF bounds = new RectF(Math.min(a1.x, b1.x) - Vertex.delta, Math.min(a1.y, b1.y) - Vertex.delta, Math.max(a1.x, b1.x) + Vertex.delta, Math.max(a1.y, b1.y) + Vertex.delta);
		/*if(!bounds.intersects(Math.min(a2.x, b2.x) - Vertex.delta, Math.min(a2.y, b2.y) - Vertex.delta, Math.max(a2.x, b2.x) + Vertex.delta, Math.max(a2.y, b2.y) + Vertex.delta))
		{
			return null;		
		}*/
		PointF check = IntersectionAsLines(edge);

		return check;
	}
	public double AngleBetween(Edge edge)
	{
		float Ax = vectorX(this);
		float Ay = vectorY(this);
		float Bx = Edge.vectorX(edge);
		float By = Edge.vectorY(edge);
		float cosA = (Ax*Bx +Ay*By)/(Lenght()*edge.Lenght());
		double angle = Math.acos(cosA);
		return angle;
	}
	
	public float Tan()
	{
		PointF a1 = m_start;
		PointF b1 = m_end;
		//Ax + By  = C
		float A1 = (a1.y - b1.y);
		float B1 = (b1.x - a1.x);
		float res;
		if(B1 != 0)
		{
			res = A1/B1;
		}
		else
		{
			if(b1.y > a1.y)
			{
				res = 1000000;
			}
			else
			{
				res = -1000000;
			}
		}
		return res;
	}  
	
	/**unchecked  
	 * Два Edge отстоящие от this на delta  
     */
	private ArrayList<Edge> Offset(float delta)
	{
		ArrayList<Edge> result = new ArrayList<Edge>();
		double alpha = Math.atan(Tan());
		PointF s1 = new PointF(); 
		PointF s2 = new PointF(); 
		PointF e1 = new PointF(); 
		PointF e2 = new PointF(); 
		
		s1.x = (float) (m_start.x + delta*Math.sin(alpha));
		s1.y = (float) (m_start.y + delta*Math.cos(alpha));

		s2.x = (float) (m_start.x - delta*Math.sin(alpha));
		s2.y = (float) (m_start.y - delta*Math.cos(alpha));
		
		e1.x = (float) (m_end.x + delta*Math.sin(alpha));
		e1.y = (float) (m_end.y + delta*Math.cos(alpha));

		e2.x = (float) (m_end.x - delta*Math.sin(alpha));
		e2.y = (float) (m_end.y - delta*Math.cos(alpha));
		
		Edge one = new Edge( new Vertex(s1), new Vertex (e1));
		result.add(one);
		one = new Edge( new Vertex(s2), new Vertex (e2));
		result.add(one);		
		m_text_bounds = result;
		return result;
	}
	public PointF ProjectionPointOnEdge(PointF point)
	{
		PointF res = ProjectionPointOnLine(point);
		if(res != null)
		{
			if(IsAboutIncluded(res))
			{
				return res;
			}
		}
		return null;
	}
	public PointF ProjectionPointOnLine(PointF point)
	{
		PointF a1 = m_start;
		PointF b1 = m_end;
		PointF c = point;
		
		//Ax + By  = C
		float A1 = (a1.y - b1.y);
		float B1 = (b1.x - a1.x);
		float C1 = b1.x*a1.y - a1.x*b1.y;
		
		//float ax = b1.x - a1.x;
		//float ay = b1.y - a1.y;
		
		
		//Ax + By  = C  прямая перпендикулярная к 1 через с
		float A2 = b1.x - a1.x;
		float B2 = b1.y - a1.y;
		float C2 = (b1.x - a1.x)*c.x +( b1.y - a1.y)*c.y;
		
		float Determinant = A1*B2 - A2*B1;
		
		if(Math.abs(Determinant) < 0.1)
		{
			return null;
		}
		
		float DeterminantX = C1*B2 - C2*B1;
		float DeterminantY = A1*C2 - A2*C1;
		
		float x = DeterminantX/Determinant;
		float y = DeterminantY/Determinant;
		
		PointF res = new PointF(x, y);

		return res;

	}
	public PointF WitchPointOfEdgeHasProjectedAsPointOnThisEdge(Edge edge, PointF point)
	{
		//PointF intersection = IntersectionAsLines(edge);
		
		PointF a1 = m_start;
		PointF b1 = m_end;
		//Ax + By  = C
		//original line
		float A1 = (a1.y - b1.y);
		float B1 = (b1.x - a1.x);
		float C1 = b1.x*a1.y - a1.x*b1.y;
		
		PointF a2 = edge.m_start;
		PointF b2 = edge.m_end;
		//Ax + By  = C
		//original line of edge
		float A2 = (a2.y - b2.y);
		float B2 = (b2.x - a2.x);
		float C2 = b2.x*a2.y - a2.x*b2.y;
		
		//perpendicular for line
		float Ap = (b1.x - a1.x);
		float Bp = (b1.y - a1.y);
		float Cp = (b1.x - a1.x)*point.x + (b1.y - a1.y)*point.y;
		
		//temp res
		float Determinant = A2*Bp - Ap*B2;
		if(Math.abs(Determinant) < 0.1)
		{
			return null;
		}
		
		float DeterminantX = C2*Bp - Cp*B2;
		float DeterminantY = A2*Cp - Ap*C2;
		
		float x = DeterminantX/Determinant;
		float y = DeterminantY/Determinant;

		PointF temp_res = new PointF(x, y);
		
		//perpendicular for edge's line
		float App = (b2.x - a2.x);
		float Bpp = (b2.y - a2.y);
		float Cpp = (b2.x - a2.x)*temp_res.x + (b2.y - a2.y)*temp_res.y;
		
		//result
		Determinant = A1*Bpp - App*B1;
		if(Math.abs(Determinant) < 0.1)
		{
			return null;
		}
		
		DeterminantX = C1*Bpp - Cpp*B1;
		DeterminantY = A1*Cpp - App*C1;
		
		x = DeterminantX/Determinant;
		y = DeterminantY/Determinant;

		PointF result = new PointF(x, y);
		return result;
	}
	public PointF PointOfBoundaryIntrsection(Edge edge, Edge bound, float height)
	{
		//PointF OfAxes =  intersectionAsEdgesSmooth(edge);
		
		PointF OfAxes =  IntersectionAsLines(edge);
		if(OfAxes == null)
		{
			return null;
		}
		PointF OfAxeNBound =  intersectionAsEdgesSmooth(bound);
		if(OfAxeNBound == null)
		{
			return null;
		}
		Edge vector = new Edge(new Vertex(OfAxes), new Vertex(OfAxeNBound));
		double angle = AngleBetween(edge); 
		int sign = 1;
		if(angle <= Math.PI/2)
		{
			sign = -1;
		}
		Edge res = vector.VectorXk((float)(1 - sign*height/(Math.tan(angle)*vector.Lenght())));
		//OfAxeNBound.x = OfAxeNBound.x + vector.vectorX();
		//OfAxeNBound.y = OfAxeNBound.y + vector.vectorY();
		
		//PointF result = new PointF(OfAxeNBound.x + vector.vectorX(), OfAxeNBound.y + vector.vectorY());
		PointF result = res.m_end;
		
		return result;
	}
	public Edge VectorFromStart(PointF start, Edge vector)
	{
		float x = start.x + Edge.vectorX(vector);
		float y = start.y + Edge.vectorY(vector);
		PointF end = new PointF(x, y);
		Edge res = new Edge(new Vertex(start), new Vertex(end));
		return res;
	}
	public Edge VectorXk(float k)
	{
		float x = m_start.x + vectorX(this)*k;
		float y = m_start.y + vectorY(this)*k;
		PointF end = new PointF(x, y);
		Edge res = new Edge(m_start, new Vertex(end));
		return res;
	}
	
	/**векторная сумма  */
	public Edge VerctorSumm(Edge edge)
	{
		return new Edge(new PointF(0, 0), new PointF(vectorX(this) + Edge.vectorX(edge), vectorY(this) + Edge.vectorY(edge)));
	}
	
	/**векторная разность this - edge  */
	public Edge VerctorDiff(Edge edge)
	{
		return new Edge(new PointF(0, 0), new PointF(vectorX(this) - Edge.vectorX(edge), vectorY(this) - Edge.vectorY(edge)));
	}
	
    /**unchecked  
     * Определяется пересечение прямоугольника с медианой совпадающей с отрезком и высотой 2*offset
     */
	public boolean IsOffsetedIntersect(Edge edge, float offset)
	{
		/*if(intersectionAsEdgesSmooth(edge) != null)
		{
			return true;
		}
		
		ArrayList<Edge> current = getTextBounds(offset);
		ArrayList<Edge> compare = edge.getTextBounds(offset);
		
		float one_left = Math.min(Math.min(current.get(0).m_start.x, current.get(0).m_end.x), Math.min(current.get(1).m_start.x, current.get(1).m_end.x));
		float one_top = Math.min(Math.min(current.get(0).m_start.y, current.get(0).m_end.y), Math.min(current.get(1).m_start.y, current.get(1).m_end.y));
		float one_right = Math.max(Math.max(current.get(0).m_start.x, current.get(0).m_end.x), Math.max(current.get(1).m_start.x, current.get(1).m_end.x));
		float one_bottom = Math.max(Math.max(current.get(0).m_start.y, current.get(0).m_end.y), Math.max(current.get(1).m_start.y, current.get(1).m_end.y));
		
		float other_left = Math.min(Math.min(compare.get(0).m_start.x, compare.get(0).m_end.x), Math.min(compare.get(1).m_start.x, compare.get(1).m_end.x));
		float other_top = Math.min(Math.min(compare.get(0).m_start.y, compare.get(0).m_end.y), Math.min(compare.get(1).m_start.y, compare.get(1).m_end.y));
		float other_right = Math.max(Math.max(compare.get(0).m_start.x, compare.get(0).m_end.x), Math.max(compare.get(1).m_start.x, compare.get(1).m_end.x));
		float other_bottom = Math.max(Math.max(compare.get(0).m_start.y, compare.get(0).m_end.y), Math.max(compare.get(1).m_start.y, compare.get(1).m_end.y));
		
		RectF bounds = new RectF(one_left, one_top, one_right, one_bottom);
		

		if(!bounds.intersects(other_left, other_top, other_right, other_bottom))
		{
			return false;		
		}
		*/

		GIGeometryPolygon fig_curr = GetOffsetGeomerty(offset);
		GIGeometryPolygon fig_comp = edge.GetOffsetGeomerty(offset);
		for(int i = 0; i < fig_curr.m_points.size(); i++)
		{
			if(fig_comp.IncludePoint(fig_curr.m_points.get(i)))
			{
				return true;
			}
		}
		for(int i = 0; i < fig_comp.m_points.size(); i++)
		{
			if(fig_curr.IncludePoint(fig_comp.m_points.get(i)))
			{
				return true;
			}
		}
		return false;
	}
	/*public PointF PerpendicularBoundIntersection_old(ArrayList<Edge> boudaries, int need_index, float height) 
	{
		Edge lookig = boudaries.get(need_index);
		Edge first = new Edge(boudaries.get(0).m_start, boudaries.get(1).m_start);
		Edge second = new Edge(boudaries.get(0).m_end, boudaries.get(1).m_end);
		PointF point_A = null;
		PointF point_B = null;
		PointF pp =	intersectionAsEdgesSmooth(first);
		if(pp != null)
		{
			point_A = pp;
			point_B = lookig.m_start;
		}
		else
		{
			pp =	intersectionAsEdgesSmooth(second);
			if(pp != null)
			{
				point_A = pp;
				point_B = lookig.m_end;
			}
		}
		if(point_A == null)
		{
			return null;
		}
		PointF point_C = ProjectionPointOnEdge(point_B);
		if(point_C == null)
		{
			return null;
		}
		Edge edge_CB = new Edge(new Vertex(point_C), new Vertex(point_B));
		float Kf = height/edge_CB.Lenght();
		Edge edge_AC = new Edge(new Vertex(point_A), new Vertex(point_C));
		Edge res = edge_AC.VectorXk(Kf);
		//PointF result = res.m_end;

		
		return null;
	}*/
	/**
	 * часть исходного отрезка, помещающаяся в полигон с учетом высоты прямоугольника, построенного на нем как на медиане
	 * @param geometry произвольный выпуклый полигон, заданный упорядоченным массивом точек
	 * @param delta половина высоты прямоугольника, построенного на отрезке как медиане 
	 * @return 
	 */
	public Edge IncludedEdgeByGeometry(ArrayList<Vertex> geometry, float delta)
	{
		ArrayList<Edge> boudaries = getTextBounds(delta);
		//начальная и конечная точки
		//PointF max = null;
		//PointF min = null;
		//начальная и конечная параметрические координаты
		float t_min = -1;
		float t_max = -1;
		//ищем пересечения с основаниями ограничивающего прямоугольника
		for(int i = 0; i < boudaries.size(); i++)
		{
			Edge current = boudaries.get(i);
			float[] coords =  current.ParametricCoordsOfSectionByPolygon(geometry);
			if(coords != null)
			{
			//выбираем минимум для начала и максимум для конца. наибольшее отсечение
				if((coords[0] > t_min)||((t_min == -1)))
				{
					t_min = coords[0];
				}
				if((coords[1] < t_max)||(t_max == -1))
				{
					t_max = coords[1];
				}
			}
			else
			{
				return null;
			}
		}
		if((t_min != -1)&&(t_max != -1))
		{
			return ParametricalPartOfEdge(t_min, t_max);
		}
		return null;
	}
	/**
	 * отрезок для исключения из исходного с учетом высоты прямоугольника, построенного на нем как на медиане
	 * @param geometry произвольный выпуклый полигон, заданный упорядоченным массивом точек
	 * @param delta половина высоты прямоугольника, построенного на отрезке как медиане 
	 * @return 
	 */
	public Edge ExcludingEdgeByGeometry(ArrayList<Vertex> geometry, float delta)
	{
		ArrayList<Edge> boudaries = getTextBounds(delta);
		//начальная и конечная точки
		//PointF max = null;
		//PointF min = null;
		//начальная и конечная параметрические координаты
		float t_min = -1;
		float t_max = -1;
		//ищем пересечения с основаниями ограничивающего прямоугольника
		for(int i = 0; i < boudaries.size(); i++)
		{
			Edge current = boudaries.get(i);
			float[] coords =  current.ParametricCoordsOfSectionByPolygon(geometry);
			if(coords != null)
			{
			//выбираем минимум для начала и максимум для конца. наибольшее отсечение
				if((coords[0] < t_min)||((t_min == -1)))
				{
					t_min = coords[0];
				}
				if((coords[1] > t_max)||(t_max == -1))
				{
					t_max = coords[1];
				}
			}
		}
		if((t_min != -1)&&(t_max != -1))
		{
			return ParametricalPartOfEdge(t_min, t_max);
		}
		return null;
	}
	
	/**
	 * отрезок для исключения из исходного с учетом высоты прямоугольника, построенного на нем как на медиане
	 * @param geometry произвольный выпуклый полигон, заданный упорядоченным массивом точек
	 * @param delta половина высоты прямоугольника, построенного на отрезке как медиане 
	 * @return 
	 */
	public float[] ExcludingCoordsByGeometry(ArrayList<Vertex> geometry, float delta) 
	{
		ArrayList<Edge> boudaries = getTextBounds(delta);
		//начальная и конечная точки
		//PointF max = null;
		//PointF min = null;
		//начальная и конечная параметрические координаты
		float t_min = -1;
		float t_max = -1;
		//ищем пересечения с основаниями ограничивающего прямоугольника
		for(int i = 0; i < boudaries.size(); i++)
		{
			Edge current = boudaries.get(i);
			float[] coords =  current.ParametricCoordsOfSectionByPolygon(geometry);
			if(coords != null)
			{
			//выбираем минимум для начала и максимум для конца. наибольшее отсечение
				if((coords[0] < t_min)||((t_min == -1)))
				{
					t_min = coords[0];
				}
				if((coords[1] > t_max)||(t_max == -1))
				{
					t_max = coords[1];
				}
			}
		}
		if((t_min != -1)&&(t_max != -1))
		{
			float[] res = new float[2];
			res[0] = t_min;
			res[1] = t_max;
			return res;
		}
		return null;
	}
	
	/**
	 * .
	 * @param geometry произвольный выпуклый полигон, заданный упорядоченным массивом точек
	 * @param delta половина высоты прямоугольника, построенного на отрезке как медиане 
	 * @return 
	 */
	public Edge ExcludingEdgeByGeometry_old(GIGeometryPolygon geometry, float delta)
	{
		ArrayList<Edge> boudaries = getTextBounds(delta);
		boudaries.add(new Edge(boudaries.get(0).m_start, boudaries.get(1).m_start ));
		boudaries.add(new Edge(boudaries.get(0).m_end, boudaries.get(1).m_end ));
		ArrayList<Edge> edges = geometry.MakeEdgesRing();
		//GIGeometryPolygon current_polygon = GetOffsetGeomerty(delta);
		//ищем попавшие в область вершины
		PointF max = null;
		PointF min = null;
		if(geometry.IncludePoint(boudaries.get(0).m_start) || geometry.IncludePoint(boudaries.get(1).m_start))
		{
			min = m_start;
		}

		if(geometry.IncludePoint(boudaries.get(0).m_end) || geometry.IncludePoint(boudaries.get(1).m_end))
		{
			max = m_end;
		}
		
		//точки пересечения границ текстовой области с ребрами геометрии
		for(int i = 0; i < boudaries.size(); i++)
		{
			Edge current = boudaries.get(i);
			for(int j = 0; j < edges.size(); j++)
			{
				Edge compare = edges.get(j);
 				PointF point = current.intersectionAsEdgesSmooth(compare);
				if(point != null)
				{
					PointF projection = ProjectionPointOnLine(point);
					if(projection != null)
					{
						if((max == null)||(ParametricCoordOfPoint(projection) > ParametricCoordOfPoint(max)))
						{
							max = projection;
						}
						if((min == null)||(ParametricCoordOfPoint(projection) < ParametricCoordOfPoint(min)))
						{
							min = projection;
						}
					}
				}
			}
			
		}
		if((max != null) && (min != null))
		{
			Edge result = new Edge(new Vertex(min), new Vertex(max));
			return result;
		}
		return null;
	}
	public Edge ExcludingEdgeByGeometry_old_old(GIGeometryPolygon geometry, float delta)
	{
		ArrayList<Edge> boudaries = getTextBounds(delta);
		boudaries.add(new Edge(boudaries.get(0).m_start, boudaries.get(1).m_start ));
		boudaries.add(new Edge(boudaries.get(0).m_end, boudaries.get(1).m_end ));
		ArrayList<Edge> edges = geometry.MakeEdgesRing();

		PointF max = null;
		PointF min = null;
		if(geometry.IncludePoint(m_start))
		{
			min = m_start;
		}

		if(geometry.IncludePoint(m_end))
		{
			max = m_end;
		}
		
		//точки пересечения границ текстовой области с ребрами геометрии
		for(int i = 0; i < boudaries.size(); i++)
		{
			Edge current = boudaries.get(i);
			for(int j = 0; j < edges.size(); j++)
			{
				Edge compare = edges.get(j);
 				PointF point = current.intersectionAsEdgesSmooth(compare);
				if(point != null)
				{
					PointF projection = ProjectionPointOnLine(point);
					if(projection != null)
					{
						if((max == null)||(ParametricCoordOfPoint(projection) > ParametricCoordOfPoint(max)))
						{
							max = projection;
						}
						if((min == null)||(ParametricCoordOfPoint(projection) < ParametricCoordOfPoint(min)))
						{
							min = projection;
						}
					}
				}
			}
			
		}
		if((max != null) && (min != null))
		{
			Edge result = new Edge(new Vertex(min), new Vertex(max));
			return result;
		}
		return null;
	}
    /**unchecked  
     * параметрическая координата точки на прямой проходящей через отрезок. 
     * 	0 соотвествует началу отрезка.
     * 	1 соотвествует концу отрезка
     */	
	public float ParametricCoordOfPoint(PointF point)
	{
		//параметрическое уравнение прямой
		// x = lt+x0
		// y = mt+y0 
		float t = 0;
		
		float x0 = m_start.x;
		float y0 = m_start.y;
		float l = vectorX(this);
		float m = vectorY(this);
		
		if(l != 0)
		{
			t = (point.x - x0)/l;
		}
		else if(m != 0)
		{
			t = (point.y - y0)/m;
		}
		return t;
	}
	
    /**about old  
     * замкнутый прямоугольник, имеющий медианой начальный отрезок и высотой 2*delta 
     */
	public GIGeometryPolygon GetOffsetGeomerty(float delta)
	{
		ArrayList<Edge> offset = getTextBounds(delta);
		GIGeometryPolygon result = new GIGeometryPolygon();
		result.add(offset.get(0).m_start);
		result.add(offset.get(0).m_end);
		result.add(offset.get(1).m_end);
		result.add(offset.get(1).m_start);
		result.add(offset.get(0).m_start);
		//result.MakeEdgesRing();
		result.MakeCC();
		return result;
	}
    /**unchecked  
     * замкнутый прямоугольник, имеющий медианой начальный отрезок и высотой 2*delta 
     */
	public ArrayList<Vertex> GetOffsetVertexes(float delta)
	{
		//ArrayList<Edge> offset = Offset(delta);
		ArrayList<Edge> offset = getTextBounds(delta);
		ArrayList<Vertex> result = new ArrayList<Vertex>();
		result.add(offset.get(0).m_start);
		result.add(offset.get(0).m_end);
		result.add(offset.get(1).m_end);
		result.add(offset.get(1).m_start);
		result.add(offset.get(0).m_start);
		
		return result;
	}
	
	/**
	 * разбивает исходный отрезок, отнимая все участки перечисленные в массиве excluded_parts
	 * @return исходный отрезок если excluded_parts null или пустой, массив оставшихся отрезков в другом случае
	 * 
	 */
	public ArrayList<Edge> ExcludeAll(ArrayList<Edge> excluded_parts)
	{
		ArrayList<Edge> result = new ArrayList<Edge>();
		result.add(this);
		if(excluded_parts == null)
		{
			return result;
		}
		for(int i = 0; i < excluded_parts.size(); i++)
		{
			Edge to_exclude = excluded_parts.get(i);
			boolean happends = false;
			int edge_counter = 0;
			while(edge_counter < result.size())
			{
				Edge current_edge = result.get(edge_counter);
				ArrayList<Edge> substraction = current_edge.Substraction(to_exclude);
				//TODO
				if(substraction != null)
				{
	
					Edge start_part = substraction.get(0);
					Edge end_part = substraction.get(1);
					//из одного edge получаем два
					if(end_part != null)
					{
						end_part.m_ID = current_edge.m_ID;
						result.add(edge_counter, end_part);
					}
					if(start_part != null)
					{
						start_part.m_ID = current_edge.m_ID;
						result.add(edge_counter, start_part);
					}
					result.remove(current_edge);
					//новые path добавили, старый удалили
					happends = true;
					break;
				}
				if(!happends)
				{
					edge_counter++;
				}
			}
		}
		return result;
	}
	
	/**
	 * Сечение отрезка выпуклым произвольным замкнутым многоугольником по Кириусу-Беку
	 * @param polygon произвольный выпуклый полигон заданный упорядоченным  массивом точек.
	 * @return пара, мин и макс, параметрических координат отрезка внутри полигона. null если отрезок лежит целиком снаружи.
	 * 
	 */
	private float[] ParametricCoordsOfSectionByPolygon(ArrayList<Vertex> polygon )
	{
		//инициализируем результирующие точки пересечения
		float t_min = 0; // нижний предел в параметрических координатах
		float t_max = 1; // верхний -//-
		boolean inside = true;
		float[] res = new float[2];
		for(int i = 0; i < polygon.size() - 1; i++)
		{
			//this - секомый отрезок
			Edge P = this;
			//R текущее ребро 
			Edge Fi = new Edge(polygon.get(i), polygon.get(i+1));
			//нормаль к ребру
			//Edge Ni = Fi.VectorNormal();
			Edge Ni = Edge.VectorNormal(Fi);
			//скалярное произведение . определяет взаимную ориентацию отрезка и ребра
			//float pi = P.scalarMultiplication(Ni);
			float pi = Edge.scalarMultiplication(P, Ni);
			//вектор Qt = V(t) - F начинающийся в начальной точке ребра окна и заканчивающийся в некоторой точке V(t) удлиненной линии.
			//Edge Qt = P.VerctorDiff(Fi);
			Edge Qt = new Edge(polygon.get(i), P.m_start);
			//скалярное произведение  Qt*Ni
			float qi = Edge.scalarMultiplication(Qt, Ni);
			
			//вырожден в точку либо паралелен стороне
			if(pi == 0)
			{
				//включительно : "<" 
				//исключительно : "<="
				if(qi < 0)
				{
					//вне окна/невидим
					return null;
				}
			}
			else
			{
				//параметрическая координата пересечения
				float t = -1*(qi/pi);
				//точка пересечения в пределах отрезка

				//с внутренней стороны на внешнюю
				if(pi < 0) 
				{
					if(t < t_min)
					{
						inside = false;
						break;
					}
					if(t < t_max)
					{
						
						t_max = t;
					}
				}
				//с внешней на внутренюю
				else
				{
					if(t > t_max)
					{
						inside = false;
						break;
					}
					if(t > t_min)
					{
						t_min = t;
					}
				}
			}
		}
		if(inside)
		{
			res[0] = t_min;
			res[1] = t_max;
			return res;
		}
		return null;
	}
	/**
	 * пересечение двух отрезков
	 */
	public static boolean ParametricIntersection(Edge one, Edge other)
	{
		
		
		return ParametricIntersection(one.m_start, one.m_end, other.m_start, other.m_end);
	}
	/**
	 * пересечение двух отрезков
	 */
	public static boolean ParametricIntersection(PointF start1, PointF end1, PointF start2, PointF end2)
	{
		PointF dir1 = new PointF(end1.x - start1.x, end1.y - start1.y);
		PointF dir2 = new PointF(end2.x - start2.x, end2.y - start2.y);
		
		
		float a1 = -dir1.y;
		float b1 = +dir1.x;
		float d1 = -(a1*start1.x + b1*start1.y);
		
		float a2 = -dir2.y;
		float b2 = +dir2.x;
		float d2 = -(a2*start2.x + b2*start2.y);
		
        float seg1_line2_start = a2*start1.x + b2*start1.y + d2;
        float seg1_line2_end = a2*end1.x + b2*end1.y + d2;

        float seg2_line1_start = a1*start2.x + b1*start2.y + d1;
        float seg2_line1_end = a1*end2.x + b1*end2.y + d1;
        
        if (seg1_line2_start * seg1_line2_end >= 0 || seg2_line1_start * seg2_line1_end >= 0) 
        {
            return false;
        }
		
		return true;
	}
	
	public static double Angle(Edge one, Edge two)
	{
		return Math.atan2(Edge.vectorMultiplication(one, two), Edge.scalarMultiplication(one, two));
	}
	/**
	 * ParametricCoordsOfSectionByPolygon похоже не работает
	 * IsEdgeIntersectedByPolygon - алгоритм Кириуса-Бэка работает.
	 */
	public  boolean IsEdgeIntersectedByPolygon(ArrayList<Vertex> polygon )
	{
		//инициализируем результирующие точки пересечения
		double t_min = 0; // нижний предел в параметрических координатах
		double t_max = 1; // верхний -//-
		boolean inside = true;
		//boolean sected = false;

		for(int i = 0; i < polygon.size() - 1; i++)
		{
			//this - секомый отрезок
			Edge P = this;
			//R текущее ребро 
			Edge Fi = new Edge(polygon.get(i), polygon.get(i+1));
			//нормаль к ребру
			//Edge Ni = Fi.VectorNormal();
			Edge Ni =  new Edge(0, 0, (Fi.m_end.y - Fi.m_start.y), (Fi.m_start.x - Fi.m_end.x));
			//скалярное произведение . определяет взаимную ориентацию отрезка и ребра
			//double pi = Edge.scalarMultiplication(P, Ni);
			double pi = (P.m_end.x - P.m_start.x)*(Ni.m_end.x - Ni.m_start.x) + (P.m_end.y - P.m_start.y)*(Ni.m_end.y - Ni.m_start.y);
			//вектор Qt = V(t) - F начинающийся в начальной точке ребра окна и заканчивающийся в некоторой точке V(t) удлиненной линии.
			//Edge Qt = P.VerctorDiff(Fi);
			Edge Qt = new Edge(polygon.get(i), P.m_start);
			//скалярное произведение  Qt*Ni
			//double qi = Edge.scalarMultiplication(Qt, Ni);
			double qi = (Qt.m_end.x - Qt.m_start.x)*(Ni.m_end.x - Ni.m_start.x) + (Qt.m_end.y - Qt.m_start.y)*(Ni.m_end.y - Ni.m_start.y);
			if(qi < 0)
			{
				inside = false;
			}
			//вырожден в точку либо паралелен стороне
			if(pi == 0)
			{
				//включительно : "<" 
				//исключительно : "<="
				if(qi < 0)
				{
					//вне окна/невидим
					return false;
				}
			}
			else
			{
				//параметрическая координата пересечения
				double t = -1*(qi/pi);
				//точка пересечения в пределах отрезка

				//с внутренней стороны на внешнюю
				//TODO
				if(pi < 0) 
				{
					if(t < t_max)
					{
						t_max = t;
					}
				}
				//с внешней на внутренюю
				else
				{
					if(t > t_min)
					{
						t_min = t;
					}
				}
			}
		}
		if(t_max >= t_min)
		{
			return true;
		}
		if(inside)
		{
			return true;
		}
		return false;

	}
	
	/**
	 * Сечение отрезка выпуклым произвольным замкнутым многоугольником по Кириусу-Беку
	 * @param polygon произвольный выпуклый полигон заданный упорядоченным  массивом точек.
	 * @return отрезок лежащий внутри полигона при полигоне упорядоченном "по часовой стрелке"
	 * 
	 */
	public Edge SectionByPolygon(ArrayList<Vertex> polygon )
	{
		float[] coords = ParametricCoordsOfSectionByPolygon(polygon);
		if(coords != null)
		{
			return ParametricalPartOfEdge(coords[0], coords[1]);
		}
		return null;
	}
	/**
	 * отрезок, лежащий на той же прямой что и исходный, с концами заданными в параметрическом виде
	 * @param start - координата начала отрезка (0 - совпадает с началом исходного)
	 * @param end - координата конца отрезка (1 - совпадает с концом исходного)
	 * @return Edge
	 */
	public Edge ParametricalPartOfEdge(float start, float end)
	{
		//this
		//X(t) = X0 + t*(X1 - X0) 
		//Y(t) = Y0 + t*(Y1 - Y0)
		// where 0 <= t <= 1
		float X0 = m_start.x;
		float Y0 = m_start.y;
		float X1 = m_end.x;
		float Y1 = m_end.y;
		float res_x_min = X0 + start*(X1 - X0);
		float res_y_min = Y0 + start*(Y1 - Y0);
		float res_x_max = X0 + end*(X1 - X0);
		float res_y_max = Y0 + end*(Y1 - Y0);
		return new Edge(res_x_min, res_y_min, res_x_max, res_y_max);
	}
	
    /**
     * вектор нормали с началом в (0, 0)
     */
	public static Edge VectorNormal(Edge edge)
	{
		return new Edge(0, 0, (edge.m_start.y - edge.m_end.y), (edge.m_end.x - edge.m_start.x));
	}
	
	/**
	 * скалярное произведение
	 */
	public static float scalarMultiplication(Edge one, Edge two)
	{

		return (one.m_end.x - one.m_start.x)*(two.m_end.x - two.m_start.x) + (one.m_end.y - one.m_start.y)*(two.m_end.y - two.m_start.y);
		//return one.vectorX()*two.vectorX() + one.vectorY()*two.vectorY();
	}
	/**
	 * модуль скалярного произведения для векторов на плоскости
	 * 
	 */
	public static float vectorMultiplication(Edge one, Edge two)
	{
		//return vectorX()*edge.vectorY() - vectorY()*edge.vectorX();
		return (one.m_end.x - one.m_start.x)*(two.m_end.y - two.m_start.y) - (two.m_end.x - two.m_start.x)*(one.m_end.y - one.m_start.y);
	}

	
}


