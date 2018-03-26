package javacourses.control;

import javacourses.entity.Role;
import javacourses.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
@Stateless
public class UserControl {
    private static final Logger logger = LoggerFactory.getLogger(UserControl.class);
    @PersistenceContext
    private EntityManager em;

    public User findUserByEmail(String email, boolean confirmed) {
        try {
            return em.createQuery("select u from User u where u.email = :email AND u.confirmed = :confirmed", User.class)
                    .setParameter("email", email)
                    .setParameter("confirmed", confirmed)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.error(String.format("user (email: %s; confirmed: %s) not found", email, confirmed), e);
            return null;
        }
    }


    public Role findRole(String name) {
        return em.createQuery("select r from Role r where r.name = :name", Role.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return Base64.getMimeEncoder(76, new byte[]{'\n'}).encodeToString(hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.error("This never can happen!", e);
            throw new IllegalStateException(e);
        }
    }

    public boolean emailExists(String email) {
        return em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList()
                .size() > 0;
    }

    public User createUser(String email, String fullName, String password) {
        Role r = findRole("user");
        User u = new User();
        u.setEmail(email);
        u.setFullName(fullName);
        u.setPassword(hashPassword(password));
        u.setConfirmed(false);
        u.setRoles(Collections.singleton(r));
        em.persist(u);
        return u;
    }

}
