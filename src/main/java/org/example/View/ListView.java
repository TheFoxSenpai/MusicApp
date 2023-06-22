package org.example.View;

import org.example.Model.SongModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class ListView {
    private JList<String> playlistView;
    private DefaultListModel<String> playlistModel;
    private JList<SongModel> songView;
    private DefaultListModel<SongModel> songModel;
    private JScrollPane playlistScrollPane;
    private JScrollPane songScrollPane;
    private JPanel panel;

    public ListView() {
        panel = new JPanel(new BorderLayout());

        playlistModel = new DefaultListModel<>();
        playlistView = new JList<>(playlistModel);
        playlistView.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setPreferredSize(new Dimension(130, c.getPreferredSize().height));
                return c;
            }
        });

        playlistScrollPane = new JScrollPane(playlistView);

        songModel = new DefaultListModel<>();
        songView = new JList<>(songModel);
        songView.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList<SongModel> list = (JList<SongModel>)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    SongModel song = list.getModel().getElementAt(index);
                    try {
                        Desktop.getDesktop().browse(new URI(song.getUrl()));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        songScrollPane = new JScrollPane(songView);

        panel.add(playlistScrollPane, BorderLayout.WEST);
        panel.add(songScrollPane, BorderLayout.CENTER);
    }

    public Component getComponent() {
        return panel;
    }
    public SongModel getSelectedSong() {
        return songView.getSelectedValue();
    }
    public String getSelectedPlaylist() {
        return playlistView.getSelectedValue();
    }
    public void addPlaylistSelectionListener(ListSelectionListener listener) {
        playlistView.addListSelectionListener(listener);
    }
    public void updatePlaylistView(List<String> playlists) {
        playlistModel.clear();
        for (String playlist : playlists) {
            playlistModel.addElement(playlist);
        }
    }

    public void updateSongView(List<SongModel> songs) {
        songModel.clear();
        for (SongModel song : songs) {
            songModel.addElement(song);
        }
    }
}