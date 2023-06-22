package org.example.Model;

import java.util.ArrayList;
import java.util.List;

public class PlaylistModel {
    private String name;
    private List<Integer> songIds;
    public PlaylistModel(String name, List<Integer> songIds) {
        this.name = name;
        this.songIds = songIds != null ? songIds : new ArrayList<>();
    }


    public String getName() {
        return this.name;
    }

    public List<Integer> getSongIds() {
        return this.songIds;
    }
}