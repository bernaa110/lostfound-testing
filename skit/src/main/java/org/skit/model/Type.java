package org.skit.model;

public enum Type{
    LOST("Изгубено"),
    FOUND("Најдено");

    private final String typeName;

    Type(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
