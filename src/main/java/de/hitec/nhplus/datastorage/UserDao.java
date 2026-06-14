package de.hitec.nhplus.datastorage;
import de.hitec.nhplus.model.User;

import java.sql.*;

import java.util.ArrayList;

/**
 * Kümmert sich um alle Datenbankoperationen für die user-Tabelle.
 * Folgt demselben Muster wie PatientDao und TreatmentDao.
 */
public class UserDao extends DaoImp<User> {
    public UserDao(Connection connection) {
        super(connection);
    }

    // Tabelle anlegen falls sie noch nicht existiert
    public void createTableIfNotExists() {
        final String SQL =
                "CREATE TABLE IF NOT EXISTS user (" +
                        "   uid           INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "   username      TEXT    NOT NULL UNIQUE, " +
                        "   password_hash TEXT    NOT NULL, " +
                        "   salt          TEXT    NOT NULL, " +
                        "   super_user    INTEGER NOT NULL DEFAULT 0" +
                        ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(SQL);
        } catch (SQLException e) {
            System.out.println("UserDao.createTableIfNotExists: " + e.getMessage());
        }
    }

    // Sucht einen User anhand des Benutzernamens und gibt null zurück wenn nicht gefunden
    public User findByUsername(String username) throws SQLException {
        final String SQL = "SELECT * FROM user WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return getInstanceFromResultSet(rs);
            }
        }
        return null;
    }

    // Aktualisiert Hash + Salt eines Benutzers (für Passwort-Reset)
    public void updatePassword(User user) throws SQLException {
        final String SQL = "UPDATE user SET password_hash = ?, salt = ? WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setString(1, user.getPasswordHash());
            stmt.setString(2, user.getSalt());
            stmt.setLong(3, user.getUid());
            stmt.executeUpdate();
        }
    }

    @Override
    protected User getInstanceFromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("uid"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("salt"),
                rs.getInt("super_user") == 1
        );
    }

    @Override
    protected ArrayList<User> getListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        while (rs.next()) {
            list.add(getInstanceFromResultSet(rs));
        }
        return list;
    }

    @Override
    protected PreparedStatement getCreateStatement(User user) {
        final String SQL =
                "INSERT INTO user (username, password_hash, salt, super_user) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getSalt());
            stmt.setInt(4, user.isSuperUser() ? 1 : 0);
            return stmt;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM user WHERE uid = ?");
            stmt.setLong(1, key);
            return stmt;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        try {
            return connection.prepareStatement("SELECT * FROM user");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected PreparedStatement getUpdateStatement(User user) {
        final String SQL =
                "UPDATE user SET username=?, password_hash=?, salt=?, super_user=? WHERE uid=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getSalt());
            stmt.setInt(4, user.isSuperUser() ? 1 : 0);
            stmt.setLong(5, user.getUid());
            return stmt;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM user WHERE uid = ?");
            stmt.setLong(1, key);
            return stmt;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}