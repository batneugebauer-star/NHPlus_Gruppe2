package de.hitec.nhplus.datastorage;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.model.Role;

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
                        "   super_user    INTEGER NOT NULL DEFAULT 0, " +
                        "   role          TEXT," +
                        "   wrapped_encryption_key TEXT, " +
                        "   wrapped_encryption_key_salt TEXT, " +
                        "   wrapped_encryption_key_iv TEXT, " +
                        "   wrapped_encryption_key_iterations INTEGER" +
                        ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(SQL);
            ensureColumnExists("wrapped_encryption_key", "TEXT");
            ensureColumnExists("wrapped_encryption_key_salt", "TEXT");
            ensureColumnExists("wrapped_encryption_key_iv", "TEXT");
            ensureColumnExists("wrapped_encryption_key_iterations", "INTEGER");
            ensureColumnExists("role", "TEXT");
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
        final String SQL =
                "UPDATE user SET password_hash = ?, salt = ?, wrapped_encryption_key = ?, " +
                        "wrapped_encryption_key_salt = ?, wrapped_encryption_key_iv = ?, " +
                        "wrapped_encryption_key_iterations = ? WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setString(1, user.getPasswordHash());
            stmt.setString(2, user.getSalt());
            stmt.setString(3, user.getWrappedEncryptionKey());
            stmt.setString(4, user.getWrappedEncryptionKeySalt());
            stmt.setString(5, user.getWrappedEncryptionKeyIv());
            stmt.setInt(6, user.getWrappedEncryptionKeyIterations());
            stmt.setLong(7, user.getUid());
            stmt.executeUpdate();
        }
    }

    @Override
    protected User getInstanceFromResultSet(ResultSet rs) throws SQLException {

        User user = new User(
                rs.getLong("uid"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("salt"),
                rs.getInt("super_user") == 1,
                rs.getString("wrapped_encryption_key"),
                rs.getString("wrapped_encryption_key_salt"),
                rs.getString("wrapped_encryption_key_iv"),
                rs.getInt("wrapped_encryption_key_iterations")
        );

        String roleName = rs.getString("role");

        if (roleName != null) {
            user.setRole(Role.valueOf(roleName));
        }

        return user;
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
                "INSERT INTO user (username, password_hash, salt, super_user, role, wrapped_encryption_key, " +
                        "wrapped_encryption_key_salt, wrapped_encryption_key_iv, wrapped_encryption_key_iterations) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getSalt());
            stmt.setInt(4, user.isSuperUser() ? 1 : 0);
            stmt.setString(5, user.getRole() == null ? null : user.getRole().name());
            stmt.setString(6, user.getWrappedEncryptionKey());
            stmt.setString(7, user.getWrappedEncryptionKeySalt());
            stmt.setString(8, user.getWrappedEncryptionKeyIv());
            stmt.setInt(9, user.getWrappedEncryptionKeyIterations());
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
                "UPDATE user SET username=?, password_hash=?, salt=?, super_user=?,role=?, wrapped_encryption_key=?, " +
                        "wrapped_encryption_key_salt=?, wrapped_encryption_key_iv=?, wrapped_encryption_key_iterations=? WHERE uid=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getSalt());
            stmt.setInt(4, user.isSuperUser() ? 1 : 0);
            stmt.setString(5, user.getRole() == null ? null: user.getRole().name());
            stmt.setString(6, user.getWrappedEncryptionKey());
            stmt.setString(7, user.getWrappedEncryptionKeySalt());
            stmt.setString(8, user.getWrappedEncryptionKeyIv());
            stmt.setInt(9, user.getWrappedEncryptionKeyIterations());
            stmt.setLong(10, user.getUid());
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

    private void ensureColumnExists(String columnName, String definition) throws SQLException {
        if (hasColumn(columnName)) {
            return;
        }

        final String SQL = "ALTER TABLE user ADD COLUMN " + columnName + " " + definition;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(SQL);
        }
    }

    private boolean hasColumn(String columnName) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(user)")) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
