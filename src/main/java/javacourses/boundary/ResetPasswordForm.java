package javacourses.boundary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
@ViewScoped
@Named
public class ResetPasswordForm implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordForm.class);
    @PersistenceContext
    private EntityManager em;
    @Inject
    private EmailSender emailSender;

    private String email;
    private String confirmationCode;
    private String password1;
    private String password2;
    private boolean confirmationSend;

    public void request() {
        confirmationSend = true;
    }

    public String confirm() {
        confirmationSend = true;
        logger.debug("Confirming new password");
        return null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public boolean isConfirmationSend() {
        return confirmationSend;
    }

    public void setConfirmationSend(boolean confirmationSend) {
        this.confirmationSend = confirmationSend;
    }
}
