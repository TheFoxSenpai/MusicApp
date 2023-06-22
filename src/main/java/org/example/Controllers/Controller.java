package org.example.Controllers;

import it.sauronsoftware.jave.*;
import org.example.Model.DatabaseModel;

import org.example.Model.ShazamModel;
import org.example.View.RecognizedSongView;
import org.example.View.SwingView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class Controller {
    private DatabaseModel model;
    private ShazamModel shazam;
    private SwingView view;

    public Controller(DatabaseModel model, ShazamModel shazam, SwingView view) {
        this.model = model;
        this.shazam = shazam;
        this.view = view;



        view.addRecognizeSongListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String songPath = view.getSelectedSongPath();
                if (songPath != null) {
                    // Show the loading screen
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            view.showLoadingScreen();
                        }
                    });

                    // Start a new Thread for the long-running task
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String rawFilePath = convertToRaw(songPath);
                                String songInfoJson = shazam.recognizeSong(rawFilePath);

                                // Parse the JSON response
                                JsonParser parser = new JsonParser();
                                JsonObject json = (JsonObject) parser.parse(songInfoJson);

                                // Extract song title and artist from the JSON response
                                JsonObject track = json.getAsJsonObject("track");
                                String songTitle = track.get("title").getAsString();
                                String songArtist = track.get("subtitle").getAsString();
                                String imageURL = track.get("images").getAsJsonObject().get("coverart").getAsString();
                                System.out.println(imageURL);
                                String youtubeURL = "";
                                try {
                                    youtubeURL = track.get("url").getAsString();
                                } catch (Exception ex) {
                                    System.out.println("No Youtube URL found");
                                }


                                // Download the image
                                BufferedImage songImage = null;
                                try {
                                    songImage = ImageIO.read(new URL(imageURL));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    songImage = ImageIO.read(new File("default_image_path")); // replace default_image_path with your default image path
                                }


                                // Create final copies of the variables
                                final BufferedImage finalSongImage = songImage;
                                final String finalYoutubeURL = youtubeURL;

                                // Display the song information
                                RecognizedSongView recognizedSongView = new RecognizedSongView(view.getFrame());
                                RecognizedSongController recognizedSongController = new RecognizedSongController(model, recognizedSongView, songTitle, songArtist, finalSongImage, finalYoutubeURL);

                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.hideLoadingScreen(); // Hide the loading screen
                                        recognizedSongView.show(songTitle, songArtist, finalSongImage, finalYoutubeURL);  // Show the new window
                                    }
                                });
                            } catch (IOException | InterruptedException | EncoderException ex) {
                                ex.printStackTrace();
                            } catch (RuntimeException ex) {
                                if (ex.getMessage().contains("Status code: 413")) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            JOptionPane.showMessageDialog(view.getFrame(), "The MP3 file is too long. Try shortening it.");
                                        }
                                    });
                                } else {
                                    ex.printStackTrace();
                                }
                            } finally {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.hideLoadingScreen(); // Always hide the loading screen
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    // Handle the case where no file was selected
                }
            }
        });


    }

    public void runApplication() {
        view.show();
    }

    private String convertToRaw(String audioFilePath) throws IOException, InputFormatException, EncoderException {
        String rawFilePath = audioFilePath.replace(".mp3", ".raw");

        // Create a temporary WAV file path
        String tempWavFilePath = audioFilePath.replace(".mp3", ".wav");

        // Convert the MP3 to WAV using JAVE library
        convertToWav(audioFilePath, tempWavFilePath);

        // Convert the WAV to raw using TarsosDSP library
        convertWavToRaw(tempWavFilePath, rawFilePath);

        // Delete the temporary WAV file
        Files.deleteIfExists(Path.of(tempWavFilePath));

        return rawFilePath;
    }

    private void convertToWav(String audioFilePath, String wavFilePath) throws IOException, InputFormatException, EncoderException {
        Encoder encoder = new Encoder();
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setCodec("pcm_s16le");
        audioAttributes.setBitRate(16);
        audioAttributes.setChannels(1);
        audioAttributes.setSamplingRate(44100);

        EncodingAttributes encodingAttributes = new EncodingAttributes();
        encodingAttributes.setFormat("wav");
        encodingAttributes.setAudioAttributes(audioAttributes);

        File source = new File(audioFilePath);
        File target = new File(wavFilePath);
        encoder.encode(source, target, encodingAttributes);
    }

    private void convertWavToRaw(String wavFilePath, String rawFilePath) throws IOException {
        File sourceFile = new File(wavFilePath);
        File targetFile = new File(rawFilePath);
        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}