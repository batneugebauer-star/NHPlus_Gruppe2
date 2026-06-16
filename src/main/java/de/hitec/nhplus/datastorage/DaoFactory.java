package de.hitec.nhplus.datastorage;

/**
 * Erstellt DAO-Objekte für die verschiedenen Datenbanktabellen.
 * Diese Klasse ist ein gemeinsamer Kopplungspunkt: jeder Workstream
 * (A: Caregiver, B: über Caregiver/Patient/Treatment, C: User) ergänzt
 * hier seine eigene "create...Dao()" Methode.
 */

public class DaoFactory {

    private static DaoFactory instance;

    private DaoFactory() {
    }

    public static synchronized DaoFactory getDaoFactory() {
        if (DaoFactory.instance == null) {
            DaoFactory.instance = new DaoFactory();
        }
        return DaoFactory.instance;
    }

    public TreatmentDao createTreatmentDao() {
        return new TreatmentDao(ConnectionBuilder.getConnection());
    }

    public PatientDao createPatientDao() {
        return new PatientDao(ConnectionBuilder.getConnection());
    }

    public CaregiverDao createCaregiverDao() {return new CaregiverDao(ConnectionBuilder.getConnection());
    }

    /**
     * Erstellt ein neues UserDao für den Zugriff auf die user-Tabelle.
     * Gehört zum Login-System (Workstream C).
     *
     * @return ein neues UserDao-Objekt
     */
    public UserDao createUserDao() {
        return new UserDao(ConnectionBuilder.getConnection());
    }
    public EventLogDao createEventLogDao() {return new EventLogDao(ConnectionBuilder.getConnection());
    }

}
