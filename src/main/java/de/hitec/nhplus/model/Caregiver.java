package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Caregiver extends Person {

    private final SimpleLongProperty cid;
    private final SimpleStringProperty phoneNumber;
    private final SimpleStringProperty role;

    /**
     * Creates a caregiver with an explicit id and no role value.
     *
     * @param cid caregiver id
     * @param firstName caregiver first name
     * @param surname caregiver surname
     * @param phoneNumber caregiver phone number
     */
    public Caregiver(long cid, String firstName, String surname, String phoneNumber) {
        this(cid, firstName, surname, phoneNumber, "");
    }

    /**
     * Creates a new caregiver without id.
     *
     * @param firstName caregiver first name
     * @param surname caregiver surname
     * @param phoneNumber caregiver phone number
     * @param role caregiver role
     */
    public Caregiver(String firstName, String surname, String phoneNumber, String role) {
        this(0, firstName, surname, phoneNumber, role);
    }

    /**
     * Creates a caregiver with all available values.
     *
     * @param cid caregiver id
     * @param firstName caregiver first name
     * @param surname caregiver surname
     * @param phoneNumber caregiver phone number
     * @param role caregiver role
     */
    public Caregiver(long cid, String firstName, String surname,
                     String phoneNumber, String role) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(cid);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.role = new SimpleStringProperty(role);
    }

    /**
     * Returns the caregiver id.
     *
     * @return caregiver id
     */
    public long getCid() {
        return cid.get();
    }

    /**
     * Returns the JavaFX property for the caregiver id.
     *
     * @return id property
     */
    public SimpleLongProperty cidProperty() {
        return cid;
    }

    /**
     * Returns the caregiver phone number.
     *
     * @return phone number
     */
    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    /**
     * Sets the caregiver phone number.
     *
     * @param phoneNumber new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    /**
     * Returns the JavaFX property for the phone number.
     *
     * @return phone number property
     */
    public SimpleStringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    /**
     * Returns the caregiver phone number using the legacy naming used in UI code.
     *
     * @return telephone number
     */
    public String getTelephoneNumber() {
        return getPhoneNumber();
    }

    /**
     * Sets the caregiver phone number using the legacy naming used in UI code.
     *
     * @param telephoneNumber new telephone number
     */
    public void setTelephoneNumber(String telephoneNumber) {
        setPhoneNumber(telephoneNumber);
    }

    /**
     * Returns the caregiver role.
     *
     * @return role value
     */
    public String getRole() {
        return role.get();
    }

    /**
     * Sets the caregiver role.
     *
     * @param role new role value
     */
    public void setRole(String role) {
        this.role.set(role);
    }

    /**
     * Returns the JavaFX property for the caregiver role.
     *
     * @return role property
     */
    public SimpleStringProperty roleProperty() {
        return role;
    }
}