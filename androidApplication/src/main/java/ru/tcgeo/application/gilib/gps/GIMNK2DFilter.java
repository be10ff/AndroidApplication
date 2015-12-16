package ru.tcgeo.application.gilib.gps;

import java.util.ArrayList;

import android.location.Location;

public class GIMNK2DFilter 
{

	public class ParametricPoint
	{
		public double m_X;
		public double m_Y;
		public double m_t;
		public ArrayList<ParametricPoint> m_values;
		ParametricPoint()
		{
			m_X = 0;
			m_Y = 0;
			m_t = 0;
		}
		ParametricPoint(double x, double y, double t)
		{
			m_X = x;
			m_Y = y;
			m_t = t;
		}
	}

	private ArrayList<ParametricPoint> m_values;
	private int m_deep;
	
	public GIMNK2DFilter(int deep) 
	{
		m_deep = deep;
		m_values = new ArrayList<GIMNK2DFilter.ParametricPoint>();
	}
	public void addValue(double x, double y, double t)
	{
		addValue(new ParametricPoint(x, y, t));
	}
	
	public void addValue(ParametricPoint value)
	{
		if(m_values.size() == m_deep)
		{
			m_values.remove(0);
		}
		m_values.add(value);
	}

	public void addValue(Location location)
	{
		addValue(new ParametricPoint(location.getLongitude(), location.getLatitude(), location.getTime()));
	}
	/**
	 * аппороксимация набора точкой Методом Наименьших Квадратов
	 * @return
	 */
	public ParametricPoint geMNT_asPoint()
	{
		double res = 0;//m_values.get(0).getValue();
		double x = 0;
		double y = 0;
		double t = 0;
		for(int i = 0; i < m_values.size(); i++)
		{
			x +=  m_values.get(i).m_X;
			y +=  m_values.get(i).m_Y;
			t +=  m_values.get(i).m_t;
		}
		x = x/m_values.size();
		y = y/m_values.size();
		t = t/m_values.size();
		return new ParametricPoint(x, y, t);
	}
	/**
	 * аппороксимация набора линией Методом Наименьших Квадратов
	 * x = a1*t + b1;
	 * y = a2t + b2
	 * @return
	 */
	public ParametricPoint[] getMNT_asLine()
	{
		double summ_x = 0;
		double summ_y = 0;
		double summ_yt = 0;
		double summ_xt = 0;
		double summ_t = 0;
		double summ_t_2 = 0;
		int n = m_values.size();
		for(int i = 0; i < m_values.size(); i++)
		{
			summ_x +=  m_values.get(i).m_X;
			summ_y +=  m_values.get(i).m_Y;
			summ_t +=  m_values.get(i).m_t;
			summ_xt += m_values.get(i).m_X*m_values.get(i).m_t;
			summ_yt += m_values.get(i).m_Y*m_values.get(i).m_t;
			summ_t_2 +=  m_values.get(i).m_t*m_values.get(i).m_t;
		}
		double a1 = (n*summ_xt - summ_x*summ_t)/(n*summ_t_2 -summ_t*summ_t);
		double b1 = (summ_x - a1*summ_t)/n;
		double a2 = (n*summ_yt - summ_y*summ_t)/(n*summ_t_2 -summ_t*summ_t);
		double b2 = (summ_y - a1*summ_t)/n;

		
		double xs = a1*m_values.get(0).m_t + b1;
		double ys = a2*m_values.get(0).m_t + b2;
		double xe = a1*m_values.get(m_values.size() - 1).m_t + b1;
		double ye = a2*m_values.get(m_values.size() - 1).m_t + b2;
		
		ParametricPoint s = new ParametricPoint(xs, ys, m_values.get(0).m_t);
		ParametricPoint e = new ParametricPoint(xs, ys, m_values.get(m_values.size() - 1).m_t);
		
		ParametricPoint[] result = new ParametricPoint[2];
		result[0] = s;
		result[1] = e;
		return result;
	}
	public ParametricPoint get_asDelta()
	{
		double summ_x = 0;
		double summ_y = 0;
		double summ_yt = 0;
		double summ_xt = 0;
		double summ_t = 0;
		double summ_t_2 = 0;
		int n = m_values.size();
		for(int i = 0; i < m_values.size(); i++)
		{
			summ_x +=  m_values.get(i).m_X;
			summ_y +=  m_values.get(i).m_Y;
			summ_t +=  m_values.get(i).m_t;
			summ_xt += m_values.get(i).m_X*m_values.get(i).m_t;
			summ_yt += m_values.get(i).m_Y*m_values.get(i).m_t;
			summ_t_2 +=  m_values.get(i).m_t*m_values.get(i).m_t;
		}
		double a1 = (n*summ_xt - summ_x*summ_t)/(n*summ_t_2 -summ_t*summ_t);
		double b1 = (summ_x - a1*summ_t)/n;
		double a2 = (n*summ_yt - summ_y*summ_t)/(n*summ_t_2 -summ_t*summ_t);
		double b2 = (summ_y - a1*summ_t)/n;

		double dt = m_values.get(m_values.size() - 1).m_t - m_values.get(0).m_t;
		double dx = a1*dt;
		double dy = a2*dt;
		return new ParametricPoint(dx, dy, dt);
	}
	
	public static double GetDistanceBetween(double from_lon, double from_lat, double to_lon, double to_lat)
	{
		double slat= from_lat;
		double slon= from_lon;
		double flat= to_lat;
		double flon= to_lon;
		
		double lat1=Math.toRadians(slat);
		double lon1=Math.toRadians(slon);
		double lat2=Math.toRadians(flat);
		double lon2=Math.toRadians(flon);

		double cl1 = Math.cos(lat1);
		double cl2 = Math.cos(lat2);
		double sl1 = Math.sin(lat1);
		double sl2 = Math.sin(lat2);

		double delta = lon2 - lon1;
		double cdelta = Math.cos(delta);
		double sdelta = Math.sin(delta);
		
		double y = Math.hypot(cl2*sdelta, cl1*sl2 - sl1*cl2*cdelta);
		double x = sl1*sl2 + cl1*cl2*cdelta;
		double ad = Math.atan2(y, x);
		double dist = ad*6372795;
		return dist;
	}
	public static double GetAzimuth(double from_lon, double from_lat, double to_lon, double to_lat)
	{
		double slat= from_lat;
		double slon= from_lon;
		double flat= to_lat;
		double flon= to_lon;
		
		double lat1=Math.toRadians(slat);
		double lon1=Math.toRadians(slon);
		double lat2=Math.toRadians(flat);
		double lon2=Math.toRadians(flon);

		double cl1 = Math.cos(lat1);
		double cl2 = Math.cos(lat2);
		double sl1 = Math.sin(lat1);
		double sl2 = Math.sin(lat2);

		double delta = lon2 - lon1;

		double cdelta = Math.cos(delta);
		double sdelta = Math.sin(delta);
		
		double x = (cl1*sl2) - (sl1*cl2*cdelta);
		double y = sdelta*cl2;
		double z = Math.toDegrees(Math.atan(-y/x));
		if(x < 0)
		{
			z = z + 180.;
		}
		double z2 = ((z + 180.)%360.) - 180.;
		z2 = - Math.toRadians(z2);
		double anglerad2 = z2 - ((2*Math.PI)*Math.floor(z2/(2*Math.PI)));
		double angledeg = Math.toDegrees(anglerad2);
		return angledeg;
	}
}
