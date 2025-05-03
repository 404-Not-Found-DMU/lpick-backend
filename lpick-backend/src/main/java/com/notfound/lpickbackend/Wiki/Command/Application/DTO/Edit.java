package com.notfound.lpickbackend.Wiki.Command.Application.DTO;

public class Edit {
    public enum Type { REMOVE, ADD }
    public final Type type;
    public final int oldIndex;
    public final int newIndex;
    public Edit(Type type, int oldIndex, int newIndex) {
        this.type = type;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }
}