package ru.tcgeo.application.layer.requestor;


import ru.tcgeo.application.gilib.models.GIGeometry;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.layer.GILayer;

public interface GIDataRequestor
{
	boolean needsHierarchicalView();

	GIDataRequestor StartGatheringData(GILonLat point);
	GIDataRequestor EndGatheringData (GILonLat point);

	GIDataRequestor StartHierarchyLevel();
	GIDataRequestor EndHierarchyLevel();

	GIDataRequestor StartLayer(GILayer layer);
	GIDataRequestor EndLayer(GILayer layer);

	GIDataRequestor StartObject(GIGeometry geometry);
	GIDataRequestor EndObject(GIGeometry geometry);

	GIDataRequestor ProcessSemantic(String name, String value);
}
