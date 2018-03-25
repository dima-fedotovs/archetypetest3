package javacourses.boundary;

import javacourses.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.1.0
 * @since 1.1.0
 */
@RequestScoped
@Named
public class RegistrationForm {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationForm.class);
    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    @PersistenceContext
    private EntityManager em;
    private String email;
    private String fullName;
    private String password1;
    private String password2;

    @Transactional
    public String register() {
        if (!Objects.equals(password1, password2)) {
            FacesContext.getCurrentInstance()
                    .addMessage("registrationForm:password2",
                            new FacesMessage("Password doesn't match the confirm password"));
            return null;
        }

        if (emailExists()) {
            FacesContext.getCurrentInstance()
                    .addMessage("registrationForm:email",
                            new FacesMessage("This email already exists"));
            return null;
        }

        createUser();

        return "/sign-in.xhtml?faces-redirect=true";
    }

    private void createUser() {
        User u = new User();
        u.setEmail(email);
        u.setFullName(fullName);
        u.setPassword(hash(password1));
        em.persist(u);
    }

    private String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return Base64.getMimeEncoder(76, new byte[]{'\n'}).encodeToString(hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.error("This never can happen!", e);
            throw new IllegalStateException(e);
        }
    }

    private boolean emailExists() {
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        query.setParameter("email", email);
        List<User> result = query.getResultList();
        return result.size() > 0;
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
}
