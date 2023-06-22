package org.example.Model;

public class SongModel {
    private int id;
    private String name;
    private String artist;
    private String url;

    public SongModel(String name, String url) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.url = url;
    }

    public String getName() {
        return name;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUrl() {
        return url;
    }
    public int getId() {  // Add this getter
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}