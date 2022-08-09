package org.silverpeas.jcr.security;

import org.apache.jackrabbit.oak.spi.security.authentication.AbstractLoginModule;
import org.silverpeas.core.admin.user.model.User;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SilverpeasJCRLoginModule extends AbstractLoginModule {

  private Credentials userId = null;
  private Principal principal = null;

  private boolean success = false;

  private boolean initialized = false;

  @Override
  public void initialize(final Subject subject, final CallbackHandler callbackHandler,
      final Map<String, ?> sharedState, final Map<String, ?> options) {
    super.initialize(subject, callbackHandler, sharedState, options);
    this.initialized = true;
  }

  @Override
  public boolean login() throws LoginException {
    try {
      // Get credentials using a JAAS callback
      Credentials credentials = getCredentials();
      success = false;
      if (isCredentialsSupported(credentials)) {
        // Use the credentials to authenticate the subject and then to produce the principal
        // required to access the JCR
        User user = authenticateUser(credentials);
        principal = new SilverpeasUserPrincipal(user);
        success = true;
      }

      return success;

    } catch (Exception ex) {
      throw new LoginException(ex.getMessage());
    }
  }

  @Override
  public boolean commit() {
    if (!success) {
      clearState();
      return false;
    }
    if (!subject.isReadOnly()) {
      subject.getPrincipals().add(principal);
      // Fallback in the JCR implementations to figure out the user identifier in session. This for
      // the case the Silverpeas wrapper of the actual implementation doesn't set such information.
      // The identifier of the user for whom a session has been opened is required by the
      // reentrant JCR session mechanism as implemented in org.silverpeas.jcr.JCRSession.
      // See javax.jcr.Session#getUserID method
      userId = new SimpleCredentials(principal.getName(), new char[0]);
      subject.getPublicCredentials().add(userId);
    }
    return true;
  }

  @Override
  public boolean logout() throws LoginException {
    Set<Object> userCredentials = getAllCredentials();
    Set<Principal> userPrincipals = getAllPrincipals();
    return logout(userCredentials.isEmpty() ? null : userCredentials,
        userPrincipals.isEmpty() ? null : userPrincipals);
  }

  /**
   * Is this module initialized?
   * @return true if the module was initialized before any use. False otherwise.
   */
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  protected void clearState() {
    super.clearState();
    this.userId = null;
    this.principal = null;
  }

  protected Set<Object> getAllCredentials() {
    return Stream.of(userId).filter(Objects::nonNull).collect(Collectors.toSet());
  }

  protected Set<Principal> getAllPrincipals() {
    return Stream.of(principal).filter(Objects::nonNull).collect(Collectors.toSet());
  }

  /**
   * Authenticates the user behind the specified credentials.
   * @param credentials the credentials of a user in Silverpeas.
   * @return the user identified by the given credentials if and only if the authentication
   * succeeds.
   * @throws LoginException if the authentication of the user fails.
   */
  protected abstract User authenticateUser(final Credentials credentials) throws LoginException;

  private boolean isCredentialsSupported(final Credentials credentials) {
    return getSupportedCredentials().stream().anyMatch(c -> c.isInstance(credentials));
  }
}
