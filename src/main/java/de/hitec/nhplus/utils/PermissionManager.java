package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Role;

public class PermissionManager {

    /**
     * Utility class for resolving the current user's role and permissions.
     */
    private PermissionManager() {
    }

    /**
     * Returns the effective role of the currently logged-in user.
     * <p>
     * Super users are treated as {@link Role#ADMIN}. If a logged-in user has no explicit role,
     * {@link Role#REGISTERED_NURSE} is used as fallback.
     * </p>
     *
     * @return the effective role of the current user, or {@code null} when no user is logged in
     */
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

    /**
     * Checks whether the current user has administrator permissions.
     *
     * @return {@code true} if the current user is an admin; otherwise {@code false}
     */
    public static boolean isAdmin() {
        return getRole() == Role.ADMIN;
    }

    /**
     * Checks whether the current user may manage patients.
     *
     * @return {@code true} for admins and leaders; otherwise {@code false}
     */
    public static boolean canManagePatients() {
        Role role = getRole();
        return role == Role.ADMIN || role == Role.LEITER;
    }

    /**
     * Checks whether the current user may manage caregivers.
     *
     * @return {@code true} for admins and leaders; otherwise {@code false}
     */
    public static boolean canManageCaregivers() {
        Role role = getRole();
        return role == Role.ADMIN || role == Role.LEITER;
    }

    /**
     * Checks whether the current user may manage diagnoses.
     *
     * @return {@code true} for admins and doctors; otherwise {@code false}
     */
    public static boolean canManageDiagnoses() {
        Role role = getRole();
        return role == Role.ADMIN || role == Role.DOCTOR;
    }

    /**
     * Checks whether the current user may manage nursing documentation.
     *
     * @return {@code true} for admins and registered nurses; otherwise {@code false}
     */
    public static boolean canManageNursingDocumentation() {
        Role role = getRole();
        return role == Role.ADMIN
                || role == Role.REGISTERED_NURSE;
    }

    /**
     * Checks whether the current user may manage therapies.
     *
     * @return {@code true} for admins and therapists; otherwise {@code false}
     */
    public static boolean canManageTherapies() {
        Role role = getRole();
        return role == Role.ADMIN
                || role == Role.THERAPIST;
    }

    /**
     * Checks whether the current user may view residents.
     *
     * @return {@code true} if any user is logged in; otherwise {@code false}
     */
    public static boolean canViewResidents() {
        return getRole() != null;
    }

    /**
     * Checks whether the current user may manage user roles.
     *
     * @return {@code true} for admins and leaders; otherwise {@code false}
     */
    public static boolean canManageRoles() {
        Role role = getRole();
        return role == Role.ADMIN || role == Role.LEITER;
    }

}