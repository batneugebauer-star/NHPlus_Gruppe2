package de.hitec.nhplus.model;

/**
 * klasse für einen Benutzer des Systems.
 * Passwort wird nicht als Klartext gespeichert, sonder Hash
 */
public class User {

    private long uid;

    private String username;
    private String passwordHash; // gehashtes PW
    private String salt; // für das hashen
    private boolean superUser; // true = admin, false = normale pflegeperson

    // Konstruktor für neue Benutzer
    public User(String username, String passwordHash, String salt, boolean superUser) {
        this.username = username;
            this.passwordHash = passwordHash;
        this.salt = salt;
        this.superUser = superUser;
    }

    // Konstruktor wen der User aus der Datenbank geladen wird
    public User(long uid, String username, String passwordHash, String salt, boolean superUser) {
        this(username, passwordHash, salt, superUser);
        this.uid = uid;
    }

    public long getUid() {
        return uid;
    }
    public String getUsername(){
        return username;
    }
    public String getPasswordHash(){
        return passwordHash;
    }
    public String getSalt(){
        return salt;
    }
    public boolean isSuperUser(){ return superUser; }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    } // Setter nur für das Passwort Reset gebraucht
    public void setSalt(String salt)
    {
        this.salt = salt;
    }
}
