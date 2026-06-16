package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Patients live in a NURSING home and are treated by nurses.
 */
public class Patient extends Person {
    private SimpleLongProperty pid;
    private final SimpleStringProperty dateOfBirth;
    private final SimpleStringProperty careLevel;
    private final SimpleStringProperty roomNumber;
    private final SimpleStringProperty assets;
    private final List<Treatment> allTreatments = new ArrayList<>();

    /**
     * Constructor to initiate an object of class <code>Patient</code> with the given parameter. Use this constructor
     * to initiate objects, which are not persisted yet, because it will not have a patient id (pid).
     *
     * @param firstName First name of the patient.
     * @param surname Last name of the patient.
     * @param dateOfBirth Date of birth of the patient.
     * @param careLevel Care level of the patient.
     * @param roomNumber Room number of the patient.
     * @param assets Assets of the patient.
     */
    public Patient(String firstName, String surname, LocalDate dateOfBirth, String careLevel, String roomNumber, String assets) {
        super(firstName, surname);
        this.pid = new SimpleLongProperty(0);
        this.dateOfBirth = new SimpleStringProperty(DateConverter.convertLocalDateToString(dateOfBirth));
        this.careLevel = new SimpleStringProperty(careLevel);
        this.roomNumber = new SimpleStringProperty(roomNumber);
        this.assets = new SimpleStringProperty(assets);
    }

    /**
     * Constructor to initiate an object of class <code>Patient</code> with the given parameter. Use this constructor
     * to initiate objects, which are already persisted and have a patient id (pid).
     *
     * @param pid Patient id.
     * @param firstName First name of the patient.
     * @param surname Last name of the patient.
     * @param dateOfBirth Date of birth of the patient.
     * @param careLevel Care level of the patient.
     * @param roomNumber Room number of the patient.
     * @param assets Assets of the patient.
     */
    public Patient(long pid, String firstName, String surname, LocalDate dateOfBirth, String careLevel, String roomNumber, String assets) {
        super(firstName, surname);
        this.pid = new SimpleLongProperty(pid);
        this.dateOfBirth = new SimpleStringProperty(DateConverter.convertLocalDateToString(dateOfBirth));
        this.careLevel = new SimpleStringProperty(careLevel);
        this.roomNumber = new SimpleStringProperty(roomNumber);
        this.assets = new SimpleStringProperty(assets);
    }

    /**
     * Returns the patient id.
     *
     * @return patient id
     */
    public long getPid() {
        return pid.get();
    }

    /**
     * Returns the JavaFX property of the patient id.
     *
     * @return id property
     */
    public SimpleLongProperty pidProperty() {
        return pid;
    }

    /**
     * Returns the date of birth as formatted string.
     *
     * @return date of birth
     */
    public String getDateOfBirth() {
        return dateOfBirth.get();
    }

    /**
     * Returns the JavaFX property of the date of birth.
     *
     * @return date of birth property
     */
    public SimpleStringProperty dateOfBirthProperty() {
        return dateOfBirth;
    }

    /**
     * Stores the given string as new <code>birthOfDate</code>.
     *
     * @param dateOfBirth as string in the following format: YYYY-MM-DD.
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth.set(dateOfBirth);
    }

    /**
     * Returns the care level.
     *
     * @return care level
     */
    public String getCareLevel() {
        return careLevel.get();
    }

    /**
     * Returns the JavaFX property of the care level.
     *
     * @return care level property
     */
    public SimpleStringProperty careLevelProperty() {
        return careLevel;
    }

    /**
     * Sets the care level.
     *
     * @param careLevel new care level
     */
    public void setCareLevel(String careLevel) {
        this.careLevel.set(careLevel);
    }

    /**
     * Returns the room number.
     *
     * @return room number
     */
    public String getRoomNumber() {
        return roomNumber.get();
    }

    /**
     * Returns the JavaFX property of the room number.
     *
     * @return room number property
     */
    public SimpleStringProperty roomNumberProperty() {
        return roomNumber;
    }


    /**
     * Sets the room number.
     *
     * @param roomNumber new room number
     */
    public void setRoomNumber(String roomNumber) {
        this.roomNumber.set(roomNumber);
    }

    /**
     * Returns the assets information.
     *
     * @return assets value
     */
    public String getAssets() {
        return assets.get();
    }

    /**
     * Returns the JavaFX property of the assets information.
     *
     * @return assets property
     */
    public SimpleStringProperty assetsProperty() {
        return assets;
    }

    /**
     * Sets the assets information.
     *
     * @param assets new assets value
     */
    public void setAssets(String assets) {
        this.assets.set(assets);
    }

    /**
     * Adds a treatment to the list of treatments, if the list does not already contain the treatment.
     *
     * @param treatment Treatment to add.
     * @return False, if the treatment was already part of the list, else true.
     */
    public boolean add(Treatment treatment) {
        if (this.allTreatments.contains(treatment)) {
            return false;
        }
        this.allTreatments.add(treatment);
        return true;
    }

    /**
     * Returns a human-readable string representation of this patient.
     *
     * @return patient details as text
     */
    @Override
    public String toString() {
        return "Patient" + "\nMNID: " + this.pid +
                "\nFirstname: " + this.getFirstName() +
                "\nSurname: " + this.getSurname() +
                "\nBirthday: " + this.dateOfBirth +
                "\nCarelevel: " + this.careLevel +
                "\nRoomnumber: " + this.roomNumber +
                "\nAssets: " + this.assets +
                "\n";
    }
}