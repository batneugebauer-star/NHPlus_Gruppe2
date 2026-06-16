package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Caregiver extends Person {

    private SimpleLongProperty cid;
    private final SimpleStringProperty phoneNumber;
    private final SimpleStringProperty qualification;
    private final SimpleStringProperty role;

    // Für neue Einträge
    public Caregiver(long cid, String firstName, String surname, String phoneNumber, SimpleStringProperty role) {
        this(cid, firstName, surname, phoneNumber, "", role);
    }

    public Caregiver(String firstName, String surname,
                     String phoneNumber, String qualification, SimpleStringProperty role) {
        super(firstName, surname);
        this.role = role;
        this.cid = new SimpleLongProperty(0);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.qualification = new SimpleStringProperty(qualification);
    }

    public Caregiver(long cid, String firstName, String surname,
                     String phoneNumber, String qualification, SimpleStringProperty role) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(cid);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.qualification = new SimpleStringProperty(qualification);
        this.role = role;
    }

    public long getCid() {
        return cid.get();
    }

    public SimpleLongProperty cidProperty() {
        return cid;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public SimpleStringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    // Kompatibilität mit AllCaregiverController
    public String getTelephoneNumber() {
        return getPhoneNumber();
    }

    public void setTelephoneNumber(String telephoneNumber) {
        setPhoneNumber(telephoneNumber);
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public SimpleStringProperty roleProperty() {
        return role;
    }
    }