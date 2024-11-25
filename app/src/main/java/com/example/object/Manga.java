package com.example.object;

public class Manga {
    private String id, name, src;

    public Manga() {
    }

    public Manga(String id, String name, String src) {
        this.id = id;
        this.src = src;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
