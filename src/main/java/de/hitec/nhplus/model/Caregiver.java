package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;


public class Caregiver extends Person {

    private SimpleLongProperty cid;
    private final SimpleStringProperty phoneNumber;
    private final SimpleStringProperty qualification;

    public Caregiver(String firstName, String surname,
                     String phoneNumber, String qualification) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(0);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.qualification = new SimpleStringProperty(qualification);
    }

    public Caregiver(long cid, String firstName, String surname,
                     String phoneNumber, String qualification) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(cid);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.qualification = new SimpleStringProperty(qualification);
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

    public SimpleStringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public String getQualification() {
        return qualification.get();
    }

    public SimpleStringProperty qualificationProperty() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification.set(qualification);
    }
