package org.silverpeas.jcr.security;

import org.apache.jackrabbit.api.security.authentication.token.TokenCredentials;
import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.util.StringUtil;

import javax.annotation.Nonnull;
import javax.jcr.Credentials;
import javax.security.auth.login.LoginException;
import java.util.Set;

/**
 * A login module to authenticate the users who access the JCR repository used by Silverpeas. This
 * login module accepts only {@link TokenCredentials} carrying the API token of the user.
 * <p>
 * The login module verifies the token by asking to Silverpeas the user having such a token. If no
 * such a user exists, then the authentication is considered as a failure.
 * </p>
 * @author mmoquillon
 */
public class SilverpeasTokenJCRLoginModule extends SilverpeasJCRLoginModule {

  @SuppressWarnings("rawtypes")
  static final Set<Class> SUPPORTED_CREDENTIALS = Set.of(TokenCredentials.class);

  @SuppressWarnings("rawtypes")
  @Override
  @Nonnull
  protected Set<Class> getSupportedCredentials() {
    return SUPPORTED_CREDENTIALS;
  }

  @Override
  protected User authenticateUser(final Credentials credentials)
      throws LoginException {
    String token = ((TokenCredentials) credentials).getToken();
    User user = User.provider().getUserByToken(token);
    if (user == null) {
      throw new LoginException("User API Token '" + token + "' non valid!");
    }
    return user;
  }

  @Override
  @Nonnull
  protected AccessContext getAccessContext(final Credentials credentials) {
    final String grantedDocPath = ((TokenCredentials) credentials).getAttribute(
        WebDavAccessContext.AUTHORIZED_DOCUMENT_PATH_ATTRIBUTE);
    return StringUtil.isDefined(grantedDocPath) ?
        new WebDavAccessContext(grantedDocPath) :
        AccessContext.EMPTY;
  }
}
