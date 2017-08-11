package ru.tcgeo.application.gilib;

import ru.tcgeo.application.gilib.models.GIColor;

public class GITuple
{
    public GILayer layer;
    public boolean visible;
//    public GIScaleRange scale_range;

    public int position = -1;

    GITuple(GILayer layer, boolean visible) {
        this.layer = layer;
        this.visible = visible;
//		scale_range = scale_range_;
    }

    public static class Builder {
        GILayer.Builder layerBuilder;
        GITuple tuple;
        boolean visibility;
//        GIScaleRange scaleRange;

        public Builder(GITuple tuple) {
            this.tuple = tuple;
            layerBuilder = new GILayer.Builder(tuple.layer);
            visibility = tuple.visible;
//            scaleRange = tuple.scale_range;
        }

        //tuple
        public Builder visibility(boolean visibility) {
            this.visibility = visibility;
            return this;
        }
//
//        public Builder scaleRange(GIScaleRange scaleRange) {
//            this.scaleRange = scaleRange;
//            return this;
//        }

        //layer
        public Builder name(String name) {
            layerBuilder.name(name);
            return this;
        }

        public Builder type(GILayer.GILayerType type) {
            layerBuilder.type(type);
            return this;
        }

        public Builder enabled(boolean enabled) {
            layerBuilder.enabled(enabled);
            return this;
        }

        public Builder sourceLocation(String location) {
            layerBuilder.sourceLocation(location);
            return this;
        }

        public Builder sourceName(String name) {
            layerBuilder.name(name);
            return this;
        }

        public Builder styleType(String type) {
            layerBuilder.styleType(type);
            return this;
        }

        public Builder styleLineWidth(double width) {
            layerBuilder.styleLineWidth(width);
            return this;
        }

        public Builder styleOpacity(double opacity) {
            layerBuilder.styleOpacity(opacity);
            return this;
        }

        public Builder styleColor(GIColor color) {
            layerBuilder.styleColor(color);
            return this;
        }

        public Builder rangeFrom(int from) {
            layerBuilder.rangeFrom(from);
//            this.scaleRange = new GIScaleRange(layerBuilder.builder.rangeBuilder.build());
            return this;
        }

        public Builder rangeTo(int to) {
            layerBuilder.rangeTo(to);
//            this.scaleRange = new GIScaleRange(layerBuilder.builder.rangeBuilder.build());
            return this;
        }

        public Builder sqldbMaxZ(int maxZ) {
            layerBuilder.sqldbMaxZ(maxZ);
            return this;
        }

        public Builder sqldbMinZ(int minZ) {
            layerBuilder.sqldbMinZ(minZ);
            return this;
        }

        public Builder sqldbZoomType(String zoomType) {
            layerBuilder.sqldbZoomType(zoomType);
            return this;
        }

        public Builder sqldbRatio(int ratio) {
            layerBuilder.sqldbRatio(ratio);
            return this;
        }

        public GITuple build() {
            tuple.visible = visibility;
//            tuple.scale_range = scaleRange;
            tuple.layer = layerBuilder.build();
            return tuple;
        }
    }
}