package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caregiver;

import java.sql.*;
import java.util.ArrayList;

/**
 * Provides database access for Caregiver objects.
 */
public class CaregiverDao extends DaoImp<Caregiver> {

    public CaregiverDao(Connection connection) {
        super(connection);
    }

    @Override
    protected PreparedStatement getCreateStatement(Caregiver caregiver) {
        PreparedStatement statement = null;
        try {
            final String SQL =
                    "INSERT INTO caregiver " +
                            "(firstname, surname, phonenumber, role) " +
                            "VALUES (?, ?, ?, ?)";

            statement = this.connection.prepareStatement(SQL);

            statement.setString(1, caregiver.getFirstName());
            statement.setString(2, caregiver.getSurname());
            statement.setString(3, caregiver.getPhoneNumber());
            statement.setString(4, caregiver.getRole()); // Rolle einfügen

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return statement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long cid) {
        PreparedStatement statement = null;
        try {
            final String SQL =
                    "SELECT * FROM caregiver WHERE cid = ?";

            statement = this.connection.prepareStatement(SQL);
            statement.setLong(1, cid);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return statement;
    }

    @Override
    protected Caregiver getInstanceFromResultSet(ResultSet result) throws SQLException {
        return new Caregiver(
                result.getLong("cid"),
                result.getString("firstname"),
                result.getString("surname"),
                result.getString("phonenumber"),
                result.getString("role") // Rolle auslesen
        );
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement statement = null;

        try {
            final String SQL = "SELECT * FROM caregiver";
            statement = this.connection.prepareStatement(SQL);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return statement;
    }

    @Override
    protected ArrayList<Caregiver> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<Caregiver> list = new ArrayList<>();

        while (result.next()) {
            Caregiver caregiver = new Caregiver(
                    result.getLong("cid"),
                    result.getString("firstname"),
                    result.getString("surname"),
                    result.getString("phonenumber"),
                    result.getString("role") // Rolle in Schleife auslesen
            );
            list.add(caregiver);
        }

        return list;
    }

    @Override
    protected PreparedStatement getUpdateStatement(Caregiver caregiver) {
        PreparedStatement statement = null;

        try {
            final String SQL =
                    "UPDATE caregiver SET " +
                            "firstname = ?, " +
                            "surname = ?, " +
                            "phonenumber = ?, " +
                            "role = ? " +
                            "WHERE cid = ?";

            statement = this.connection.prepareStatement(SQL);

            statement.setString(1, caregiver.getFirstName());
            statement.setString(2, caregiver.getSurname());
            statement.setString(3, caregiver.getPhoneNumber());
            statement.setString(4, caregiver.getRole()); // Rolle aktualisieren
            statement.setLong(5, caregiver.getCid());

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return statement;
    }

    @Override
    protected PreparedStatement getDeleteStatement(long cid) {
        PreparedStatement statement = null;

        try {
            final String SQL =
                    "DELETE FROM caregiver WHERE cid = ?";

            statement = this.connection.prepareStatement(SQL);
            statement.setLong(1, cid);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return statement;
    }
}