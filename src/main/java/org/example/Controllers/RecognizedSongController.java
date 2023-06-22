package org.example.Controllers;

import org.example.Model.DatabaseModel;
import org.example.View.RecognizedSongView;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

public class RecognizedSongController {
    private DatabaseModel model;
    private RecognizedSongView view;
    private String songTitle;
    private String songArtist;
    private BufferedImage songImage;
    private String youtubeURL;


    public RecognizedSongController(DatabaseModel model, RecognizedSongView view, String songTitle, String songArtist, BufferedImage songImage , String youtubeURL) {
        this.model = model;
        this.view = view;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songImage = songImage;
        this.youtubeURL = youtubeURL;

        view.addSaveButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPlaylistName = showPlaylistSelectionDialog();
                if (selectedPlaylistName != null) {
                    int playlistId = model.getPlaylistId(selectedPlaylistName);

                    // The updated saveSong method now returns the song ID
                    int songId = model.saveSong(songTitle, youtubeURL);

                    model.savePlaylistSong(playlistId, songId);
                    view.hide();
                }
            }
        });

        view.addSkipButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.hide();
            }
        });

    }
    private String showPlaylistSelectionDialog() {
        List<String> playlists = model.getAllPlaylists();
        String[] choices = new String[playlists.size()];
        choices = playlists.toArray(choices);

        String selectedPlaylist = (String) JOptionPane.showInputDialog(null,
                "Choose playlist",
                "Choose playlist",
                JOptionPane.QUESTION_MESSAGE,
                null,
                choices,
                choices[0]);

        return selectedPlaylist;
    }

}