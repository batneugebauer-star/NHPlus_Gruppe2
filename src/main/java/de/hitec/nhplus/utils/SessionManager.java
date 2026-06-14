package de.hitec.nhplus.utils;
import de.hitec.nhplus.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javax.crypto.SecretKey;
/**
 * Merkt sich wer gerade eingeloggt ist.
 * Startet automatisch einen 15-Minuten-Timer für den Auto-Logout.
 */
public class SessionManager {

    private static final int TIMEOUT_SECONDS = 15 * 60; // 900 Sekunden = 15min

    private static SessionManager instance; // das einzige Exemplar
    private User loggedInUser;
    private SecretKey dataEncryptionKey;
    private Timeline timeoutTimer;
    private Runnable onTimeout;

    private SessionManager() {} // private = kein new SessionManager() von außen

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Login: User merken + Timer starten
    public void login(User user, SecretKey dataEncryptionKey, Runnable onTimeout) {
        this.loggedInUser = user;
        this.dataEncryptionKey = dataEncryptionKey;
        this.onTimeout = onTimeout;
        startTimer();
    }

    // Logout: User vergessen + Timer stoppen
    public void logout() {
        loggedInUser = null;
        dataEncryptionKey = null;
        stopTimer();
    }

    public User getLoggedInUser() { return loggedInUser; }
    public SecretKey getDataEncryptionKey() { return dataEncryptionKey; }

    public boolean isLoggedIn() { return loggedInUser != null; }

    // Timer zurücksetzen bei Benutzeraktivität
    public void resetTimer() {
        if (timeoutTimer != null) {
            timeoutTimer.stop();
            timeoutTimer.playFromStart();
        }
    }

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

    private void stopTimer() {
        if (timeoutTimer != null) {
            timeoutTimer.stop();
            timeoutTimer = null;
        }
    }
}
