package com.onursedef.mopperjavafx;

import com.onursedef.mopperjavafx.model.Organizer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrganizerService {

    public List<Organizer> GetAll()
    {
        List<Organizer> organizers = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + getClass().getResource("db/mopper.db").toString());
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
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + getClass().getResource("db/mopper.db").toString());

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
                    LocalDateTime.now() + ": Could not get new organization to db \n {}", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void Add(Organizer organizer) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + getClass().getResource("db/mopper.db").toString());
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
                    LocalDateTime.now() + ": Added new organization: " + organizer.getName());

        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not add new organization to db \n {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void Update(int id, String name, String extension, String path) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + getClass().getResource("db/mopper.db").toString());

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
                    LocalDateTime.now() + ": Updated organization with id: " + id);
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not update organization to db \n {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void Delete(int id) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + getClass().getResource("db/mopper.db").toString());

            StringBuilder query = new StringBuilder("DELETE FROM organizers WHERE id = ");
            query.append(id);
            PreparedStatement statement = connection.prepareStatement(query.toString());

            statement.executeUpdate();

            Logger.getAnonymousLogger().log(
                    Level.INFO,
                    LocalDateTime.now() + ": Deleted organization with id: " + id);
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not delete organization to db \n {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
