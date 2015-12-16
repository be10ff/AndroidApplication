package ru.tcgeo.application.gilib.models;

import ru.tcgeo.application.gilib.parser.GIRange;

public class GIScaleRange
{
	private double m_min;
	private double m_max;
	
	public GIScaleRange ()
	{
		m_min = -1.0f;
		m_max = -1.0f;
	}
	public GIScaleRange (GIRange range)
	{
		if(range == null)
		{
			m_min = -1.0f;
			m_max = -1.0f;
		}
		else
		{
			m_min = 1/((double)range.m_from);
			m_max = 1/((double)range.m_to);
		}
	}
	/** 
	 * -1.0f means NO_LIMIT
	 * @param min
	 * @param max
	 */
	public GIScaleRange (double min, double max)
	{
		m_min = -1.0f;
		m_max = -1.0f;
		if(min != 0.0f)
		{
			m_min = min;
		}
		if(max != 0.0f)
		{
			m_max = max;
		}
	}
	
	public void set (double min, double max)
	{
		m_min = min;
		m_max = max;
	}
	
	public boolean IsWithinRange (double scale)
	{
		if (scale > m_max && -1.0f != m_max)
			return false;
		
		if (scale < m_min) // scale can't be negative
			return false;
			
		return true;
	}


	public double getMin() {
		return m_min;
	}

	public void setMin(double m_min) {
		this.m_min = m_min;
	}

	public double getMax() {
		return m_max;
	}

	public void setMax(double m_max) {
		this.m_max = m_max;
	}

//	public void setIntMin(int min){
//		m_min = 1/((double)min);
//	}
//
//	public void setIntMax(int max){
//		m_max = 1/((double)max);
//	}
}
