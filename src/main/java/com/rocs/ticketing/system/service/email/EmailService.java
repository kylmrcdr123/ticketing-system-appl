package com.rocs.ticketing.system.service.email;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Properties;

import static com.rocs.ticketing.system.domain.email.constant.EmailConstant.*;
/**
 * This Service sends emails, including OTP confirmation emails.
 */
@Service
public class EmailService {
    /**
     * Sends an OTP (One-Time Password) to the provided email address.
     *
     * @param email the recipient's email address
     * @param otp   the one-time password to be sent
     */
    public void sendNewPasswordEmail(String email, String otp) throws MessagingException {
        Message message = createEmail(email, otp);
        try (Transport smtpTransport = getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL)) {
            smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
            smtpTransport.sendMessage(message, message.getAllRecipients());
        }
    }
    /**
     * Creates a new email message with the OTP.
     *
     * @param email the recipient's email address
     * @param otp   the one-time password to be included in the email
     * @return the constructed Message
     */
    private Message createEmail(String email,String otp) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Your OTP for confirmation is:" + otp);
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }
    /**
     * Retrieves the email session
     *
     * @return a Session
     */
    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }
}
