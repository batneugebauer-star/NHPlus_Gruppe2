package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.EventLog;

import java.sql.*;
import java.util.ArrayList;

public class EventLogDao extends DaoImp<EventLog> {

    public EventLogDao(Connection connection) {
        super(connection);
    }

    @Override
    protected PreparedStatement getCreateStatement(EventLog log) {

        try {

            PreparedStatement statement =
                    connection.prepareStatement(
                            "INSERT INTO eventlog(username, action, timestamp) VALUES (?, ?, ?)");

            statement.setString(1, log.getUsername());
            statement.setString(2, log.getAction());
            statement.setString(3, log.getTimestamp());

            return statement;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected EventLog getInstanceFromResultSet(ResultSet rs)
            throws SQLException {

        return new EventLog(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("action"),
                rs.getString("timestamp")
        );
    }

    @Override
    protected ArrayList<EventLog> getListFromResultSet(ResultSet rs)
            throws SQLException {

        ArrayList<EventLog> logs = new ArrayList<>();

        while (rs.next()) {
            logs.add(getInstanceFromResultSet(rs));
        }

        return logs;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PreparedStatement getReadAllStatement() {

        try {
            return connection.prepareStatement(
                    "SELECT * FROM eventlog");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected PreparedStatement getUpdateStatement(EventLog eventLog) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        throw new UnsupportedOperationException();
    }
}