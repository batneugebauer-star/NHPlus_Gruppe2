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
    private String wrappedEncryptionKey;
    private String wrappedEncryptionKeySalt;
    private String wrappedEncryptionKeyIv;
    private int wrappedEncryptionKeyIterations;

    // Konstruktor für neue Benutzer
    public User(String username, String passwordHash, String salt, boolean superUser) {
        this(username, passwordHash, salt, superUser, null, null, null, 0);
    }

    public User(String username, String passwordHash, String salt, boolean superUser,
                String wrappedEncryptionKey, String wrappedEncryptionKeySalt,
                String wrappedEncryptionKeyIv, int wrappedEncryptionKeyIterations) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.superUser = superUser;
        this.wrappedEncryptionKey = wrappedEncryptionKey;
        this.wrappedEncryptionKeySalt = wrappedEncryptionKeySalt;
        this.wrappedEncryptionKeyIv = wrappedEncryptionKeyIv;
        this.wrappedEncryptionKeyIterations = wrappedEncryptionKeyIterations;
    }

    // Konstruktor wen der User aus der Datenbank geladen wird
    public User(long uid, String username, String passwordHash, String salt, boolean superUser) {
        this(uid, username, passwordHash, salt, superUser, null, null, null, 0);
    }

    public User(long uid, String username, String passwordHash, String salt, boolean superUser,
                String wrappedEncryptionKey, String wrappedEncryptionKeySalt,
                String wrappedEncryptionKeyIv, int wrappedEncryptionKeyIterations) {
        this(username, passwordHash, salt, superUser, wrappedEncryptionKey, wrappedEncryptionKeySalt,
                wrappedEncryptionKeyIv, wrappedEncryptionKeyIterations);
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
    public String getWrappedEncryptionKey() { return wrappedEncryptionKey; }
    public String getWrappedEncryptionKeySalt() { return wrappedEncryptionKeySalt; }
    public String getWrappedEncryptionKeyIv() { return wrappedEncryptionKeyIv; }
    public int getWrappedEncryptionKeyIterations() { return wrappedEncryptionKeyIterations; }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    } // Setter nur für das Passwort Reset gebraucht
    public void setSalt(String salt)
    {
        this.salt = salt;
    }
    public void setWrappedEncryptionKey(String wrappedEncryptionKey) {
        this.wrappedEncryptionKey = wrappedEncryptionKey;
    }
    public void setWrappedEncryptionKeySalt(String wrappedEncryptionKeySalt) {
        this.wrappedEncryptionKeySalt = wrappedEncryptionKeySalt;
    }
    public void setWrappedEncryptionKeyIv(String wrappedEncryptionKeyIv) {
        this.wrappedEncryptionKeyIv = wrappedEncryptionKeyIv;
    }
    public void setWrappedEncryptionKeyIterations(int wrappedEncryptionKeyIterations) {
        this.wrappedEncryptionKeyIterations = wrappedEncryptionKeyIterations;
    }
}
