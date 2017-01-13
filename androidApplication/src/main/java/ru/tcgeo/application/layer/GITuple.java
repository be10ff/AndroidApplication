package ru.tcgeo.application.layer;

import ru.tcgeo.application.gilib.models.GIScaleRange;
import ru.tcgeo.application.layer.GILayer;

public class GITuple
{
	GITuple (GILayer layer_, boolean visible_, GIScaleRange scale_range_)
	{
		layer = layer_;
		visible = visible_;
		scale_range = scale_range_;
		
	}
	public GILayer layer;
	public boolean visible;
	public GIScaleRange scale_range;
}