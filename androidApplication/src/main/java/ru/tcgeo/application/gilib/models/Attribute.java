package ru.tcgeo.application.gilib.models;

/**
 * Created by artem on 18.07.17.
 */

public class Attribute {
    final public String name;
    final public String value;
    public int type;

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
        type = 0;
    }

    public Attribute(String name, String value, int type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;

    }
}
