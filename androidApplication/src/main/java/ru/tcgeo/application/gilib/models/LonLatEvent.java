package ru.tcgeo.application.gilib.models;

public class LonLatEvent {

    public static final int FLAG_PAUSED = 0x00000001;
    public static final int FLAG_TRACK = 0x00000010;
    public static final int FLAG_FOLLOW = 0x00000100;

    public GILonLat lonlat;
    public float accurancy;
    public int actions;
}
