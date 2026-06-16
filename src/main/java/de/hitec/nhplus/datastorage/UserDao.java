package de.hitec.nhplus.datastorage;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.model.Role;

import java.sql.*;

import java.util.ArrayList;

/**
 * Kümmert sich um alle Datenbankoperationen für die user-Tabelle.
 * Folgt demselben Muster wie PatientDao und TreatmentDao.
 *
 * Speichert neben Benutzername, Passwort-Hash und Salt auch Felder
 * für eine separate Verschlüsselungs-Funktion (wrapped_encryption_key)
 * und eine Rolle (role).
 */
public class UserDao extends DaoImp<User> {
    /**
     * Erstellt ein neues UserDao mit einer Datenbankverbindung.
     *
     * @param connection die Verbindung zur Datenbank
     */
    public UserDao(Connection connection) {
        super(connection);
    }

    /**
     * Legt die user-Tabelle an, falls sie noch nicht existiert.
     * Prüft zusätzlich, ob bereits vorhandene user-Tabellen (z.B. aus
     * einem alten Datenbankstand) die neueren Spalten für Rolle und
     * Verschlüsselung schon besitzen, und fügt sie notfalls nachträglich
     * per ALTER TABLE hinzu (siehe ensureColumnExists).
     */
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
    /**
     * Sucht einen Benutzer anhand seines Benutzernamens.
     * Wird beim Login gebraucht um Hash und Salt aus der Datenbank zu laden.
     *
     * @param username der gesuchte Benutzername
     * @return das gefundene User-Objekt oder null, wenn kein User existiert
     * @throws SQLException wenn ein Datenbankfehler auftritt
     */

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

    /**
     * Aktualisiert Passwort-Hash, Salt und die Verschlüsselungs-Felder
     * eines Benutzers in der Datenbank. Wird beim Passwort-Reset durch
     * einen Admin benutzt.
     *
     * @param user der Benutzer mit den neuen Werten
     * @throws SQLException wenn ein Datenbankfehler auftritt
     */
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
    /**
     * Baut aus einer Datenbankzeile ein vollständiges User-Objekt zusammen,
     * inklusive Rolle und Verschlüsselungs-Feldern.
     *
     * @param rs das ResultSet mit den Daten aus der Datenbank
     * @return das fertige User-Objekt
     * @throws SQLException wenn ein Datenbankfehler auftritt
     */
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

    /**
     * Baut aus mehreren Datenbankzeilen eine Liste von User-Objekten.
     *
     * @param rs das ResultSet mit mehreren Zeilen
     * @return die Liste aller gefundenen User
     * @throws SQLException wenn ein Datenbankfehler auftritt
     */
    @Override
    protected ArrayList<User> getListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        while (rs.next()) {
            list.add(getInstanceFromResultSet(rs));
        }
        return list;
    }

    /**
     * Erstellt das SQL-Statement um einen neuen Benutzer mit allen Feldern
     * (inklusive Rolle und Verschlüüsselungs-Daten) in die Datenbank einzufuegen.
     *
     * @param user der neue Benutzer der gespeichert werden soll
     * @return das fertige PreparedStatement
     */

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

    /**
     * Erstellt das SQL-Statement um einen Benutzer anhand seiner uid zu suchen.
     *
     * @param key die uid des gesuchten Benutzers
     * @return das fertige PreparedStatement
     */

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

    /**
     * Erstellt das SQL-Statement um alle Benutzer aus der Datenbank zu lesen.
     *
     * @return das fertige PreparedStatement
     */
    @Override
    protected PreparedStatement getReadAllStatement() {
        try {
            return connection.prepareStatement("SELECT * FROM user");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Erstellt das SQL-Statement, um einen Benutzer mit allen Feldern
     * in der Datenbank zu aktualisieren.
     *
     * @param user der Benutzer mit den neuen Daten
     * @return das fertige PreparedStatement
     */
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
    /**
     * Erstellt das SQL-Statement um einen Benutzer aus der Datenbank zu loeschen.
     *
     * @param key die uid des zu löschenden Benutzers
     * @return das fertige PreparedStatement
     */
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

    /**
     * Stellt sicher, dass eine bestimmte Spalte in der user-Tabelle existiert.
     * Wird gebraucht, falls die Tabelle aus einem älteren Datenbankstand
     * stammt, der diese Spalte noch nicht hatte. Fügt die Spalte notfalls
     * per ALTER TABLE nachträglich hinzu.
     *
     * @param columnName der Name der Spalte
     * @param definition der SQL-Typ der Spalte (z.B. "TEXT" oder "INTEGER")
     * @throws SQLException wenn ein Datenbankfehler auftritt
     */
    private void ensureColumnExists(String columnName, String definition) throws SQLException {
        if (hasColumn(columnName)) {
            return;
        }

        final String SQL = "ALTER TABLE user ADD COLUMN " + columnName + " " + definition;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(SQL);
        }
    }
    /**
     * Prüft, ob die user-Tabelle bereits eine Spalte mit dem gegebenen
     * Namen besitzt. Liest dafür die Tabellenstruktur per PRAGMA table_info aus.
     *
     * @param columnName der zu prüfende Spaltenname
     * @return true wenn die Spalte existiert, sonst false
     * @throws SQLException wenn ein Datenbankfehler auftritt
     */
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
