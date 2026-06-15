package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Role;
import de.hitec.nhplus.model.User;

public class PermissionManager {

    private static User currentUser() {
        return SessionManager.getInstance().getLoggedInUser();
    }

    private static Role currentRole() {

        if (currentUser() == null) {
            return null;
        }

        return currentUser().getRole();
    }

    public static boolean canManageRoles() {
        return currentRole() == Role.ADMIN;
    }

    public static boolean canManageCaregivers() {
        return currentRole() == Role.ADMIN
                || currentRole() == Role.NURSING_HOME_MANAGER
                || currentRole() == Role.WARD_MANAGER;
    }

    public static boolean canEditResidents() {
        return currentRole() == Role.ADMIN
                || currentRole() == Role.NURSING_HOME_MANAGER
                || currentRole() == Role.WARD_MANAGER;
    }

    public static boolean canEditNursingDocumentation() {
        return currentRole() == Role.ADMIN
                || currentRole() == Role.NURSING_HOME_MANAGER
                || currentRole() == Role.WARD_MANAGER
                || currentRole() == Role.REGISTERED_NURSE
                || currentRole() == Role.NURSING_ASSISTANT;
    }

    public static boolean canEditDiagnoses() {
        return currentRole() == Role.DOCTOR;
    }

    public static boolean canDocumentTherapy() {
        return currentRole() == Role.THERAPIST;
    }

    public static boolean canViewResidents() {
        return currentRole() != null;
    }
}