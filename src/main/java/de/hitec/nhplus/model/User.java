package de.hitec.nhplus.model;

/**
 * Klasse für den Benutzer des Systems.
 * Speichert Benutzername, Passwort Hash und Salt (kein Klartext-Passwort).
 * Enthält zusätzlich Felder für einen verschlüsselten Schlüssel
 * (wrappedEncryptionKey), die für eine separate Verschlüsselungs-Funktion
 * von Patientendaten gebraucht werden und nicht direkt zum Login gehören.
 */
public class User {

    private long uid;

    private String username;
    private String passwordHash;
    private String salt;
    private boolean superUser; // true = admin, false = andere person
    private Role role;
    private String wrappedEncryptionKey;
    private String wrappedEncryptionKeySalt;
    private String wrappedEncryptionKeyIv;
    private int wrappedEncryptionKeyIterations;

    /**
     * Erstellt einen neuen Benutzer ohne Verschlüsselungs-Schlüssel.
     * Wird beim normalen Anlegen eines Benutzers für das Login benutzt.
     *
     * @param username der Benutzername zum Einloggen
     * @param passwordHash das gehashte Passwort
     * @param salt der Salt der beim Hashen benutzt wurde
     * @param superUser true, wenn der Benutzer ein Admin ist
     */
    public User(String username, String passwordHash, String salt, boolean superUser) {
        this(username, passwordHash, salt, superUser, null, null, null, 0);
    }
    /**
     * Erstellt einen neuen Benutzer inklusive der Felder fpr den
     * verschlüsselten Schlüssel. Diese Felder gehören zu einer
     * separaten Verschlüsselungs-Funktion (nicht Teil des Login-Workstreams).
     *
     * @param username der Benutzername zum Einloggen
     * @param passwordHash das gehashte Passwort
     * @param salt der Salt der beim Hashen benutzt wurde
     * @param superUser true, wenn der Benutzer ein Admin ist
     * @param wrappedEncryptionKey der verschlüsselt gespeicherte Schlüssel
     * @param wrappedEncryptionKeySalt der Salt für den Verschlüsselungs Schlüssel
     * @param wrappedEncryptionKeyIv der Initialisierungsvektor für die Verschlüsselung
     * @param wrappedEncryptionKeyIterations Anzahl der Iterationen bei der Schlüsselableitung
     */
    public User(String username, String passwordHash, String salt, boolean superUser,
                String wrappedEncryptionKey, String wrappedEncryptionKeySalt,
                String wrappedEncryptionKeyIv, int wrappedEncryptionKeyIterations) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.superUser = superUser;
        this.role = Role.REGISTERED_NURSE;
        this.wrappedEncryptionKey = wrappedEncryptionKey;
        this.wrappedEncryptionKeySalt = wrappedEncryptionKeySalt;
        this.wrappedEncryptionKeyIv = wrappedEncryptionKeyIv;
        this.wrappedEncryptionKeyIterations = wrappedEncryptionKeyIterations;
    }

    /**
     * Erstellt einen Benutzer mit bekannter uid, ohne Verschlüsselungs Schlüssel.
     * Wird benutzt, wenn der User aus der Datenbank geladen wird.
     *
     * @param uid die ID des Benutzers aus der Datenbank
     * @param username der Benutzername zum Einloggen
     * @param passwordHash das gehashte Passwort
     * @param salt der Salt der beim Hashen benutzt wurde
     * @param superUser true, wenn der Benutzer ein Admin ist
     */

    public User(long uid, String username, String passwordHash, String salt, boolean superUser) {
        this(uid, username, passwordHash, salt, superUser, null, null, null, 0);
    }

    /**
     * Erstellt einen Benutzer mit bekannter uid inklusive der Felder
     * für den verschlüsselten Schlüssel.
     *
     * @param uid die ID des Benutzers aus der Datenbank
     * @param username der Benutzername zum Einloggen
     * @param passwordHash das gehashte Passwort
     * @param salt der Salt der beim Hashen benutzt wurde
     * @param superUser true, wenn der Benutzer ein Admin ist
     * @param wrappedEncryptionKey der verschlüsselt gespeicherte Schlüssel
     * @param wrappedEncryptionKeySalt der Salt für den Verschluesselungs Schlüssel
     * @param wrappedEncryptionKeyIv der Initialisierungsvektor für die Verschlüsselung
     * @param wrappedEncryptionKeyIterations Anzahl der Iterationen bei der Schlüsselableitung
     */

    public User(long uid, String username, String passwordHash, String salt, boolean superUser,
                String wrappedEncryptionKey, String wrappedEncryptionKeySalt,
                String wrappedEncryptionKeyIv, int wrappedEncryptionKeyIterations) {
        this(username, passwordHash, salt, superUser, wrappedEncryptionKey, wrappedEncryptionKeySalt,
                wrappedEncryptionKeyIv, wrappedEncryptionKeyIterations);
        this.uid = uid;
    }
    /**
     * @return die uid aus der Datenbank
     */
    public long getUid() {
        return uid;
    }
    /**
     * @return der Benutzername
     */
    public String getUsername(){
        return username;
    }
    /**
     * @return das gehashte Passwort (kein Klartext)
     */
    public String getPasswordHash(){
        return passwordHash;
    }
    /**
     * @return der Salt der beim Hashen benutzt wurde
     */
    public String getSalt(){
        return salt;
    }
    /**
     * @return true, wenn der Benutzer Admin Rechte hat
     */
    public boolean isSuperUser(){ return superUser; }

    /**
     *
     * @return die Rolle des Benutzers
     */
    public Role getRole() {
        return role;
    }

    /**
     * @return der verschlüsselt gespeicherte Schlüssel (Verschlüsselungs-Feature)
     */
    public String getWrappedEncryptionKey() { return wrappedEncryptionKey; }
    /**
     * @return der Salt fuer den Verschlüsselungs-Schlüssel
     */
    public String getWrappedEncryptionKeySalt() { return wrappedEncryptionKeySalt; }
    /**
     * @return der Initialisierungsvektor für die Verschlüsselung
     */
    public String getWrappedEncryptionKeyIv() { return wrappedEncryptionKeyIv; }
    /**
     * @return Anzahl der Iterationen bei der Schlüsselableitung
     */
    public int getWrappedEncryptionKeyIterations() { return wrappedEncryptionKeyIterations; }
    /**
     * Setzt einen neuen Passwort-Hash. Wird beim Passwort-Reset gebraucht.
     *
     * @param passwordHash der neue Passwort-Hash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    /**
     * Setzt einen neuen Salt. Wird beim Passwort-Reset gebraucht.
     *
     * @param salt der neue Salt
     */
    public void setSalt(String salt)
    {
        this.salt = salt;
    }
    /**
     * Setzt die Rolle des Benutzers.
     *
     * @param role die neue Rolle
     */
    public void setRole(Role role)
    {
        this.role = role;
    }

    /**
     * Setzt den verschlüsselt gespeicherten Schlüssel.
     *
     * @param wrappedEncryptionKey der neue verschlüsselte Schlüssel
     */
    public void setWrappedEncryptionKey(String wrappedEncryptionKey) {
        this.wrappedEncryptionKey = wrappedEncryptionKey;
    }
    /**
     * Setzt den Salt für den Verschlüsselungs-Schlüssel.
     *
     * @param wrappedEncryptionKeySalt der neue Salt
     */
    public void setWrappedEncryptionKeySalt(String wrappedEncryptionKeySalt) {
        this.wrappedEncryptionKeySalt = wrappedEncryptionKeySalt;
    }
    /**
     * Setzt den Initialisierungsvektor für die Verschlüsselung.
     *
     * @param wrappedEncryptionKeyIv der neue Initialisierungsvektor
     */
    public void setWrappedEncryptionKeyIv(String wrappedEncryptionKeyIv) {
        this.wrappedEncryptionKeyIv = wrappedEncryptionKeyIv;
    }
    /**
     * Setzt die Anzahl der Iterationen bei der Schlüsselableitung.
     *
     * @param wrappedEncryptionKeyIterations die neue Anzahl an Iterationen
     */
    public void setWrappedEncryptionKeyIterations(int wrappedEncryptionKeyIterations) {
        this.wrappedEncryptionKeyIterations = wrappedEncryptionKeyIterations;
    }
}