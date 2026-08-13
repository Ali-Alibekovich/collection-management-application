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
 * Sends a welcome e-mail to newly registered users. When no sender account is
 * configured the mailer is disabled and registration proceeds without
 * notifications. Passwords are deliberately never included in the message.
 */
public final class WelcomeMailer {

    private static final Logger log = LoggerFactory.getLogger(WelcomeMailer.class);

    private final String senderAddress;
    private final String senderPassword;
    private final String smtpHost;
    private final String smtpPort;

    public WelcomeMailer(String senderAddress, String senderPassword, String smtpHost, String smtpPort) {
        this.senderAddress = senderAddress;
        this.senderPassword = senderPassword;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        if (isConfigured()) {
            log.info("Mail notifications enabled, sending from {}", senderAddress);
        } else {
            log.info("MAIL_USER/MAIL_PASSWORD are not set, mail notifications are disabled");
        }
    }

    public static WelcomeMailer fromEnvironment() {
        return new WelcomeMailer(
                System.getenv("MAIL_USER"),
                System.getenv("MAIL_PASSWORD"),
                valueOrDefault(System.getenv("SMTP_HOST"), "smtp.yandex.ru"),
                valueOrDefault(System.getenv("SMTP_PORT"), "465"));
    }

    public boolean isConfigured() {
        return senderAddress != null && !senderAddress.isEmpty()
                && senderPassword != null && !senderPassword.isEmpty();
    }

    public void sendWelcome(String login, String recipient) throws MessagingException {
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
