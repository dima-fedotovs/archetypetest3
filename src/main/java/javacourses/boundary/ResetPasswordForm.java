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
 * @version 1.0
 * @since 1.0
 */
@ViewScoped
@Named
public class ResetPasswordForm implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordForm.class);
    @Inject
    private EmailSender emailSender;
    @Inject
    private UserControl userControl;
    private String email;
    private String confirmationCode;
    private String password1;
    private String password2;
    private boolean confirmationSend;

    @Transactional
    public void request() {
        User u = userControl.findUserByEmail(email, true);
        if (u == null) {
            Util.addError("resetPassword:email", "Unknown email");
            return;
        }
        String code = emailSender.sendConfirmationCodeResetPassword(email);
        u.setConfirmationCode(code);
        confirmationSend = true;
    }

    @Transactional
    public String confirm() {
        User u = userControl.findUserByEmail(email, true);
        if (u == null) {
            Util.addError("resetPassword:email", "Unknown email");
            return null;
        }
        if (!Objects.equals(password1, password2)) {
            Util.addError("resetPassword:password2", "Password doesn't match the confirm password");
            return null;
        }

        if (Objects.equals(confirmationCode, u.getConfirmationCode())) {
            u.setConfirmationCode(null);
            u.setPassword(userControl.hashPassword(password1));
            return "/sign-in.xhtml?faces-redirect=true";
        } else {
            Util.addError("resetPassword:confirmationCode", "Invalid confirmation code");
            return null;
        }
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
