package org.example.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class DatabaseModel {
    private Connection connection;

    public DatabaseModel() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/musicdb?useSSL=false", "root", "123!@#QAZWSX");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlaylistId(String playlistName) {
        String sql = "SELECT id FROM playlists WHERE name = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, playlistName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                rs.close();
                stmt.close();
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int generateSongId() {
        String sql = "SELECT MAX(id) as max_id FROM songs";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                rs.close();
                stmt.close();
                return maxId + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1; // Checking if song table is empty. Returns 1 if the songs table is empty
    }
    public int saveSong(String songName, String youtubeURL) {
        // First, check if the song already exists in the songs table
        String selectSongSql = "SELECT id FROM songs WHERE song_name = ? AND YtUrl = ?";
        try {
            PreparedStatement selectStmt = connection.prepareStatement(selectSongSql);
            selectStmt.setString(1, songName);
            selectStmt.setString(2, youtubeURL);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                //  If song exists just return ID don't make new record
                int songId = rs.getInt("id");
                rs.close();
                selectStmt.close();
                return songId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If song does'nt exists insert new one
        int songId = generateSongId();
        String insertSongSql = "INSERT INTO songs (id, song_name, YtUrl) VALUES (?, ?, ?)";
        try {
            PreparedStatement insertStmt = connection.prepareStatement(insertSongSql);
            insertStmt.setInt(1, songId);
            insertStmt.setString(2, songName);
            insertStmt.setString(3, youtubeURL);
            insertStmt.executeUpdate();
            insertStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return songId;
    }

    public void savePlaylistSong(int playlistId, int songId) {
        String insertPlaylistSongSql = "INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(insertPlaylistSongSql);
            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllPlaylists() {
        List<String> playlists = new ArrayList<>();
        String sql = "SELECT name FROM playlists";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String playlistName = rs.getString("name");
                playlists.add(playlistName);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlists;
    }
    public void savePlaylist(PlaylistModel playlist) {
        String insertPlaylistSql = "INSERT INTO playlists (name) VALUES (?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(insertPlaylistSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, playlist.getName());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int playlistId = generatedKeys.getInt(1);
                savePlaylistSongs(playlistId, playlist.getSongIds());
            }

            generatedKeys.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlaylist(String playlistName) {
        try {
            // Get ID of playlist
            String getPlaylistIdQuery = "SELECT id FROM playlists WHERE name = ?";
            PreparedStatement getPlaylistIdStmt = connection.prepareStatement(getPlaylistIdQuery);
            getPlaylistIdStmt.setString(1, playlistName);
            ResultSet rs = getPlaylistIdStmt.executeQuery();

            if (rs.next()) {
                int playlistId = rs.getInt("id");

                // First we are deleting all songs connected to playlist
                String deleteSongsQuery = "DELETE FROM playlist_songs WHERE playlist_id = ?";
                PreparedStatement deleteSongsStmt = connection.prepareStatement(deleteSongsQuery);
                deleteSongsStmt.setInt(1, playlistId);
                deleteSongsStmt.executeUpdate();

                // And when songs are deleted , delete also playlist
                String deletePlaylistQuery = "DELETE FROM playlists WHERE id = ?";
                PreparedStatement deletePlaylistStmt = connection.prepareStatement(deletePlaylistQuery);
                deletePlaylistStmt.setInt(1, playlistId);
                deletePlaylistStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SongModel> getPlaylistSongs(int playlistId) {
        List<SongModel> songs = new ArrayList<>();
        try {
            String sql = "SELECT s.id, s.song_name, s.YtUrl FROM songs s INNER JOIN playlist_songs ps ON s.id = ps.song_id WHERE ps.playlist_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, playlistId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");  // get the song ID from the result set
                String songName = rs.getString("song_name");
                String ytUrl = rs.getString("YtUrl");
                SongModel song = new SongModel(songName, ytUrl);
                song.setId(id);  // set the song ID to the song model object
                songs.add(song);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }
    private void savePlaylistSongs(int playlistId, List<Integer> songIds) {
        String insertPlaylistSongSql = "INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)";

        try {
            PreparedStatement insertPlaylistSongStmt = connection.prepareStatement(insertPlaylistSongSql);

            for (int songId : songIds) {
                insertPlaylistSongStmt.setInt(1, playlistId);
                insertPlaylistSongStmt.setInt(2, songId);
                insertPlaylistSongStmt.executeUpdate();
            }

            insertPlaylistSongStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSongFromPlaylist(int playlistId, int songId) {
        try {
            //Part of code that is deleting song from specific playlist from playlist_songs table
            String deleteSongQuery = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
            PreparedStatement deleteSongStmt = connection.prepareStatement(deleteSongQuery);
            deleteSongStmt.setInt(1, playlistId);
            deleteSongStmt.setInt(2, songId);
            deleteSongStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}