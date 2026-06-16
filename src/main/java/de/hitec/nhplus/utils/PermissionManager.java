package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Role;

public class PermissionManager {

    private PermissionManager() {
    }

    public static Role getRole() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            return null;
        }

        if (SessionManager.getInstance().getLoggedInUser().isSuperUser()) {
            return Role.ADMIN;
        }

        Role role = SessionManager.getInstance().getLoggedInUser().getRole();
        return role != null ? role : Role.REGISTERED_NURSE;
    }

    public static boolean isAdmin() {
        return getRole() == Role.ADMIN;
    }

    public static boolean canManagePatients() {
        Role role = getRole();
        return role == Role.ADMIN || role == Role.LEITER;
    }

    public static boolean canManageCaregivers() {
        Role role = getRole();
        return role == Role.ADMIN || role == Role.LEITER;
    }

    public static boolean canManageDiagnoses() {
        Role role = getRole();
        return role == Role.ADMIN || role == Role.DOCTOR;
    }

    public static boolean canManageNursingDocumentation() {
        Role role = getRole();
        return role == Role.ADMIN
                || role == Role.REGISTERED_NURSE;
    }

    public static boolean canManageTherapies() {
        Role role = getRole();
        return role == Role.ADMIN
                || role == Role.THERAPIST;
    }

    public static boolean canViewResidents() {
        return getRole() != null;
    }

    public static boolean canManageRoles() {
        Role role = getRole();
        return role == Role.ADMIN || role == Role.LEITER;
    }

}