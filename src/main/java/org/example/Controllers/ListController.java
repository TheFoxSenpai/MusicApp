package org.example.Controllers;

import org.example.Model.DatabaseModel;
import org.example.Model.PlaylistModel;
import org.example.Model.SongModel;
import org.example.View.ListView;
import org.example.View.SwingView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListController {
    private DatabaseModel databaseModel;
    private SwingView swingView;

    public ListController(DatabaseModel databaseModel, SwingView swingView) {
        this.databaseModel = databaseModel;
        this.swingView = swingView;

        // Add action listeners to the buttons
        swingView.addCreatePlaylistListener(new CreatePlaylistListener());

        swingView.addDeleteSongListener(new DeleteSongListener());

        swingView.addDeletePlaylistListener(new DeletePlaylistListener());


        swingView.addPlaylistSelectionListener(new PlaylistSelectionListener());
    }

    public void updatePlaylistView() {
        List<String> playlists = databaseModel.getAllPlaylists();
        swingView.updatePlaylistView(playlists);
    }

    private class CreatePlaylistListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String playlistName = JOptionPane.showInputDialog(swingView.getFrame(), "Enter playlist name:");
            if (playlistName != null && !playlistName.isEmpty()) {
                PlaylistModel playlist = new PlaylistModel(playlistName, null);
                databaseModel.savePlaylist(playlist);
                updatePlaylistView();
            }
        }
    }
    private class DeleteSongListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SongModel selectedSong = swingView.getSelectedSong();
            String selectedPlaylist = swingView.getSelectedPlaylist();
            if (selectedSong != null && selectedPlaylist != null) {
                int dialogResult = JOptionPane.showConfirmDialog(swingView.getFrame(),
                        "Are you sure you want to delete this song from the playlist?",
                        "Delete Song", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    int playlistId = databaseModel.getPlaylistId(selectedPlaylist);
                    int songId = selectedSong.getId();
                    databaseModel.deleteSongFromPlaylist(playlistId, songId);
                    // Update the song view after deletion
                    List<SongModel> songs = databaseModel.getPlaylistSongs(playlistId);
                    swingView.updateSongView(songs);
                }
            }
        }
    }
    private class DeletePlaylistListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedPlaylist = swingView.getSelectedPlaylist();
            if (selectedPlaylist != null && !selectedPlaylist.isEmpty()) {
                int dialogResult = JOptionPane.showConfirmDialog(swingView.getFrame(),
                        "Are you sure you want to delete this playlist?",
                        "Delete Playlist", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    databaseModel.deletePlaylist(selectedPlaylist);
                    updatePlaylistView();
                }
            }
        }
    }
    private class PlaylistSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                JList<String> source = (JList<String>) e.getSource();
                String selectedPlaylistName = source.getSelectedValue();
                int playlistId = databaseModel.getPlaylistId(selectedPlaylistName);
                List<SongModel> songs = databaseModel.getPlaylistSongs(playlistId);
                swingView.updateSongView(songs);
            }
        }
    }
}