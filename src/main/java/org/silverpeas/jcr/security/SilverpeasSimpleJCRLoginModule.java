/*
 * Copyright (c) 2000 - 2022 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "https://www.silverpeas.org/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.silverpeas.jcr.security;

import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.i18n.I18n;
import org.silverpeas.core.security.authentication.Authentication;
import org.silverpeas.core.security.authentication.AuthenticationCredential;
import org.silverpeas.core.security.authentication.AuthenticationResponse;
import org.silverpeas.core.security.authentication.exception.AuthenticationException;
import org.silverpeas.core.util.StringUtil;

import javax.annotation.Nonnull;
import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.login.LoginException;
import java.util.Set;

/**
 * A login module to authenticate the users that access the JCR repository used by Silverpeas.
 * This login module takes {@link javax.jcr.SimpleCredentials} in which are set both the user
 * connection identifier and the associated password.
 * <p>
 * The login module delegates the authentication itself to an authentication service that has the
 * knowledge of how to perform the authentication on behalf of Silverpeas.
 * </p>
 * @author mmoquillon
 */
public class SilverpeasSimpleJCRLoginModule extends SilverpeasJCRLoginModule {

  @SuppressWarnings("rawtypes")
  @Override
  @Nonnull
  protected Set<Class> getSupportedCredentials() {
    return Set.of(SimpleCredentials.class);
  }

  @Override
  protected User authenticateUser(final Credentials credentials) throws LoginException {
    Authentication auth = Authentication.get();
    AuthenticationCredential cred = convert((SimpleCredentials) credentials);
    AuthenticationResponse resp = auth.authenticate(cred);
    if (resp.getStatus().succeeded()) {
      try {
        /* get the user from the authentication response and then build the principal */
        String authToken = resp.getToken();
        return auth.getUserByAuthToken(authToken);
      } catch (AuthenticationException e) {
        throw new LoginException(e.getMessage());
      }
    } else {
      throw new LoginException(resp.getStatus().getMessage(I18n.get().getDefaultLanguage()));
    }
  }

  @Nonnull
  private AuthenticationCredential convert(final SimpleCredentials credentials) throws LoginException {
    String userId = credentials.getUserID();
    if (StringUtil.isNotDefined(userId)) {
      throw new LoginException("No user ID defined!");
    }
    String[] userIdParts = userId.split("@domain");
    if (userIdParts.length != 2) {
      throw new LoginException("Bad user ID format!");
    }
    return AuthenticationCredential
        .newWithAsLogin(userIdParts[0])
        .withAsDomainId(userIdParts[1])
        .withAsPassword(String.valueOf(credentials.getPassword()));
  }
}
