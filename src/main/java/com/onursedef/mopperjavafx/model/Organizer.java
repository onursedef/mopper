package com.onursedef.mopperjavafx.model;

public class Organizer {
    private Integer id;
    private String name;
    private String extensions;
    private String path;

    public Organizer() {}

    public Organizer(Integer id, String name, String extensions, String path) {
        this.id = id;
        this.name = name;
        this.extensions = extensions;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExtensions() {
        return extensions;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", extensions='" + extensions + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
