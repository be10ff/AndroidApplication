package ru.tcgeo.application.gilib.models;

public class LonLatEvent {

    public static final int FLAG_RUNNING = 0b00000001;
    public static final int FLAG_TRACK = 0b00000010;
    public static final int FLAG_FOLLOW = 0b00000100;
    public static final int FLAG_GPS = 0b00000111;

    public GILonLat lonlat;
    public float accurancy;
    public int actions;
}
