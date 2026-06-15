package de.hitec.nhplus.model;

public class EventLog {

    private long id;
    private String username;
    private String action;
    private String timestamp;

    public EventLog(String username,
                    String action,
                    String timestamp) {

        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
    }

    public EventLog(long id,
                    String username,
                    String action,
                    String timestamp) {

        this(username, action, timestamp);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }

    public String getTimestamp() {
        return timestamp;
    }
}