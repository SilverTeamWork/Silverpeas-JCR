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

import org.apache.jackrabbit.oak.spi.security.authentication.AbstractLoginModule;
import org.jetbrains.annotations.NotNull;
import org.silverpeas.core.i18n.I18n;
import org.silverpeas.core.security.authentication.Authentication;
import org.silverpeas.core.security.authentication.AuthenticationCredential;
import org.silverpeas.core.security.authentication.AuthenticationResponse;
import org.silverpeas.core.util.StringUtil;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.login.LoginException;
import java.security.Principal;
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
public class SilverpeasSimpleJCRLoginModule extends AbstractLoginModule {

  private Credentials credentials = null;
  private Principal principal = null;

  @Override
  protected @NotNull Set<Class> getSupportedCredentials() {
    return Set.of(SimpleCredentials.class);
  }

  @Override
  public boolean login() throws LoginException {
    try {
      // Get credentials using a JAAS callback
      credentials = getCredentials();
      if (credentials instanceof SimpleCredentials) {
        // Use the credentials to authenticate the subject and then to get its principal to access
        // the JCR repository
        principal = null;

        Authentication auth = Authentication.get();
        AuthenticationCredential cred = convert((SimpleCredentials) credentials);
        AuthenticationResponse resp = auth.authenticate(cred);
        if (resp.getStatus().succeeded()) {
          /* get the user from the authentication response and then build the principal */
          //principal = new SilverpeasUserPrincipal()
        } else {
          throw new LoginException(resp.getStatus().getMessage(I18n.get().getDefaultLanguage()));
        }

        return principal != null;
      }

      return false;

    } catch (Exception ex) {
      throw new LoginException(ex.getMessage());
    }
  }

  @Override
  public boolean commit() throws LoginException {
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
  public boolean logout() throws LoginException {
    if (credentials != null &&  principal != null) {
      subject.getPrivateCredentials().remove(credentials);
      subject.getPrincipals().remove(principal);
      return true;
    }
    return false;
  }

  @NotNull
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
