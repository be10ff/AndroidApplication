package ru.tcgeo.application.gilib.planimetry;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;

/**
 * класс полигонального объекта для заполнения текстом
 *
 */
public class GIGeometryPolygon extends GIShape
{
	public static enum KIND {convex_cc, convex_uncc, unconvex_cc, unconvex_uncc};
	private KIND[] kindes = {KIND.convex_cc, KIND.convex_uncc, KIND.unconvex_cc, KIND.unconvex_uncc};
	public int iKind;
	private ArrayList<Edge> m_levels;
	private ArrayList<RectF> m_rects;

	/**
	 * конструктор по умолчанию
	 */
	public GIGeometryPolygon()
	{
		super();
	}

	/**
	 * конструктор с заданным текстом
	 * @param text который будем вписывать
	 */
	public GIGeometryPolygon(String text)
	{
		super(text);
	}
	/**
	 * конструктор полигона по Rect
	 * @param rect преобразуемый
	 */
	public GIGeometryPolygon(Rect rect)
	{
		super();
		m_points.add(new Vertex(rect.left, rect.top));
		m_points.add(new Vertex(rect.right, rect.top));
		m_points.add(new Vertex(rect.right, rect.bottom));
		m_points.add(new Vertex(rect.left, rect.bottom));
		m_points.add(new Vertex(rect.left, rect.top));
	}

	/**
	 * polygon
	 */
	public TYPE getType()
	{
		return TYPE.polygon;
	}
	public GIGeometryPolygon clone()
	{
		GIGeometryPolygon result = new GIGeometryPolygon(m_labeltext);
		for(int i = 0; i < m_points.size(); i++)
		{
			result.add(m_points.get(i).clone());
		}
		return result;
	}
	/**
	 * добавляем полигон как inner ring к объекту
	 * @param ring - inner ring
	 */
	public void addRing(GIGeometryPolygon ring)
	{
		if(ring == null)
		{
			ring = new GIGeometryPolygon();
		}
		if(m_rings == null)
		{
			m_rings = new ArrayList<GIGeometryPolygon>();
		}
		m_rings.add(ring);
	}

	public void DrawLevels(Canvas canvas)
	{
		Paint paint = new Paint();
        Path path= new Path();
        paint.setColor(Color.RED);
        paint.setStyle(Style.STROKE);
        paint.setTextSize(12);

        if(m_levels == null)
        {
        	return;
        }

        for(int k = 0; k < m_levels.size(); k++)
        {
        	Path line_path = new Path();
        	Edge line = m_levels.get(k);
        	line_path.moveTo(line.m_start.x  , line.m_start.y );
        	line_path.lineTo(line.m_end.x , line.m_end.y);
        	canvas.drawPath(line_path, paint);
        	line_path.reset();
        }

        canvas.drawPath(path, paint);

	}
//	@Override
//	public void DrawRects(Canvas canvas, Paint paint_text)
//	{
//		Paint paint = new Paint();
//        paint.setColor(Color.CYAN);
//        paint.setStyle(Style.FILL_AND_STROKE);
//        paint.setTextSize(12);
//
//        if(m_rects == null)
//        {
//        	return;
//        }
//
//        for(int k = 0; k < m_rects.size(); k++)
//		{
//        	RectF rect = m_rects.get(k);
//        	canvas.drawRect(rect, paint);
//        }
//	}
//	@Override
//	public void DrawBoundaries(Canvas canvas)
//	{
//
//	}
/**
 * построение массива отрезков полигона по массиву точек
 */
	@Override
	public ArrayList<Edge> MakeEdgesRing()
	{
		m_edges = new ArrayList<Edge>();
		for(int i = 0; i < m_points.size()-1; i++)
		{
			Vertex current = new Vertex(m_points.get(i));
			Vertex next = new Vertex(m_points.get(i+1));
			if(m_bounds != null)
			{
				if(current._m_original == 0)
				{
					current._m_original = current.getQuarte(m_bounds);
				}
				if(next._m_original == 0)
				{
					next._m_original = next.getQuarte(m_bounds);
				}

			}
			Edge edge = new Edge(current, next);
			m_edges.add(edge);
		}

		if(m_rings != null)
		{
			for(int i = 0; i < m_rings.size(); i++)
			{
				m_edges.addAll(m_rings.get(i).MakeEdgesRing());
			}
		}
		return m_edges;
	}


	/**
	 * сечение полигона ограничивающим прямоугольником с сохранением топологии
	 */
	@Override
	public boolean IntersectByRect(RectF rect)
	{
		boolean isNotInvisible = false;
		m_bounds = rect;
		ArrayList<Vertex> result = new ArrayList<Vertex>();
		for(int i = 0; i < m_points.size() - 1; i++)
		{
			Vertex current = new Vertex(m_points.get(i));
			Vertex next = new Vertex(m_points.get(i+1));
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
				if(result.size() == 0 || !second.equals(result.get(result.size()-1)))
				{
					result.add(second);
				}
				continue;
			}
			//include Edge. visible.
			if((current.getCode(rect) | next.getCode(rect)) == 0)
			{
				if(result.size() == 0 || !current.equals(result.get(result.size()-1)))
				{
					isNotInvisible = true;
					result.add(current);
				}
				if(result.size() == 0 || !next.equals(result.get(result.size()-1)))
				{
					isNotInvisible = true;
					result.add(next);
				}
				continue;
			}

			Edge clipped = Edge.Clipping(new Edge(current, next), rect);
			//cutline
			if(current.getCode(rect) != 0)
			{
				Vertex first = current.projectOnRect(current, rect);
				if(result.size() == 0 || !first.equals(result.get(result.size()-1)))
				{
					result.add(first);
				}
			}
			if(clipped != null)
			{
				//visible part
				if(result.size() == 0 || !clipped.m_start.equals(result.get(result.size()-1)))
				{
					isNotInvisible = true;
					result.add(clipped.m_start);
				}
				if(result.size() == 0 || !clipped.m_end.equals(result.get(result.size()-1)))
				{
					isNotInvisible = true;
					result.add(clipped.m_end);
				}
			}
			else
				//invisible corner point
			{
				int code_start = current.getCode(rect);
				int code_end = next.getCode(rect);
				if(((code_start == 8)&&(code_end == 2))||((code_start == 2)&&(code_end == 8)))
				{
					Vertex invisiblecorner = new Vertex(new PointF(rect.left, rect.top));
					invisiblecorner._m_original = 10;
					result.add(invisiblecorner);
				}
				if(((code_start == 4)&&(code_end == 2))||((code_start == 2)&&(code_end == 4)))
				{
					Vertex invisiblecorner = new Vertex(new PointF(rect.right, rect.top));
					invisiblecorner._m_original = 6;
					result.add(invisiblecorner);
				}
				if(((code_start == 4)&&(code_end ==1))||((code_start == 1)&&(code_end == 4)))
				{
					Vertex invisiblecorner = new Vertex(new PointF(rect.right, rect.bottom));
					invisiblecorner._m_original = 5;
					result.add(invisiblecorner);
				}
				if(((code_start == 8)&&(code_end ==1))||((code_start == 1)&&(code_end == 8)))
				{
					Vertex invisiblecorner = new Vertex(new PointF(rect.left, rect.bottom));
					invisiblecorner._m_original = 9;
					result.add(invisiblecorner);
				}
			}
			//cutline
			if(next.getCode(rect) != 0)
			{
				Vertex second = next.projectOnRect(next, rect);
				if(result.size() == 0 || !second.equals(result.get(result.size()-1)))
				{
					//second.
					result.add(second);
				}
			}
		}
		if(m_rings != null)
		{
			for(int i = 0; i < m_rings.size(); i++)
			{
				GIGeometryPolygon ring = m_rings.get(i);
				//ring.IntersectByRect(rect);
				//TODO excluding fully invisible inner rings. works. commented for debugging LabelGeometry.Difference
				if(!ring.IntersectByRect(rect))
				{
					m_rings.remove(ring);
					i = i - 1;
				}
			}
		}
		m_points = result;
		return isNotInvisible;
	}
/**
 * упрощение контура полигона исключением перекрывающихся отрезков из ребер
 */

	public void Difference()
	{
		if(m_edges == null)
		{
			MakeEdgesRing();
		}
		int index = 0;

		while(index <  m_edges.size() - 1)
		{
			Edge current = m_edges.get(index);
			int next_idx = index + 1;
			boolean happends = false;

			boolean current_is_interesting = ((current.m_start._m_original & current.m_end._m_original) != 0);
			//TODO there is max method
			current_is_interesting = true;
			while((next_idx <  m_edges.size())&&current_is_interesting)
			{
				Edge compare = m_edges.get(next_idx);
				boolean pair_is_interesting = ((current.m_start._m_original & current.m_end._m_original & compare.m_start._m_original & compare.m_end._m_original) != 0);
				//TODO there is max method
				pair_is_interesting = true;
				if(pair_is_interesting)
				{
					ArrayList<Edge> divided = current.Difference(compare);
					if(divided != null)
					{
						m_edges.addAll(divided);
						m_edges.remove(compare);
						m_edges.remove(current);
						happends = true;
						break;
					}
					else
					{
						next_idx++;
					}
				}
				else
				{
					next_idx++;
				}

			}
			if(!happends)
			{
				index++;
			}
		}
	}
	/**
	 * кажется устаревшее. строит прямоугольники для текста по медианам трапеций
	 * @return
	 */
//	public ArrayList<RectF> getTextRectArray()
//	{
//		ArrayList<Edge> edges = getTextLineArray();
//		ArrayList<RectF> result = new ArrayList<RectF>();
//		for(int i = 0; i < edges.size() - 1; i++)
//		{
//			Edge top_edge = edges.get(i);
//			Edge bottom_edge = edges.get(i+1);
//			Edge left_edge = new Edge(top_edge.m_start, bottom_edge.m_start);
//			Edge right_edge = new Edge(top_edge.m_end, bottom_edge.m_end);
//
//			float left = left_edge.center_point().x;
//			float right = right_edge.center_point().x;
//			float top = top_edge.m_start.y;
//			float bottom = bottom_edge.m_start.y;
//			RectF rect = new RectF(left, top, right, bottom);
//			result.add(rect);
//		}
//		m_rects = result;
//		return result;
//	}
//	public ArrayList<Trapezoid> getTextTrapezoidArray()
//	{
//		ArrayList<Edge> edges = getTextLineArray();
//		if(edges == null)
//		{
//			return null;
//		}
//		ArrayList<Trapezoid> result = new ArrayList<Trapezoid>();
//		int counter = 0;
//		int internal_counter = 0;
//
//		ArrayList<ArrayList<Edge>> levels = new ArrayList<ArrayList<Edge>>();
//		while(counter < edges.size())
//		{
//			Edge top_edge = edges.get(counter);
//			ArrayList<Edge> one_lvl_edges = new ArrayList<Edge>();
//			one_lvl_edges.add(top_edge);
//			internal_counter = counter + 1;
//			while((internal_counter < edges.size())&&(Math.abs(top_edge.m_start.y - edges.get(internal_counter).m_start.y) < Vertex.delta))
//			{
//				Edge next_top = edges.get(internal_counter);
//				one_lvl_edges.add(next_top);
//				internal_counter++;
//			}
//			levels.add(one_lvl_edges);
//			counter = internal_counter;
//		}
//		for(int i = 0; i < levels.size() - 1; i++)
//		{
//			ArrayList<Edge> top_edges = levels.get(i);
//			ArrayList<Edge> bottom_edges = levels.get(i+1);
//			boolean CanJoinTop = true;
//			boolean CanJoinBottom = true;
//			int j = 1;
//			Edge top = top_edges.get(0);
//			while(CanJoinTop && j  < top_edges.size())
//			{
//				Edge next = top_edges.get(j);
//				if(!top.CanBeJoin(next))
//				{
//					CanJoinTop = false;
//				}
//				else
//				{
//					top = top.Join(next);
//				}
//				j++;
//			}
//			j = 1;
//			Edge bottom = bottom_edges.get(0);
//			while(CanJoinBottom && j  < bottom_edges.size())
//			{
//				Edge next = bottom_edges.get(j);
//				if(!bottom.CanBeJoin(next))
//				{
//					CanJoinBottom = false;
//				}
//				else
//				{
//					bottom = bottom.Join(next);
//				}
//
//				j++;
//			}
//			if(CanJoinBottom && CanJoinTop)
//			{
//				Trapezoid segment = new Trapezoid(top, bottom);
//				result.add(segment);
//			}
//			/*else
//			{
//				//Vertex top_left = top_edges.get(0).m_start;
//				int k = 0;
//				while(k < top_edges.size())
//				{
//					Vertex current = top_edges.get(k).m_start;
//					int t = 0;
//					boolean current_has_link = true;
//					while( t < bottom_edges.size() && current_has_link)
//					{
//						compare = bottom_edges.get(t);
//						for(int n = 0; n < m_edges.size(); n++)
//						{
//							if()
//						}
//						t++;
//					}
//				}
//
//
//			}*/
//		}
//		return result;
//	}
//
//	public ArrayList<Trapezoid> getTextMaxTrapezoidArray()
//	{
//		ArrayList<Edge> edges = getTextLineArray();
//		if(edges == null)
//		{
//			return null;
//		}
//		ArrayList<Trapezoid> result = new ArrayList<Trapezoid>();
//		int counter = 0;
//		int top_counter = 0;
//		boolean canBeAddedTop = false;
//		boolean canBeAddedBottom = false;
//		//boolean oneHasCollected = false;
//		while(counter < edges.size())
//		{
//			Edge top_edge = edges.get(counter);
//			ArrayList<Edge> top_edges = new ArrayList<Edge>();
//			top_counter = counter + 1;
//			//oneHasCollected = false;
//			canBeAddedTop = true;
//			while((top_counter < edges.size())&&(Math.abs(top_edge.m_start.y - edges.get(top_counter).m_start.y) < Vertex.delta))
//			{
//				Edge next_top = edges.get(top_counter);
//				//top_edges.add(next_top);
//				if((top_edge.CanBeJoin(next_top)))
//				{
//					top_edge = top_edge.Join(next_top);
//					//oneHasCollected = true;
//				}
//				else
//				{
//					canBeAddedTop = false;
//				}
//				if(!canBeAddedTop)
//				{
//					top_edges.add(top_edge);
//				}
//				top_counter++;
//			}
//			counter = top_counter;
//			if(counter < edges.size())
//			{
//				Edge bottom_edge = edges.get(counter);
//				int bottom_counter = counter + 1;
//				canBeAddedBottom = true;
//				ArrayList<Edge> bottom_edges = new ArrayList<Edge>();
//				while((bottom_counter < edges.size())&&(Math.abs(bottom_edge.m_start.y - edges.get(bottom_counter).m_start.y) < Vertex.delta))
//				{
//					Edge next_bottom = edges.get(bottom_counter);
//					bottom_edges.add(next_bottom);
//					if((bottom_edge.CanBeJoin(next_bottom))/*&&canBeAddedTop&&!oneHasCollected*/)
//					{
//						bottom_edge = bottom_edge.Join(next_bottom);
//						//oneHasCollected = true;
//					}
//					else
//					{
//						canBeAddedBottom = false;
//					}
//					if(!canBeAddedBottom)
//					{
//						top_edges.add(bottom_edge);
//					}
//					bottom_counter++;
//				}
//				if(canBeAddedTop&&canBeAddedBottom&&(top_edge != null)&&(bottom_edge != null))
//				{
//					Trapezoid segment = new Trapezoid(top_edge, bottom_edge);
//					result.add(segment);
//				}
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * строит массив пригодных для написания текстов трапеций
//	 * @return
//	 */
//	public ArrayList<Trapezoid> getTextMinTrapezoidArray()
//	{
//
//		ArrayList<Edge> edges = getTextLineArray();
//		if(edges == null)
//		{
//			return null;
//		}
//		ArrayList<Trapezoid> result = new ArrayList<Trapezoid>();
//		int counter = 0;
//		int top_counter = 0;
//		boolean canBeAddedTop = false;
//		boolean canBeAddedBottom = false;
//		//boolean oneHasCollected = false;
//		while(counter < edges.size())
//		{
//			Edge top_edge = edges.get(counter);
//			ArrayList<Edge> top_edges = new ArrayList<Edge>();
//			top_counter = counter + 1;
//			//oneHasCollected = false;
//			canBeAddedTop = true;
//			while((top_counter < edges.size())&&(Math.abs(top_edge.m_start.y - edges.get(top_counter).m_start.y) < Vertex.delta))
//			{
//				Edge next_top = edges.get(top_counter);
//				top_edges.add(next_top);
//				if((top_edge.CanBeJoin(next_top))&&canBeAddedTop)
//				{
//					top_edge = top_edge.Join(next_top);
//					//oneHasCollected = true;
//				}
//				else
//				{
//					canBeAddedTop = false;
//				}
//				top_counter++;
//			}
//			counter = top_counter;
//			if(counter < edges.size())
//			{
//				Edge bottom_edge = edges.get(counter);
//				int bottom_counter = counter + 1;
//				canBeAddedBottom = true;
//				ArrayList<Edge> bottom_edges = new ArrayList<Edge>();
//				while((bottom_counter < edges.size())&&(Math.abs(bottom_edge.m_start.y - edges.get(bottom_counter).m_start.y) < Vertex.delta))
//				{
//					Edge next_bottom = edges.get(bottom_counter);
//					bottom_edges.add(next_bottom);
//					if((bottom_edge.CanBeJoin(next_bottom))&&canBeAddedTop/*&&!oneHasCollected*/)
//					{
//						bottom_edge = bottom_edge.Join(next_bottom);
//						//oneHasCollected = true;
//					}
//					else
//					{
//						canBeAddedBottom = false;
//					}
//					bottom_counter++;
//				}
//				if(canBeAddedTop&&canBeAddedBottom&&(top_edge != null)&&(bottom_edge != null))
//				{
//					Trapezoid segment = new Trapezoid(top_edge, bottom_edge);
//					result.add(segment);
//				}
//			}
//		}
//		return result;
//	}
//
//
//	public ArrayList<Trapezoid> getTextTrapezoidArray_old()
//	{
//		ArrayList<Edge> edges = getTextLineArray();
//		ArrayList<Trapezoid> result = new ArrayList<Trapezoid>();
//		for(int i = 0; i < edges.size() - 1; i++)
//		{
//			//минимальный вариант. отбрасываем "рога"
//			Edge top_edge = edges.get(i);
//			Edge bottom_edge = edges.get(i+1);
//			boolean toAddTrapezoid = true;
//			//два отрезка на одной горизонтали
//			if(Math.abs(top_edge.m_start.y - bottom_edge.m_start.y) < Vertex.delta)
//			{
//				if(i < edges.size() - 2)
//				{
//					if(top_edge.CanBeJoin(bottom_edge))
//					{
//						//новые top_edge bottom_edge
//						top_edge = (top_edge.Join(bottom_edge));
//						bottom_edge = edges.get(i+2);
//						i++;
//					}
//					else
//					{
//						toAddTrapezoid = false; //????
//						i++;
//					}
//				}
//			}
//			//а вдруг bottom тож можно объеденить?
//			if((i+2) < edges.size())
//			{
//				Edge next = edges.get(i+2);
//				//if(bottom_edge.m_start.m_point.y == next.m_start.m_point.y)
//				if(Math.abs(bottom_edge.m_start.y - next.m_start.y) < Vertex.delta)
//				{
//					if(bottom_edge.CanBeJoin(next))
//					{
//						//новый top_edge bottom_edge
//						bottom_edge = (bottom_edge.Join(next));
//					}
//					else
//					{
//						toAddTrapezoid = false;
//					}
//				}
//			}
//			if(toAddTrapezoid)
//			{
//				Trapezoid segment = new Trapezoid(top_edge, bottom_edge);
//				result.add(segment);
//			}
//
//			//вариант. используем "рога"
//			/*
//			PointF center = getCenterOfBounds();
//			Edge top_edge = edges.get(i);
//			Edge bottom_edge = edges.get(i+1);
//			boolean toAddTrapezoid = true;
//			boolean topCouldBeJoined = true;
//			boolean topHasJoined = false;
//			//два отрезка на одной горизонтали
//			if(top_edge.m_start.m_point.y == bottom_edge.m_start.m_point.y)
//			{
//				if(i < edges.size() - 2)
//				{
//					if(top_edge.CanBeJoin(bottom_edge))
//					{
//						//новые top_edge bottom_edge
//						top_edge = (top_edge.Join(bottom_edge));
//						bottom_edge = edges.get(i+2);
//						topCouldBeJoined = true;
//						topHasJoined = true;
//						//joinedYet = true;
//						i++;
//					}
//					else
//					{
//						top_edge = Edge.NearestToPointOfTwo(top_edge, bottom_edge, center);
//						bottom_edge = edges.get(i+2);
//						topCouldBeJoined = false;
//						topHasJoined = false;
//						center = top_edge.center_point().m_point;
//						//toAddTrapezoid = false;
//						i++;
//					}
//				}
//			}
//			//а вдруг bottom тож можно объеденить?
//			if((i+2) < edges.size())
//			{
//				Edge next = edges.get(i+2);
//				if(bottom_edge.m_start.m_point.y == next.m_start.m_point.y)
//				{
//					if(bottom_edge.CanBeJoin(next)&&topHasJoined)
//					{
//						//новый top_edge bottom_edge
//						bottom_edge = (bottom_edge.Join(next));
//					}
//					else
//					{
//						bottom_edge = Edge.NearestToPointOfTwo(next, bottom_edge, center);
//						if(topHasJoined)
//						{
//							center = bottom_edge.center_point().m_point;
//							top_edge = Edge.NearestToPointOfTwo(edges.get(i), edges.get(i-1), center);
//						}
//					}
//				}
//			}
//			if(toAddTrapezoid)
//			{
//				Trapezoid segment = new Trapezoid(top_edge, bottom_edge);
//				center = bottom_edge.center_point().m_point;
//				result.add(segment);
//			}*/
//		}
//		return result;
//	}
//
//	public ArrayList<Edge> getTextLineArray_old_working()
//	{
//		ArrayList<Edge> lines = new ArrayList<Edge>();
//		ArrayList<Vertex> sorted_points = new ArrayList<Vertex>();
//		//adding interior rings
//		ArrayList<Vertex> all_points = new ArrayList<Vertex>();
//		all_points.addAll(m_points);
//		if(m_rings != null)
//		{
//			for(int i = 0; i < m_rings.size(); i++)
//			{
//				all_points.addAll(m_rings.get(i).m_points);
//			}
//		}
//		// получаем неповторяющийся список горизонталей через все вершины полигона
//		for(int d = 0; d < all_points.size(); d++)
//		{
//			Vertex current_point = all_points.get(d);
//			//TODO excluding fully invisible inner rings. works. commented for debugging LabelGeometry.Difference
//			//if(current_point.GetOriginVisiblity())
//			{
//				if(sorted_points.size() == 0)
//				{
//					sorted_points.add(current_point);
//				}
//				else
//				{
//					if(current_point.y > sorted_points.get(sorted_points.size()-1).y)
//					{
//						sorted_points.add(current_point);
//					}
//					else
//					{
//						for(int k = 0; k < sorted_points.size(); k++)
//						{
//							PointF compare = sorted_points.get(k);
//							if(Math.abs(current_point.y - compare.y) < Vertex.delta)
//							{
//								k = sorted_points.size();
//								continue;
//							}
//							if(current_point.y < compare.y)
//							{
//								sorted_points.add(k, current_point);
//								k = sorted_points.size();
//							}
//						}
//					}
//				}
//			}
//		}
//		// this
//		//generalize(1);
//		//MakeEdgesRing();
//		// or this
//		Difference(); //MakeEdgesRing(); inside : получаем m_edges, последовательно и замкнуто обходящий исходный полигон
//		if(m_edges == null)
//		{
//			return null;
//		}
//		if(m_edges.size() == 0)
//		{
//			return null;
//		}
//
//		ArrayList<Edge> intersection_edges_prev_lvl = new ArrayList<Edge>();
//		//идем по горизонталям последовательно по возрастанию
//		for(int level = 0; level < sorted_points.size(); level++) //??????? -1
//		{
//			float levelY = sorted_points.get(level).y;
//			//для каждой горизонтали создаем список всех пересекающихся с ней Edge
//			ArrayList<Edge> intersection_edges = new ArrayList<Edge>();
//			for(int i = 0; i < m_edges.size(); i++)
//			{
//				Edge current_edge = m_edges.get(i);
//				if(Edge.IsEdgeIntersectHorizontal(current_edge, levelY))
//				{
//					current_edge.m_intersection_point = Edge.EdgeIntersectionHorizontal(current_edge, levelY);
//					intersection_edges.add(current_edge);
//				}
//			}
//			ArrayList<Edge> intersection_edges_res = new ArrayList<Edge>();
//			intersection_edges_res.addAll(intersection_edges);
//			//для всех горизонталей
//			for(int i = 0; i < intersection_edges.size(); i ++)
//			{
//				Edge current_edge =  intersection_edges.get(i);
//				//  удаляем из списка все пересекающие ее _горизонтальные Edge
//				if(Edge.IsCoincidesHorizontal(current_edge, levelY))
//				{
//					//TODO: ??????
//					if(current_edge.m_start.x > current_edge.m_end.x)
//					{
//						current_edge.Swap();
//					}
//					lines.add(current_edge);
//					//TODO : horizontal edge problems
//					intersection_edges_res.remove(current_edge);
//				}
//				//кроме нижней удаляем из списка все оканчивающиеся/начинающиеся Edge которые были задеты на предыдущей горизонтали
//				//
//				//if(level != sorted_points.size() - 1)
//				{
//					for(int j = 0; j < intersection_edges_prev_lvl.size(); j++)
//					{
//						if((intersection_edges_prev_lvl.get(j) == current_edge)&&(current_edge.HasPointAsEnd(current_edge.m_intersection_point)))
//						{
//							//если ниже из точки есть продолжения
//							int num = 0;
//							for(int k = 0; k < m_edges.size(); k++)
//							{
//								if(m_edges.get(k).HasPointAsEnd(current_edge.m_intersection_point) && m_edges.get(k).center_point().y + Vertex.delta >= levelY)
//								{
//									num++;
//								}
//							}
//							if(num >0)
//							{
//								intersection_edges_res.remove(current_edge);
//							}
//						}
//					}
//				}
//			}
//			//пока оставшийся список не пуст
//			while(intersection_edges_res.size() > 1)
//			{
//				//нашли самую левую точку и удалили ее
//				float left = intersection_edges_res.get(0).m_intersection_point.x;
//				int left_index = 0;
//				for(int i = 1; i < intersection_edges_res.size();i++)
//				{
//					if(intersection_edges_res.get(i).m_intersection_point.x < left)
//					{
//						left = intersection_edges_res.get(i).m_intersection_point.x;
//						left_index = i;
//					}
//				}
//				//еще раз нашли самую левую точку и удалили ее
//				intersection_edges_res.remove(intersection_edges_res.get(left_index));
//				float next_left = intersection_edges_res.get(0).m_intersection_point.x;
//				int left_next_index = 0;
//				for(int i = 1; i < intersection_edges_res.size();i++)
//				{
//					if(intersection_edges_res.get(i).m_intersection_point.x < next_left)
//					{
//						next_left = intersection_edges_res.get(i).m_intersection_point.x;
//						left_next_index = i;
//					}
//				}
//				intersection_edges_res.remove(intersection_edges_res.get(left_next_index));
//				// добавили отрезок между самой левой и следующей за ней
//				lines.add(new Edge(new Vertex(new PointF(left, levelY)), new Vertex(new PointF(next_left, levelY))));
//			}
//			intersection_edges_prev_lvl = intersection_edges;
//		}
//		m_levels = lines;
//		return lines;
//	}
//-------------------------------------------------------------------------------------------
	/**
	 * разбивка полигона на трапеции сечением горизонталями через все вершины
	 * @return массив лежащих внутри полигона горизонтальных отрезков
	 */
//	public ArrayList<Edge> getTextLineArray()
//	{
//		ArrayList<Edge> lines = new ArrayList<Edge>();
//		ArrayList<Vertex> sorted_points = new ArrayList<Vertex>();
//		//adding interior rings
//		ArrayList<Vertex> all_points = new ArrayList<Vertex>();
//		all_points.addAll(m_points);
//		if(m_rings != null)
//		{
//			for(int i = 0; i < m_rings.size(); i++)
//			{
//				all_points.addAll(m_rings.get(i).m_points);
//			}
//		}
//		// получаем неповторяющийся список горизонталей через все вершины полигона
//		for(int d = 0; d < all_points.size(); d++)
//		{
//			Vertex current_point = all_points.get(d);
//			//TODO excluding fully invisible inner rings. works. commented for debugging LabelGeometry.Difference
//			//if(current_point.GetOriginVisiblity())
//			{
//				if(sorted_points.size() == 0)
//				{
//					sorted_points.add(current_point);
//				}
//				else
//				{
//					if(current_point.y > sorted_points.get(sorted_points.size()-1).y)
//					{
//						sorted_points.add(current_point);
//					}
//					else
//					{
//						for(int k = 0; k < sorted_points.size(); k++)
//						{
//							PointF compare = sorted_points.get(k);
//							if(Math.abs(current_point.y - compare.y) < Vertex.delta)
//							{
//								k = sorted_points.size();
//								continue;
//							}
//							if(current_point.y < compare.y)
//							{
//								sorted_points.add(k, current_point);
//								k = sorted_points.size();
//							}
//						}
//					}
//				}
//			}
//		}
//		// this
//		//generalize(1);
//		//MakeEdgesRing();
//		// or this
//		Difference(); //MakeEdgesRing(); inside : получаем m_edges, последовательно и замкнуто обходящий исходный полигон
//		if(m_edges == null)
//		{
//			return null;
//		}
//		if(m_edges.size() == 0)
//		{
//			return null;
//		}
//
//		ArrayList<Edge> intersection_edges_prev_lvl = new ArrayList<Edge>();
//		//идем по горизонталям последовательно по возрастанию
//		for(int level = 0; level < sorted_points.size(); level++) //??????? -1
//		{
//			float levelY = sorted_points.get(level).y;
//			//для каждой горизонтали создаем список всех пересекающихся с ней Edge
//			ArrayList<Edge> intersection_edges = new ArrayList<Edge>();
//			for(int i = 0; i < m_edges.size(); i++)
//			{
//				Edge current_edge = m_edges.get(i);
//				if(Edge.IsEdgeIntersectHorizontal(current_edge, levelY))
//				{
//					current_edge.m_intersection_point = Edge.EdgeIntersectionHorizontal(current_edge, levelY);
//					intersection_edges.add(current_edge);
//				}
//			}
//			ArrayList<Edge> intersection_edges_res = new ArrayList<Edge>();
//			intersection_edges_res.addAll(intersection_edges);
//			//для всех горизонталей
//			for(int i = 0; i < intersection_edges.size(); i ++)
//			{
//				Edge current_edge =  intersection_edges.get(i);
//				//  удаляем из списка все пересекающие ее _горизонтальные Edge
//				if(Edge.IsCoincidesHorizontal(current_edge, levelY))
//				{
//					//TODO: ??????
//					if(current_edge.m_start.x > current_edge.m_end.x)
//					{
//						current_edge.Swap();
//					}
//					lines.add(current_edge);
//					//TODO : horizontal edge problems
//					intersection_edges_res.remove(current_edge);
//				}
//				//кроме нижней удаляем из списка все оканчивающиеся/начинающиеся Edge которые были задеты на предыдущей горизонтали
//				//
//				//if(level != sorted_points.size() - 1)
//				{
//					for(int j = 0; j < intersection_edges_prev_lvl.size(); j++)
//					{
//						if((intersection_edges_prev_lvl.get(j) == current_edge)&&(current_edge.HasPointAsEnd(current_edge.m_intersection_point)))
//						{
//							//если ниже из точки есть продолжения
//							int num = 0;
//							for(int k = 0; k < m_edges.size(); k++)
//							{
//								if(m_edges.get(k).HasPointAsEnd(current_edge.m_intersection_point) && m_edges.get(k).center_point().y + Vertex.delta >= levelY)
//								{
//									num++;
//								}
//							}
//							if(num >0)
//							{
//								intersection_edges_res.remove(current_edge);
//							}
//						}
//					}
//				}
//			}
//			//пока оставшийся список не пуст
//			while(intersection_edges_res.size() > 1)
//			{
//				//нашли самую левую точку и удалили ее
//				float left = intersection_edges_res.get(0).m_intersection_point.x;
//				int left_index = 0;
//				for(int i = 1; i < intersection_edges_res.size();i++)
//				{
//					if(intersection_edges_res.get(i).m_intersection_point.x < left)
//					{
//						left = intersection_edges_res.get(i).m_intersection_point.x;
//						left_index = i;
//					}
//				}
//				//еще раз нашли самую левую точку и удалили ее
//				intersection_edges_res.remove(intersection_edges_res.get(left_index));
//				float next_left = intersection_edges_res.get(0).m_intersection_point.x;
//				int left_next_index = 0;
//				for(int i = 1; i < intersection_edges_res.size();i++)
//				{
//					if(intersection_edges_res.get(i).m_intersection_point.x < next_left)
//					{
//						next_left = intersection_edges_res.get(i).m_intersection_point.x;
//						left_next_index = i;
//					}
//				}
//				intersection_edges_res.remove(intersection_edges_res.get(left_next_index));
//				// добавили отрезок между самой левой и следующей за ней
//				lines.add(new Edge(new Vertex(new PointF(left, levelY)), new Vertex(new PointF(next_left, levelY))));
//			}
//			intersection_edges_prev_lvl = intersection_edges;
//		}
//		m_levels = lines;
//		return lines;
//	}
	/**
	 * принадлежность точки многоугольнику через подсчет числа пересечений
	 * @param point интересующая точка
	 * @return true если  число пересечений не четное
	 */
	public boolean IncludePoint(PointF point)
	{
		int count = 0;
		Edge infinity = new Edge(new Vertex(point), new Vertex(new PointF(10000, point.y)));
		for(int i = 0; i < m_points.size()-1; i++)
		{
			if((Math.abs(m_points.get(i).y - point.y) < Vertex.delta) && ((m_points.get(i).x - point.x) > Vertex.delta))
			{
				int next = i+1;
				int prev = i-1;

				if((i+1) == m_points.size() )
				{
					next = 0;
				}
				if((i-1) < 0)
				{
					prev = m_points.size() - 1;
				}
				//PointF point_c = m_points.get(i);
				PointF point_n = m_points.get(next);
				PointF point_p = m_points.get(prev);
				if((point_n.y >= point.y)&&(point_p.y < point.y))
				{
					count++;
				}
				if((point_n.y < point.y)&&(point_p.y >= point.y))
				{
					count++;
				}

			}
			else
			{
				Edge current = new Edge(m_points.get(i), m_points.get(i+1));
				if(current.intersectionAsEdgesStrong(infinity) != null)
				{
					count++;
				}
			}

		}
		if(count%2 == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	/**
	 * для редактируемого слоя. попадание клика в полигон
	 * @param point координаты клика
	 * @return
	 */
	public boolean IsPointInside_widing(PointF point)
	{
		double result = 0;
		if(m_points.size() < 2)
		{
			return false;
		}
		for(int i = 0; i < m_points.size() - 1; i++)
		{
			Edge first = new Edge(point, m_points.get(i));
			Edge second = new Edge(point, m_points.get(i+1));

			double rr = Math.acos(Edge.scalarMultiplication(first, second)/(first.Lenght() * second.Lenght()));
			double ss = Edge.vectorMultiplication(first, second);
			result = result + rr*Math.signum(ss);
		}
		return (Math.abs(result) > 0.1);

	}
	public boolean IsPointInsidePolygon(PointF point)
	{
		//return IncludePoint(point);

		if(!IsPointInside_widing(point))
		{
			return false;
		}
		return true;


		/*else
		{
			if(m_rings != null)
			{
				for(int i = 0; i < m_rings.size(); i++)
				{
					ArrayList<Vertex> array = m_rings.get(i).MakeCC();
					GIGeometryPolygon ring = new GIGeometryPolygon();
					for(int j = 0; j < array.size(); j++)
					{
						ring.add(array.get(j));
					}
					if(ring.IsPointInside(point))
					{
						return false;
					}

				}
			}
			return true;
		}*/
		//return false;
	}

	/**
	 * принадлежность точки многоугольнику через подсчет числа пересечений по Кириусу-Бэку
	 * @param point интересующая точка
	 * @return true если внутри исключительно, если  число пересечений не четное
	 */
	public boolean IsPointInside(PointF point)
	{
		int counter = 0;
		Edge P = new Edge(point, new PointF(point.x + 1000, point.y));
		for(int i = 0; i < m_points.size() - 1; i++)
		{
			//R текущее ребро
			Edge Fi = new Edge(m_points.get(i), m_points.get(i+1));
			//нормаль к ребру
			Edge Ni = Edge.VectorNormal(Fi);
			//скалярное произведение . определяет взаимную ориентацию отрезка и ребра
			float pi = Edge.scalarMultiplication(P, Ni);
			//вектор Qt = V(t) - F начинающийся в начальной точке ребра окна и заканчивающийся в некоторой точке V(t) удлиненной линии.
			Edge Qt = new Edge(m_points.get(i), P.m_start);
			//скалярное произведение  Qt*Ni
			float qi = Edge.scalarMultiplication(Qt, Ni);
			//вырожден в точку либо паралелен стороне
			if(pi == 0)
			{
				//включительно : "<"
				//исключительно : "<="
				if(qi <= 0)
				{
					//вне окна/невидим
					return false;
				}
			}
			else
			{
				//параметрическая координата пересечения
				float t = -1*(qi/pi);
				//точка пересечения в пределах отрезка
				if((t > 0)/*&&(t < 1)*/)
				{
					counter++;
				}
			}
		}
		if(counter%2 == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 *
	 * @return
	 */
	public boolean IsPolyConvexAndCC()
	{
		if(m_points.size() > 3)
		{
			//boolean cc;
			Edge last = new Edge(m_points.get(m_points.size() - 2), m_points.get(m_points.size() - 1));
			Edge first = new Edge(m_points.get(0), m_points.get(1));
			if(Edge.vectorMultiplication(last, first) < 0)
			{
				return false;
			}
			for(int i = 0; i < m_points.size() - 2; i++)
			{
				Edge curr = new Edge(m_points.get(i), m_points.get(i + 1));
				Edge next = new Edge(m_points.get(i + 1), m_points.get(i + 2));
				if(Edge.vectorMultiplication(curr, next) < 0)
				{
					return false;
				}
			}
		}
		return true;
	}
	public KIND getKind()
	{
		int res = iKind();
		return kindes[res];
	}
	/**
	 * первый бит - выпуклость
	 * второй бит - обход по часовой стрелке
	 * @return код
	 */
	public int iKind()
	{
		int kind = -1;
		//int prev = 0;
		if(m_points.size() > 3)
		{
			Edge last = new Edge(m_points.get(m_points.size() - 2), m_points.get(m_points.size() - 1));
			Edge first = new Edge(m_points.get(0), m_points.get(1));
			if(Edge.vectorMultiplication(last, first) < 0)
			{
				kind = 1;
			}
			else
			{
				kind = 0;
			}

			for(int i = 0; i < m_points.size() - 2; i++)
			{
				Edge curr = new Edge(m_points.get(i), m_points.get(i + 1));
				Edge next = new Edge(m_points.get(i + 1), m_points.get(i + 2));
				if(Edge.vectorMultiplication(curr, next) < 0)
				{
					if(kind == -1)
					{
						kind = 1;
					}
					if((kind & 1) == 0)
					{
						kind = kind | 2;
					}

					kind = kind | 1;
				}
				else
				{
					if(kind == -1)
					{
						kind = 0;
					}
					if((kind & 1) != 0)
					{
						kind = kind | 2;
					}

				}
			}
		}
		return kind;
	}
	public ArrayList<Vertex> MakeCC()
	{
		int code = iKind();
		if((code & 2) != 0)
		{
			return null;
		}
		if((code & 1) != 0)
		{
			//int last = m_points.size() - 1;
			//int amount = m_points.size();
			ArrayList<Vertex> result = new ArrayList<Vertex>();
			for(int i = m_points.size()-1; i >= 0; i--)
			{
				//Vertex current = m_points.get(i);
				result.add(m_points.get(i));
			}
			m_points = result;
		}
		return m_points;
	}
}
