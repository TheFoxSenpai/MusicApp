package org.example.View;
import org.example.Model.SongModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionListener;
import java.awt.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class SwingView {
    private JFrame frame;
    private JDialog loadingDialog;
    private JPanel controlPanel;
    private JButton deleteSongButton;
    private JButton recognizeSongButton;
    private JButton createPlaylistButton;
    private JButton deletePlaylistButton;
    private ListView listView;

    public JFrame getFrame() {
        return frame;
    }
    public SongModel getSelectedSong() {
        return listView.getSelectedSong();
    }
    public String getSelectedPlaylist() {
        return listView.getSelectedPlaylist();
    }
    public void addPlaylistSelectionListener(ListSelectionListener listener) {
        listView.addPlaylistSelectionListener(listener);
    }

    public SwingView() {

        // create main frame
        frame = new JFrame("MP3 Player");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create loading dialog
        loadingDialog = new JDialog(frame, "Loading...", true);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Add the loading gif
        URL loadingGifUrl = getClass().getResource("/Search.gif");
        ImageIcon loadingIcon = new ImageIcon(loadingGifUrl);
        JLabel loadingLabel = new JLabel("Recognizing song...", loadingIcon, JLabel.CENTER);

        loadingDialog.add(loadingLabel);
        loadingDialog.setSize(300, 150);

        listView = new ListView();

        // create control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        // create recognize song button
        recognizeSongButton = new JButton("Recognize Song");
        recognizeSongButton.setFont(new Font("Arial", Font.BOLD, 24));
        recognizeSongButton.setPreferredSize(new Dimension(200, 100));
        controlPanel.add(recognizeSongButton, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Adjust spacing here
        createPlaylistButton = new JButton("Create Playlist");
        deletePlaylistButton = new JButton("Delete Playlist");
        deleteSongButton = new JButton("Delete Song");
        buttonPanel.add(createPlaylistButton);
        buttonPanel.add(deletePlaylistButton);
        buttonPanel.add(deleteSongButton);


        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60)); // Adjust spacing here

        // add control panel to frame

        frame.add(controlPanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.CENTER);
        frame.add(listView.getComponent(), BorderLayout.CENTER);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void showLoadingScreen() {
        loadingDialog.setLocationRelativeTo(frame);
        loadingDialog.setVisible(true);
    }

    public void hideLoadingScreen() {
        loadingDialog.setVisible(false);
    }

    public void addDeleteSongListener(ActionListener actionListener) {
        deleteSongButton.addActionListener(actionListener);
    }
    public void addRecognizeSongListener(ActionListener actionListener) {
        recognizeSongButton.addActionListener(actionListener);
    }

    public String getSelectedSongPath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("MP3 Files", "mp3"));
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    public void addCreatePlaylistListener(ActionListener actionListener) {
        createPlaylistButton.addActionListener(actionListener);
    }

    public void addDeletePlaylistListener(ActionListener actionListener) {
        deletePlaylistButton.addActionListener(actionListener);
    }

    public void updatePlaylistView(List<String> playlists) {
        listView.updatePlaylistView(playlists);
    }

    public void updateSongView(List<SongModel> songs) {
        listView.updateSongView(songs);
    }
}