package ru.tcgeo.application.gilib.models;


public class GIBounds
{
	private double       m_top;
	private double       m_left;
	private double       m_right;
	private double       m_bottom;
	private GIProjection m_projection;

	public GIBounds (GIProjection projection, double left, double top,
	        double right, double bottom)
	{
		m_left = left;
		m_top = top;
		m_right = right;
		m_bottom = bottom;

		m_projection = projection;
	}

	public GIBounds (GIProjection projection, GILonLat center, double width,
	        double height)
	{
		m_left = center.lon() - width / 2;
		m_right = center.lon() + width / 2;
		m_top = center.lat() + height / 2;
		m_bottom = center.lat() - height / 2;

		m_projection = projection;
	}
	public void set(double left, double top, double right, double bottom)
	{
		m_left = left;
		m_top = top;
		m_right = right;
		m_bottom = bottom;
	}

	public GIProjection projection ()
	{
		return m_projection;
	}

	public double width ()
	{
		return m_right - m_left;
	}

	public double height ()
	{
		return m_top - m_bottom;
	}

	public double top ()
	{
		return m_top;
	}

	public double left ()
	{
		return m_left;
	}

	public double right ()
	{
		return m_right;
	}

	public double bottom ()
	{
		return m_bottom;
	}

	public GILonLat TopLeft ()
	{
		return new GILonLat(m_left, m_top);
	}
	public GILonLat Center ()
	{
		return new GILonLat((m_right+m_left)/2, (m_top + m_bottom)/2);
	}
	
	public GILonLat TopRight ()
	{
		return new GILonLat(m_right, m_top);
	}

	public GILonLat BottomLeft ()
	{
		return new GILonLat(m_left, m_bottom);
	}

	public GILonLat BottomRight ()
	{
		return new GILonLat(m_right, m_bottom);
	}

	public Boolean ContainsBounds (GIBounds other)
	{
		if(m_left <= other.m_left && m_right >= other.m_right && m_top >= other.m_top && m_bottom <= other.m_bottom)
		{
			return true;
		}
		return false;
	}

	public Boolean ContainsPoint (GILonLat raw_point)
	{
		GILonLat point = GIProjection.ReprojectLonLat(raw_point, GIProjection.WGS84(), m_projection);
		if(m_left <= point.lon() && m_right >= point.lon() && m_top >= point.lat() && m_bottom <= point.lat())
		{
			return true;
		}
		return false;
	}

	public Boolean Intersects (GIBounds with)
	{
		if(m_left > with.m_right || m_right < with.m_left || m_top < with.m_bottom || m_bottom > with.m_top)
		{
			return false;
		}
		return true;
	}

	public GIBounds Intersect (GIBounds with)
	{
		if(Intersects(with))
		{
			double left = Math.max(m_left, with.m_left);
			double right = Math.min(m_right, with.m_right);
			double top = Math.max(m_top, with.m_top);
			double bottom = Math.min(m_bottom, with.m_bottom);
			return new GIBounds(m_projection, left, top, right, bottom);
		}
		return null;
	}

	public GIBounds Reprojected (GIProjection destProjection)
	{
		GILonLat TopLeft_new = 		GIProjection.ReprojectLonLat(this.TopLeft(), this.projection(), destProjection);
		GILonLat BottomRight_new = 	GIProjection.ReprojectLonLat(this.BottomRight(), this.projection(), destProjection);
		return new GIBounds(destProjection, TopLeft_new.lon(), TopLeft_new.lat(),
											BottomRight_new.lon(), BottomRight_new.lat());
	}
	
	@Override
	public String toString()
	{
		String res = "";
		return res +	m_left + " " + m_top + " " + m_right + " " + m_bottom;
	}
}
