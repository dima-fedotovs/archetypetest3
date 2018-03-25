package javacourses.boundary;

import javacourses.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.Serializable;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.1.0
 * @since 1.1.0
 */
@ViewScoped
@Named
public class SignInForm implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SignInForm.class);
    @PersistenceContext
    private EntityManager em;
    @Inject
    private CurrentUser currentUser;
    @Inject
    private HttpServletRequest request;

    private String email;
    private String password;

    @Transactional
    public String signIn() {
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        query.setParameter("email", email);
        try {
            User user = query.getSingleResult();
            request.login(email, password);
            currentUser.setSignedInUser(user);
            logger.debug("User {} is signed in", user);
            return "/sign-in.xhtml?faces-redirect=true";
        } catch (NoResultException e) {
            logger.error("Sign in error", e);
            addMessageUnknowEmail();
        } catch (ServletException e) {
            logger.error("Sign in error", e);
            addMessageWrongPassword();
        }
        return null;
    }

    public void signOut() {
        try {
            request.logout();
            currentUser.setSignedInUser(null);
        } catch (ServletException e) {
            logger.error("Sign out error", e);
        }
    }

    private void addMessageWrongPassword() {
        FacesContext.getCurrentInstance()
                .addMessage("signInForm:password",
                        new FacesMessage("Wrong password"));
    }

    private void addMessageUnknowEmail() {
        FacesContext.getCurrentInstance()
                .addMessage("signInForm:email",
                        new FacesMessage("Unknown email"));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
