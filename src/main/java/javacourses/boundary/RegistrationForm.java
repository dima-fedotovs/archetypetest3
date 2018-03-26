package javacourses.boundary;

import javacourses.control.EmailSender;
import javacourses.control.UserControl;
import javacourses.control.Util;
import javacourses.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.1.0
 * @since 1.1.0
 */
@ViewScoped
@Named
public class RegistrationForm implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationForm.class);
    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    @Inject
    private EmailSender emailSender;
    @Inject
    private UserControl userControl;

    private String email;
    private String fullName;
    private String password1;
    private String password2;
    private String confirmationCode;
    private boolean awaitConfirmation = false;

    @Transactional
    public void register() {
        if (!Objects.equals(password1, password2)) {
            Util.addError("registration:password2", "Password doesn't match the confirm password");
            return;
        }

        if (userControl.emailExists(email)) {
            Util.addError("registration:email", "This email already exists");
            return;
        }

        User u = userControl.createUser(email, fullName, password1);
        String code = emailSender.sendConfirmationCode(email);
        u.setConfirmationCode(code);

        awaitConfirmation = true;
    }



    @Transactional
    public String confirm() {
        User u = userControl.findUserByEmail(email, false);
        if (u != null && Objects.equals(u.getConfirmationCode(), confirmationCode)) {
            u.setConfirmed(true);
            return "/sign-in.xhtml?faces-redirect=true";
        } else {
            Util.addError("registration:confirmationCode", "Incorrect confirmation code");
            return null;
        }
    }

    public String getEmailRegex() {
        return EMAIL_REGEX;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public boolean isAwaitConfirmation() {
        return awaitConfirmation;
    }

    public void setAwaitConfirmation(boolean awaitConfirmation) {
        this.awaitConfirmation = awaitConfirmation;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }
}
