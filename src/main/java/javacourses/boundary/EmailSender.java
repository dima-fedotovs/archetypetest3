package javacourses.boundary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
@Stateless
public class EmailSender {
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);
    @Resource(lookup = "java:jboss/mail/gmail")
    private Session mailSession;

    private String generateConfirmationCode() {
        UUID id = UUID.randomUUID();
        long n1 = Math.abs(id.getLeastSignificantBits());
        long n2 = Math.abs(id.getMostSignificantBits());
        String result = Long.toString(n2, Character.MAX_RADIX) +
                Long.toString(n1, Character.MAX_RADIX);
        return result.toUpperCase();
    }


    public String sendConfirmationCode(String email) {
        String code = generateConfirmationCode();
        MimeMessage msg = new MimeMessage(mailSession);
        try {
            msg.setRecipients(Message.RecipientType.TO, email);
            msg.setSubject("Email confirmation", "UTF-8");
            msg.setText("Your confirmation code is " + code, "UTF-8");

            Transport.send(msg);
        } catch (MessagingException e) {
            logger.error("Sending email error", e);
        }
        return code;
    }

    public String sendConfirmationCodeResetPassword(String email) {
        String code = generateConfirmationCode();
        MimeMessage msg = new MimeMessage(mailSession);
        try {
            msg.setRecipients(Message.RecipientType.TO, email);
            msg.setSubject("Password reset", "UTF-8");
            msg.setText("You need this code to reset your password: " + code, "UTF-8");

            Transport.send(msg);
        } catch (MessagingException e) {
            logger.error("Sending email error", e);
        }

        return code;
    }
}
