package org.silverpeas.jcr.security;

import org.apache.jackrabbit.api.security.authentication.token.TokenCredentials;
import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.admin.user.service.UserProvider;

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
    User user = UserProvider.get().getUserByToken(token);
    if (user == null) {
      throw new LoginException("User API Token '" + token + "' non valid!");
    }
    return user;
  }
}
