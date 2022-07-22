package org.silverpeas.jcr.security;

import org.apache.jackrabbit.oak.spi.security.authentication.AbstractLoginModule;
import org.silverpeas.core.admin.user.model.User;

import javax.jcr.Credentials;
import javax.security.auth.login.LoginException;
import java.security.Principal;

public abstract class SilverpeasJCRLoginModule extends AbstractLoginModule {

  private Credentials credentials = null;
  private Principal principal = null;

  @Override
  public boolean login() throws LoginException {
    try {
      // Get credentials using a JAAS callback
      credentials = getCredentials();
      if (isCredentialsSupported(credentials)) {
        // Use the credentials to authenticate the subject and then to get its principal to access
        // the JCR repository
        User user = authenticateUser(credentials);
        principal = new SilverpeasUserPrincipal(user);

        return true;
      }

      return false;

    } catch (Exception ex) {
      throw new LoginException(ex.getMessage());
    }
  }

  @Override
  public boolean commit() {
    if (principal != null) {
      subject.getPrincipals().add(principal);
      if (credentials != null) {
        subject.getPrivateCredentials().add(credentials);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean logout() {
    if (credentials != null && principal != null) {
      subject.getPrivateCredentials().remove(credentials);
      subject.getPrincipals().remove(principal);
      return true;
    }
    return false;
  }

  /**
   * Authenticates the user behind the specified credentials.
   * @param credentials the credentials of a user in Silverpeas.
   * @param <T>
   * @return the user identified by the given credentials if and only if the authentication
   * succeeds.
   * @throws LoginException if the authentication of the user fails.
   */
  abstract protected User authenticateUser(final Credentials credentials)
      throws LoginException;

  private boolean isCredentialsSupported(final Credentials credentials) {
    return getSupportedCredentials().stream()
        .anyMatch(c -> c.isInstance(credentials));
  }
}
