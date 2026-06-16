package de.hitec.nhplus.utils;
import de.hitec.nhplus.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javax.crypto.SecretKey;
/**
 * Merkt sich wer gerade eingeloggt ist.
 * Startet automatisch einen 15-Minuten-Timer für den Auto-Logout.
 * Hält zusätzlich den Verschlüsselungs-Schlüssel (dataEncryptionKey)
 * für die Dauer der Session im Speicher. Dieser Schlüssel gehört zu
 * einer separaten Verschlüsselungs-Funktion für Patientendaten und
 * wurde von einem Teammitglied ergänzt, nicht zum ursprünglichen
 * Login-System aus Workstream C.
 */
public class SessionManager {

    private static final int TIMEOUT_SECONDS = 15 * 60; // 900 Sekunden = 15min

    private static SessionManager instance; // das einzige Exemplar
    private User loggedInUser;
    private SecretKey dataEncryptionKey;
    private Timeline timeoutTimer;
    private Runnable onTimeout;

    private SessionManager() {} // private = kein new SessionManager() von außen
    /**
     * Gibt das einzige SessionManager-Objekt zurück.
     * Erstellt es beim ersten Aufruf (Singleton-Pattern).
     *
     * @return die einzige Instanz des SessionManagers
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Meldet einen Benutzer an, speichert seinen Verschlüsselungs-Schluessel
     * für die Dauer der Session und startet den 15-Minuten-Timer.
     *
     * @param user der Benutzer der sich eingeloggt hat
     * @param dataEncryptionKey der entschlüsselte Schlüssel für Patientendaten
     * @param onTimeout wird ausgeführt, wenn der Timer abgelaufen ist (Auto-Logout)
     */
    public void login(User user, SecretKey dataEncryptionKey, Runnable onTimeout) {
        this.loggedInUser = user;
        this.dataEncryptionKey = dataEncryptionKey;
        this.onTimeout = onTimeout;
        startTimer();
    }

    /**
     * Meldet den aktuellen Benutzer ab, löscht den gespeicherten
     * Verschlüsselungs-Schlüssel aus dem Speicher und stoppt den Timer.
     */
    public void logout() {
        loggedInUser = null;
        dataEncryptionKey = null;
        stopTimer();
    }

    /**
     * @return der aktuell eingeloggte Benutzer oder null wenn niemand eingeloggt ist
     */
    public User getLoggedInUser() { return loggedInUser; }
    /**
     * @return der Verschlüsselungs-Schlüssel der aktuellen Session,
     *         oder null wenn niemand eingeloggt ist
     */
    public SecretKey getDataEncryptionKey() { return dataEncryptionKey; }

    /**
     * @return true wenn aktuell ein Benutzer eingeloggt ist, sonst false
     */
    public boolean isLoggedIn() { return loggedInUser != null; }

    /**
     * Setzt den Inaktivitäts-Timer zurück auf 15 Minuten.
     * Muss bei jeder Benutzeraktion aufgerufen werden (Mausklick, Tastatur).
     */
    public void resetTimer() {
        if (timeoutTimer != null) {
            timeoutTimer.stop();
            timeoutTimer.playFromStart();
        }
    }

    /**
     * Startet den Timer neu, der nach Ablauf automatisch ausloggt.
     */

    private void startTimer() {
        stopTimer();
        timeoutTimer = new Timeline(
                new KeyFrame(Duration.seconds(TIMEOUT_SECONDS), event -> {
                    logout();
                    if (onTimeout != null) {
                        onTimeout.run();
                    }
                })
        );
        timeoutTimer.setCycleCount(1);
        timeoutTimer.play();
    }
    /**
     * Stoppt den laufenden Timer.
     */
    private void stopTimer() {
        if (timeoutTimer != null) {
            timeoutTimer.stop();
            timeoutTimer = null;
        }
    }
}
