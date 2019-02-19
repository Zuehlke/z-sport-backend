package com.zuehlke.zSport.Service;

import com.zuehlke.zSport.Batch.NotificationEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailService {

    private final static Logger LOG = LoggerFactory.getLogger(EmailService.class);

    @Value("${application.email-service.enabled}")
    private boolean enabled;

    @Value("${application.email-service.smtp-host}")
    private String host;

    @Value("${application.email-service.smtp-port}")
    private String port;

    @Value("${application.email-service.smtp-auth.user}")
    private String authUser;

    @Value("${application.email-service.smtp-port}")
    private String authPassword;

    @Value("${application.email-service.from-address}")
    private String fromAddress;


    public void send(NotificationEmail email) throws MessagingException {
        if (enabled) {
            LOG.info("Send " + email.toString());
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(authUser, authPassword);
                }
            });
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromAddress, false));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getRecipient()));
            msg.setSubject(email.getContent());
            msg.setContent(email.getContent(), "text/html");
            msg.setSentDate(new Date());

            Transport.send(msg);
        } else {
            LOG.warn("Email service not enabled. Notification to <"+email.getRecipient()+"> will not be send. Email <"+email.getSubject()+">:" + email.getContent());
        }
    }
}
