package com.onursedef.mopperjavafx;

import com.onursedef.mopperjavafx.model.Organizer;
import javafx.scene.shape.Path;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrganizerService {
    private static final String BASE_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
    private static final String PATH = BASE_PATH + "\\Mopper\\Data\\mopper.db";

    public OrganizerService() {
        File file = new File(PATH);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                Logger.getAnonymousLogger().log(
                        Level.SEVERE,
                        LocalDateTime.now() +  ": Could not create database file \n {}", e.getMessage());
            }
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH);
            // Create organizer db
            if (!connection.isClosed()) {
                String sql = "CREATE TABLE IF NOT EXISTS organizers (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "extensions TEXT NOT NULL," +
                        "path TEXT NOT NULL)";
                connection.createStatement().execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Organizer> GetAll()
    {
        List<Organizer> organizers = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH);
            String query = "SELECT * FROM organizers";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                organizers.add(new Organizer(rs.getInt("id"), rs.getString("name"), rs.getString("extensions"), rs.getString("path")));
            }

        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() +  ": Could not load organizers from db \n {}", e.getMessage());
            organizers.clear();
        }

        return organizers;
    }

    public Organizer GetById(int id) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH);

            StringBuilder query = new StringBuilder("SELECT * FROM organizers WHERE id =?");

            PreparedStatement statement = connection.prepareStatement(query.toString());

            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new Organizer(rs.getInt("id"), rs.getString("name"), rs.getString("extensions"), rs.getString("path"));
            }
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not get new organizer to db \n {}", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void Add(Organizer organizer) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH);
            StringBuilder query = new StringBuilder("INSERT INTO organizers");
            query.append("(name, extensions, path) VALUES ('");
            query.append(organizer.getName())
                    .append("', '")
                    .append(organizer.getExtensions())
                    .append("', '")
                    .append(organizer.getPath())
                    .append("')");
            System.out.println("Insert: " + query);
            PreparedStatement statement = connection.prepareStatement(query.toString());

            statement.executeUpdate();

            Logger.getAnonymousLogger().log(
                    Level.INFO,
                    LocalDateTime.now() + ": Added new organizer: " + organizer.getName());

        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not add new organizer to db \n {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void Update(int id, String name, String extension, String path) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH);

            StringBuilder query = new StringBuilder("UPDATE organizers SET ");
            query.append("name = '");
            query.append(name);
            query.append("', extensions = '");
            query.append(extension);
            query.append("', path = '");
            query.append(path);
            query.append("' WHERE id = ");
            query.append(id);

            System.out.println(query.toString());

            PreparedStatement statement = connection.prepareStatement(query.toString());

            statement.executeUpdate();

            Logger.getAnonymousLogger().log(
                    Level.INFO,
                    LocalDateTime.now() + ": Updated organizer with id: " + id);
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not update organizer to db \n {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void Delete(int id) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH);

            StringBuilder query = new StringBuilder("DELETE FROM organizers WHERE id = ");
            query.append(id);
            PreparedStatement statement = connection.prepareStatement(query.toString());

            statement.executeUpdate();

            Logger.getAnonymousLogger().log(
                    Level.INFO,
                    LocalDateTime.now() + ": Deleted organizer with id: " + id);
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not delete organizer to db \n {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
