package ru.tcgeo.application.gilib.models;

import android.graphics.PointF;

import java.util.ArrayList;

import ru.tcgeo.application.gilib.planimetry.Vertex;


public class GITile
{
	public ArrayList<Vertex> m_points;
	public String m_filename;
	float m_lower;
	float m_upper;
	public GIScaleRange m_range;

	public GITile(String filename, float lower, float upper)
	{
		m_filename = filename;
		m_lower = lower;
		m_upper = upper;
		m_range = new GIScaleRange(lower, upper);
		m_points = new ArrayList<Vertex>();
	}

	public void add(PointF point)
	{
		m_points.add(new Vertex(point));
	}

}
