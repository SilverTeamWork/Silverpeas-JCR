package org.silverpeas.jcr.security;

import org.apache.jackrabbit.api.security.authentication.token.TokenCredentials;
import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.admin.user.model.UserReference;
import org.silverpeas.core.security.token.persistent.PersistentResourceToken;

import javax.annotation.Nonnull;
import javax.jcr.Credentials;
import javax.security.auth.login.LoginException;
import java.util.Set;

public class SilverpeasTokenJCRLoginModule extends SilverpeasJCRLoginModule {

  @SuppressWarnings("rawtypes")
  @Override
  @Nonnull
  protected Set<Class> getSupportedCredentials() {
    return Set.of(TokenCredentials.class);
  }

  @Override
  protected User authenticateUser(final Credentials credentials)
      throws LoginException {
    String token = ((TokenCredentials) credentials).getToken();
    final PersistentResourceToken userToken = PersistentResourceToken.getToken(token);
    final UserReference userRef = userToken.getResource(UserReference.class);
    if (userRef != null) {
      return userRef.getEntity();
    } else {
      throw new LoginException("User API Token '" + token + "' non valid!");
    }
  }
}
