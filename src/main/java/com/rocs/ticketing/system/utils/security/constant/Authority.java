package com.rocs.ticketing.system.utils.security.constant;

/**
 * Contains predefined authorities for different roles
 */
public class Authority {
    /** Authorities for students. */
    public static final String[] STUDENT_AUTHORITIES = {"user:read", "user:create"};
    /** Authorities for employees. */
    public static final String[] EMPLOYEE_AUTHORITIES = {"user:read",  "user:create"};
    /** Authorities for administrators. */
    public static final String[] MISSTAFF_AUTHORITIES = {"user:read",  "user:create", "user:update"};
    /** Authorities for administrators. */
    public static final String[] ADMIN_AUTHORITIES = {"user:read", "user:create", "user:update"};
    /** Authorities for super administrators. */
    public static final String[] SUPER_ADMIN_AUTHORITIES = {"user:read", "user:create", "user:update", "user:delete"};
}
