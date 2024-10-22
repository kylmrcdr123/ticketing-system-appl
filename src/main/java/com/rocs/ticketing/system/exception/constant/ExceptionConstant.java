package com.rocs.ticketing.system.exception.constant;

public class ExceptionConstant {
    /**
     * Message indicating that the user account is locked.
     */
    public static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";

    /**
     * Message indicating that the request method is not allowed for the endpoint.
     */
    public static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    /**
     * Message used for internal server errors.
     */
    public static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    /**
     * Message indicating that the provided credentials are incorrect.
     */
    public static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again";
    /**
     * Message indicating that the user account is disabled.
     */
    public static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration";
    /**
     * Message indicating an error occurred while processing a file.
     */
    public static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    /**
     * Message indicating that the user does not have sufficient permission.
     */
    public static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
    /**
     * Default error path for handling error responses.
     */
    public static final String ERROR_PATH = "/error";
}
