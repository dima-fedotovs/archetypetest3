package javacourses.boundary;

import javacourses.entity.User;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Objects;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.1.0
 * @since 1.1.0
 */
@RequestScoped
@Named
public class SignInForm {
    @PersistenceContext
    private EntityManager em;
    @Inject
    private CurrentUser currentUser;

    private String email;
    private String password;

    @Transactional
    public void signIn() {
        currentUser.setSignedInUser(null);
        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        query.setParameter("email", email);
        try {
            User user = query.getSingleResult();
            if (!Objects.equals(user.getPassword(), password)) {
                addMessageWrongPassword();
                return;
            }
            currentUser.setSignedInUser(user);
        } catch (NoResultException e) {
            addMessageUnknowEmail();
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
