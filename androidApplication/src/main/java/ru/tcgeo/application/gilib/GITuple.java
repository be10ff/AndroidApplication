package ru.tcgeo.application.gilib;

import ru.tcgeo.application.gilib.models.GIScaleRange;

public class GITuple
{
    public GILayer layer;
    public boolean visible;
    public GIScaleRange scale_range;

    public int position = -1;

    GITuple(GILayer layer_, boolean visible_, GIScaleRange scale_range_) {
        layer = layer_;
        visible = visible_;
		scale_range = scale_range_;
	}
}