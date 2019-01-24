package ru.tcgeo.application.data.gilib.models;

/**
 * Created by artem on 18.07.17.
 */

public class Attribute {
    public String name;
    public String value;

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name;

    }
}
