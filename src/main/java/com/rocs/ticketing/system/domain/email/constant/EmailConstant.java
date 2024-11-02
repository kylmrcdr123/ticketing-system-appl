package com.rocs.ticketing.system.domain.email.constant;

public class EmailConstant {
    /**
     * This protocol is used for sending email.
     */
    public static final String SIMPLE_MAIL_TRANSFER_PROTOCOL = "smtps";

    /**
     * Email address used as the sender's address.
     */
    public static final String USERNAME = "rogationist.computing.society@gmail.com";

    /**
     * Password used for authenticating the sender's email account.
     */
    public static final String PASSWORD = "mpeo apom ojqd nqwr";

    /**
     * Default sender's email address.
     */
    public static final String FROM_EMAIL = "rogationist.computing.society@gmail.com";
    /**
     * Subject line for the welcome email sent to users.
     */
    public static final String EMAIL_SUBJECT = "Welcome to Rogationist Computing Society: Your New Password";
    /**
     * SMTP server address for Gmail.
     */
    public static final String GMAIL_SMTP_SERVER = "smtp.gmail.com";
    /**
     *SMTP host.
     */
    public static final String SMTP_HOST = "mail.smtp.host";
    /**
     * SMTP authentication.
     */
    public static final String SMTP_AUTH = "mail.smtp.auth";
    /**
     * Specifying the SMTP port.
     */
    public static final String SMTP_PORT = "mail.smtp.port";

    /**
     * Default port for SMTP
     */
    public static final int DEFAULT_PORT = 465;
    /**
     * Enabling STARTTLS in SMTP.
     */
    public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    /**
     * Requiring STARTTLS in SMTP.
     */
    public static final String SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";

}
