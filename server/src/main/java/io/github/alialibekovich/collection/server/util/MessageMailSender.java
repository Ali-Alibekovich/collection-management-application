package io.github.alialibekovich.collection.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Sends a welcome e-mail to newly registered users.
 *
 * <p>The sender account is configured through the {@code MAIL_USER} and
 * {@code MAIL_PASSWORD} environment variables ({@code SMTP_HOST} and
 * {@code SMTP_PORT} are optional). When no account is configured the sender
 * is disabled and registration proceeds without e-mail notifications.
 * Passwords are deliberately never included in the message.</p>
 */
public final class MessageMailSender {

    private static final Logger log = LoggerFactory.getLogger(MessageMailSender.class);

    private static String senderAddress;
    private static String senderPassword;
    private static String smtpHost;
    private static String smtpPort;

    private MessageMailSender() {
    }

    public static void configureFromEnvironment() {
        senderAddress = System.getenv("MAIL_USER");
        senderPassword = System.getenv("MAIL_PASSWORD");
        smtpHost = valueOrDefault(System.getenv("SMTP_HOST"), "smtp.yandex.ru");
        smtpPort = valueOrDefault(System.getenv("SMTP_PORT"), "465");
        if (isConfigured()) {
            log.info("Mail notifications enabled, sending from {}", senderAddress);
        } else {
            log.info("MAIL_USER/MAIL_PASSWORD are not set, mail notifications are disabled");
        }
    }

    public static boolean isConfigured() {
        return senderAddress != null && !senderAddress.isEmpty()
                && senderPassword != null && !senderPassword.isEmpty();
    }

    public static void sendWelcome(String login, String recipient) throws MessagingException {
        if (!isConfigured()) {
            log.debug("Skipping welcome e-mail for '{}': mail is not configured", login);
            return;
        }
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.socketFactory.port", smtpPort);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderAddress, senderPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject("Добро пожаловать в Collection Management App");
        message.setText("Здравствуйте!\n\nУчётная запись '" + login + "' успешно создана.\n");
        Transport.send(message);
        log.info("Welcome e-mail sent to the user '{}'", login);
    }

    private static String valueOrDefault(String value, String defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }
}
