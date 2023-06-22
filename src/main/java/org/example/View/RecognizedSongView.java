package org.example.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;

public class RecognizedSongView {
    private JDialog dialog;
    private JLabel songTitleLabel;
    private JLabel songArtistLabel;
    private JLabel songImageLabel;
    private JButton saveButton;
    private JButton skipButton;
    private JPanel linkPanel;
    private JLabel youtubeLabel;


    public RecognizedSongView(JFrame parent) {
        // create dialog
        dialog = new JDialog(parent, "Recognized Song", true);
        dialog.setSize(500, 600);
        dialog.setLayout(new BorderLayout());

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        songTitleLabel = new JLabel();
        songTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        songArtistLabel = new JLabel();
        songArtistLabel.setHorizontalAlignment(JLabel.CENTER);

        textPanel.add(songTitleLabel);
        textPanel.add(songArtistLabel);

        songImageLabel = new JLabel();
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.add(songImageLabel);

        linkPanel = new JPanel(new GridLayout(1, 4));
        linkPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JLabel listenLabel = new JLabel("Listen it here:");
        listenLabel.setHorizontalAlignment(JLabel.CENTER);
        linkPanel.add(listenLabel);
        youtubeLabel = createServiceIcon("yt.png");
        linkPanel.add(youtubeLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        saveButton = new JButton("Save to Playlist");
        skipButton = new JButton("Skip");
        buttonPanel.add(saveButton);
        buttonPanel.add(skipButton);

        dialog.add(textPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(imagePanel, BorderLayout.CENTER);
        centerPanel.add(linkPanel, BorderLayout.SOUTH);
        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    }

    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
    private JLabel createServiceIcon(String logoFilename) {
        URL imageUrl = getClass().getClassLoader().getResource(logoFilename);
        ImageIcon icon = new ImageIcon(imageUrl);
        Image img = icon.getImage();
        Image newImg = getScaledImage(img, 50, 38); // scale the image
        icon = new ImageIcon(newImg);
        JLabel label = new JLabel("", icon, JLabel.CENTER);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    public void show(String songTitle, String songArtist, BufferedImage songImage , String youtubeURL) {
        System.out.println("Title: " + songTitle);
        System.out.println("Artist: " + songArtist);
        System.out.println("Image: " + songImage);
        System.out.println("Youtube URL: " + youtubeURL);
        songTitleLabel.setText("Title: " + songTitle);
        songArtistLabel.setText("Artist: " + songArtist);
        songImageLabel.setIcon(new ImageIcon(songImage.getScaledInstance(400, 400, Image.SCALE_DEFAULT)));
        // Remove old mouse listeners
        for(MouseListener ml : youtubeLabel.getMouseListeners()){
            youtubeLabel.removeMouseListener(ml);
        }

        // Add new mouse listener
        youtubeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URL(youtubeURL).toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        dialog.pack();
        dialog.setVisible(true);
    }

    public void hide() {
        dialog.setVisible(false);
    }
    public static void removeActionListeners(JButton button) {
        for (ActionListener al : button.getActionListeners()) {
            button.removeActionListener(al);
        }
    }
    public void addSaveButtonListener(ActionListener actionListener) {
        // remove old action listeners
        removeActionListeners(saveButton);
        // add new action listener
        saveButton.addActionListener(actionListener);
    }

    public void addSkipButtonListener(ActionListener actionListener) {
        skipButton.addActionListener(actionListener);
    }
}