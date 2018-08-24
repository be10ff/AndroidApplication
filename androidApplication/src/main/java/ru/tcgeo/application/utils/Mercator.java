package ru.tcgeo.application.utils;

/**
 * Created by a_belov on 28.05.15.
 */
public class Mercator {
    final private static double R_MAJOR = 6378137.0;
    final private static double R_MINOR = 6356752.3142;

    public static double[] merc(double x, double y) {
        return new double[]{mercX(x), mercY(y)};
    }

    private static double mercX(double lon) {
        return R_MAJOR * Math.toRadians(lon);
    }

    private static double mercY(double lat) {
        if (lat > 89.5) {
            lat = 89.5;
        }
        if (lat < -89.5) {
            lat = -89.5;
        }
        double temp = R_MINOR / R_MAJOR;
        double es = 1.0 - (temp * temp);
        double eccent = Math.sqrt(es);
        double phi = Math.toRadians(lat);
        double sinphi = Math.sin(phi);
        double con = eccent * sinphi;
        double com = 0.5 * eccent;
        con = Math.pow(((1.0 - con) / (1.0 + con)), com);
        double ts = Math.tan(0.5 * ((Math.PI * 0.5) - phi)) / con;
        double y = 0 - R_MAJOR * Math.log(ts);
        return y;
    }


    public static String getCanonicalCoordString(double coord) {
        long degrees = (long) Math.floor(coord) ;
        long mins = (long) Math.floor((coord - degrees) * 60) ;
        double secs = ((coord - degrees) * 60 - mins) * 60;
        String res = String.format("%02d%02d%07.4f", degrees, mins, secs);
        return res.replace(",", "");
    }

    public static String getGradMinCoordString(double coord) {
        long degrees = (long) Math.floor(coord) ;
        double mins =  (coord - degrees) * 60  ;
        String res = String.format("%02d%09.6f", degrees, mins);
        return res.replace(",", "");
    }

    public static String getGradCoordString(double coord) {
        long degrees = (long) Math.round(coord * 100000000) ;
        String res = String.valueOf(degrees);
        return String.valueOf(degrees);

    }
}
