package ru.tcgeo.application.gilib.planimetry;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;


/**
 * абстрактный класс фигуры на плоскости
 */

public abstract class GIShape implements GIGeometryObject
{
	//public static enum TYPE {polygon, line, edge, point};
	public ArrayList<Vertex> m_points;
	public Point m_morton_codes;
	public ArrayList<GIGeometryPolygon> m_rings;
	public ArrayList<Edge> m_edges;
	public String m_labeltext;
	public int m_objectID;
	protected RectF m_bounds;


	public GIShape()
	{
		m_labeltext = "";
		m_points = new ArrayList<Vertex>();
	}
	public GIShape(String text)
	{
		m_labeltext = new String(text);
		m_points = new ArrayList<Vertex>();
	}

	//abstract public TYPE getType();
//	abstract public void DrawRects(Canvas canvas, Paint paint);

	abstract public GIShape clone();
	public void add(PointF point)
	{
		m_points.add(new Vertex(point));
	}
//	public void DrawGeometry(Canvas canvas, Paint paint)
//	{
//		if(m_points.size() == 0)
//		{
//			return;
//		}
//
//        Path path= new Path();
//
//        path.moveTo(m_points.get(0).x, m_points.get(0).y);
//		for(int i = 1; i < m_points.size(); i++)
//		{
//	        path.lineTo(m_points.get(i).x, m_points.get(i).y);
//	        canvas.drawText("_" + i, m_points.get(i).x + 10, m_points.get(i).y, paint);
//		}
//		canvas.drawPath(path, paint);
//		if(m_rings != null)
//		{
//			for(int i = 0; i < m_rings.size(); i++)
//			{
//				 m_rings.get(i).DrawGeometry(canvas, paint);
//			}
//		}
//	}
//
//	public void DrawGeometry(Canvas canvas)
//	{
//		if(m_points.size() == 0)
//		{
//			return;
//		}
//		Paint paint = new Paint();
//        Path path= new Path();
//        paint.setColor(Color.MAGENTA);
//        paint.setStyle(Style.STROKE);
//        paint.setTextSize(12);
//        //paint.setStrokeWidth(1);
//        path.moveTo(m_points.get(0).x, m_points.get(0).y);
//		for(int i = 1; i < m_points.size(); i++)
//		{
//	        path.lineTo(m_points.get(i).x, m_points.get(i).y);
//	        if(i == 4)
//	        canvas.drawText(m_labeltext , m_points.get(i).x + 10, m_points.get(i).y + 10, paint);
//		}
//		canvas.drawPath(path, paint);
//		if(m_rings != null)
//		{
//			for(int i = 0; i < m_rings.size(); i++)
//			{
//				 m_rings.get(i).DrawGeometry(canvas);
//
//			}
//		}
//	}
//	public void DrawEdges(Canvas canvas)
//	{
//		if(m_points.size() == 0)
//		{
//			return;
//		}
//		if(m_edges == null )
//		{
//			return;
//		}
//		if(m_edges.size() == 0 )
//		{
//			return;
//		}
//
//		Paint paint = new Paint();
//        paint.setColor(Color.BLACK);
//        paint.setStyle(Style.STROKE);
//        paint.setStrokeWidth(4);
//
//		for(int i = 0; i < m_edges.size(); i++)
//		{
//			//path.moveTo(m_edges.get(i).m_start.m_point.x, m_edges.get(i).m_start.m_point.y);
//			//path.lineTo(m_edges.get(i).m_end.m_point.x, m_edges.get(i).m_end.m_point.y);
//			canvas.drawLine(m_edges.get(i).m_start.x, m_edges.get(i).m_start.y,m_edges.get(i).m_end.x, m_edges.get(i).m_end.y, paint);
//		}
//	}
//	public static double epsilon(PointF first_of_line, PointF second_of_line, PointF third)
//	{
//		PointF a = first_of_line;
//		PointF b = second_of_line;
//		PointF c = third;
//		//Ax + By + C = 0
//		float A = (a.y - b.y);
//		float B = (b.x - a.x);
//		float C = a.x*b.y - b.x*a.y;
//		//next point m_points.get(i) distance
//		double epsi = Math.abs((A*c.x +B*c.y + C)/Math.hypot(A, B));
//		return epsi;
//	}

	abstract public ArrayList<Edge> MakeEdgesRing();

	public RectF getBounds()
	{
		if(m_points == null)
		{
			return null;
		}
		if(m_points.size() == 0)
		{
			return new RectF(0, 0, 0, 0);
		}
		float minX, maxX, minY, maxY;
		minX = m_points.get(0).x;
		maxX = m_points.get(0).x;
		minY = m_points.get(0).y;
		maxY = m_points.get(0).y;

		for(int i = 1; i < m_points.size(); i++)
		{
			PointF cur = m_points.get(i);
			if(cur.x < minX)
			{
				minX = cur.x;
			}
			if(cur.x > maxX)
			{
				maxX = cur.x;
			}
			if(cur.y < minY)
			{
				minY = cur.y;
			}
			if(cur.y > maxY)
			{
				maxY = cur.y;
			}
		}
		return new RectF(minX, minY, maxX, maxY);
	}
//
//	public Point getMortonCodes()
//	{
//		return m_morton_codes;
//	}
//
//	public void setMortonCodes(Point codes)
//	{
//		m_morton_codes = codes;
//	}


//	public PointF getCenterOfBounds()
//	{
//		RectF bounds = getBounds();
//		return new PointF(bounds.centerX(), bounds.centerY());
//	}
	//abstract ArrayList<PointF> IntersectByRect(ArrayList<Vertex> source, Rect rect);
	/*
	public static ArrayList<PointF> IntersectByRect(ArrayList<Vertex> source, Rect rect)
	{
		ArrayList<PointF> result = new ArrayList<PointF>();
		for(int i = 0; i < source.size() - 1; i++)
		{
			Vertex current = new Vertex(source.get(i));
			Vertex next = new Vertex(source.get(i+1));
			//exclude Edge. invisible.
			if((current.getCode(rect) & next.getCode(rect)) != 0)
			{
				//cutline
				Vertex first = current.projectOnRect(current, rect);
				if(result.size() == 0 || !first.equals(result.get(result.size()-1)))
				{
					result.add(first);
				}
				Vertex second = next.projectOnRect(next, rect);
				result.add(second);
				continue;
			}
			//include Edge. visible.
			if((current.getCode(rect) | next.getCode(rect)) == 0)
			{
				if(result.size() == 0 || !current.equals(result.get(result.size()-1)))
				{
					result.add(current);
				}
				result.add(next);
				continue;
			}

			Edge clipped = Edge.Clipping(new Edge(current, next), rect);
			if(clipped != null)
			{
				//cutline
				if(current.getCode(rect) != 0)
				{
					Vertex first = current.projectOnRect(current, rect);
					if(result.size() == 0 || !first.equals(result.get(result.size()-1)))
					{
						result.add(first);
					}
				}
				//visible part
				if(result.size() == 0 || !clipped.m_start.equals(result.get(result.size()-1)))
				{
					result.add(clipped.m_start);
				}
				result.add(clipped.m_end);
				//cutline
				if(next.getCode(rect) != 0)
				{
					Vertex second = next.projectOnRect(next, rect);
					if(result.size() == 0 || !second.equals(result.get(result.size()-1)))
					{
						result.add(second);
					}
				}
			}

		}
		return result;
	}*/
	abstract public boolean IntersectByRect(RectF rect);

//	public void generalize(float factor)
//	{
//		ArrayList<Vertex> points = new ArrayList<Vertex>();
//		if(m_points.size() < 3)
//		{
//			return;// new ArrayList<PointF>();
//		}
//		points.add(m_points.get(0));
//		points.add(m_points.get(1));
//		int count = 2;
//		while(count < m_points.size()) // + 1
//		{
//			if(points.size() < 2)
//			{
//				return;// new ArrayList<PointF>();
//			}
//			Vertex a =  points.get(points.size() - 2);
//			Vertex b =  points.get(points.size() - 1);
//			boolean in_sigma = true;
//			Vertex c;
//			Vertex next_b = b;
//			do
//			{
//				c =  m_points.get(count);
//				double sigma = GIGeometryPolygon.epsilon(a, b, c);
//				if(sigma > factor)
//				{
//					in_sigma = false;
//				}
//				else
//				{
//					next_b = c;
//					count++;
//				}
//			}
//			while((count < m_points.size()) && in_sigma); // + 1
//			points.set(points.size() - 1, next_b);
//			if(!in_sigma)
//			{
//				points.add(c);
//			}
//			count++;
//		}
//
//		if(points.size() < 3)
//		{
//			points = new ArrayList<Vertex>();
//		}
//		if(points.size() > 3)
//		{
//			Vertex zero = points.get(0);
//			Vertex first = points.get(1);
//			Vertex pre_last = points.get(points.size() - 2);
//			double sigma = GIGeometryPolygon.epsilon(first, pre_last, zero);
//			if(sigma <= factor)
//			{
//				points.set(points.size() - 1, first);
//				points.remove(zero);
//			}
//		}
//		if(m_rings != null)
//		{
//			for(int i = 0; i < m_rings.size(); i++)
//			{
//				m_rings.get(i).generalize(factor);
//			}
//		}
//		m_points = points;
//
//	}
	/*public static ArrayList<PointF> generalize(ArrayList<PointF> geometry, float factor)
	{
		ArrayList<PointF> points = new ArrayList<PointF>();
		if(geometry.size() < 3)	
		{
			return new ArrayList<PointF>();
		}
		points.add(geometry.get(0));
		points.add(geometry.get(1));
		int count = 2;
		while(count < geometry.size()) // + 1
		{
			if(points.size() < 2)
			{
				return new ArrayList<PointF>();
			}
			PointF a =  points.get(points.size() - 2);
			PointF b =  points.get(points.size() - 1);
			boolean in_sigma = true;
			PointF c;
			PointF next_b = b;
			do
			{
				c =  geometry.get(count);
				double sigma = LabelGeometry.epsilon(a, b, c);
				if(sigma > factor)
				{
					in_sigma = false;
				}
				else
				{
					next_b = c;
					count++;
				}
			}
			while((count < geometry.size()) && in_sigma); // + 1
			points.set(points.size() - 1, next_b);
			if(!in_sigma)
			{
				points.add(c);
			}
			count++;
		}
		
		if(points.size() < 3)	
		{
			points = new ArrayList<PointF>();
		}
		if(points.size() > 3)
		{
			PointF zero = points.get(0);
			PointF first = points.get(1);
			PointF pre_last = points.get(points.size() - 2);
			double sigma = LabelGeometry.epsilon(first, pre_last, zero);
			if(sigma <= factor)
			{
				points.set(points.size() - 1, first);
				points.remove(zero);
			}
		}
		return points;
	}*/
//	abstract public void DrawBoundaries(Canvas canvas);
	
	/*public ArrayList<Vertex> generalize_bad_question(float factor)
	{
		ArrayList<Vertex> points = new ArrayList<Vertex>();
		if(m_points.size() < 3)	
		{
			return new ArrayList<Vertex>();
		}
		points.add(m_points.get(0));
		points.add(m_points.get(1));
		int count = 2;
		while(count < m_points.size()) // + 1
		{
			if(points.size() < 2)
			{
				return new ArrayList<Vertex>();
			}
			Vertex a =  points.get(points.size() - 2);
			Vertex b =  points.get(points.size() - 1);
			boolean in_sigma = true;
			Vertex c;
			Vertex next_b = b;
			do
			{
				c =  m_points.get(count);
				double sigma = LabelGeometry.epsilon(a, b, c);
				if(sigma > factor)
				{
					in_sigma = false;
				}
				else
				{
					next_b = c;
					count++;
				}
			}
			while((count < m_points.size()) && in_sigma); // + 1
			points.set(points.size() - 1, next_b);
			if(!in_sigma)
			{
				points.add(c);
			}
			count++;
		}
		if(points.size() < 3)	
		{
			points = new ArrayList<Vertex>();
		}
		m_points = points;
		return points;
	}*/

}
