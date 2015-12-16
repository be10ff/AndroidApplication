package ru.tcgeo.application.utils;

import ru.tcgeo.application.gilib.models.GILonLat;

/**
 * Created by a_belov on 06.07.15.
 */
public class MapUtils {
    public static double GetDistance(GILonLat from, GILonLat to)
    {
        double slat= from.lat();
        double slon= from.lon();
        double flat= to.lat();
        double flon= to.lon();

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
    public static double GetDistanceBetween(GILonLat from, GILonLat to)
    {
		/*double slat= from.lat();
		double slon= from.lon();
		double flat= to.lat();
		double flon= to.lon();

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
		return dist;*/
        return GetDistance(from, to);
    }

    public static double GetAzimuth(GILonLat from, GILonLat to)
    {
        double slat= from.lat();
        double slon= from.lon();
        double flat= to.lat();
        double flon= to.lon();

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
        double z = Math.toDegrees(Math.atan(-y / x));
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

    public static double GetRadBetween(GILonLat from, GILonLat to)
    {
        double slat= from.lat();
        double slon= from.lon();
        double flat= to.lat();
        double flon= to.lon();

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
        return ad;
    }
    public static double GetAngle_A_OfTriangle(GILonLat A, GILonLat B, GILonLat C)
    {
        double a = GetRadBetween(B, C);
        double b =  GetRadBetween(C, A);
        double c = GetRadBetween(A, B);

        double angle = Math.acos((Math.cos(a) - Math.cos(b)*Math.cos(c))/(Math.sin(b)*Math.sin(c)));
        return angle;
    }
}
