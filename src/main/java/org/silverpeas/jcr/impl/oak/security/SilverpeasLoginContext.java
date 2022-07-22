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

package org.silverpeas.jcr.impl.oak.security;

import org.apache.jackrabbit.oak.spi.security.authentication.LoginContext;
import org.silverpeas.jcr.security.LoginModuleRegistry;

import javax.jcr.Credentials;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context of a login/logout to the JCR repository by a user within the scope of Silverpeas out of
 * any JAAS process as the security system of Silverpeas isn't built upon this framework.
 * Nevertheless, the JAAS logic in login/logout is respected (but not how it is performed).
 * <p>
 * At login or logout, it checks the type of credentials that is passed in order to invoke the
 * {@link javax.security.auth.spi.LoginModule} instances that support such a credentials. It chains
 * then their invocation to perform the actual operation of login/logout until a {@link LoginModule}
 * instance responds successfully. When an authentication succeeds, the context expects through the
 * {@link LoginModule#commit()} method the subject to be enriched with the {@link
 * java.security.Principal} that identifies the authenticated user. Otherwise a {@link
 * LoginException} is thrown.
 * </p>
 * <p>
 * The {@link LoginModule} instances to consider for a given credentials are provided by the {@link
 * LoginModuleRegistry} object. So, any {@link LoginModule} defined for JCR authentication have to
 * register themselves to this registry by indicating the type of credentials they support.
 * </p>
 * @author mmoquillon
 */
public class SilverpeasLoginContext implements LoginContext {

  private final Subject subject;

  SilverpeasLoginContext(final Subject subject) {
    this.subject = subject;
  }

  @Override
  public Subject getSubject() {
    return subject;
  }

  @Override
  public void login() throws LoginException {
    applyOnLoginModule(l -> l.login() && l.commit());
  }

  @Override
  public void logout() throws LoginException {
    applyOnLoginModule(LoginModule::logout);
  }

  private void applyOnLoginModule(AuthOp operation) throws LoginException {
    Credentials credentials = getCredentials();
    if (credentials == null) {
      throw new LoginException("No credentials!");
    }
    List<LoginModule> modules = getLoginModuleRegistry().getLoginModule(credentials.getClass());
    if (modules.isEmpty()) {
      throw new LoginException("Unsupported credentials: " + credentials.getClass()
          .getName());
    }
    Map<String, ?> sharedState = new HashMap<>();
    Map<String, ?> options = new HashMap<>();
    boolean succeeded = false;
    for (int i = 0; i < modules.size() && !succeeded; i++) {
      LoginModule module = modules.get(i);
      module.initialize(getSubject(), null, sharedState, options);
      succeeded = operation.check(module);
    }
    if (!succeeded) {
      throw new LoginException(
          "No authentication mechanism matches the credentials " + credentials);
    }
  }

  private Credentials getCredentials() {
    var allCredentials = subject.getPrivateCredentials();
    if (allCredentials.isEmpty()) {
      return null;
    }
    return (Credentials) allCredentials.iterator().next();
  }

  private LoginModuleRegistry getLoginModuleRegistry() {
    return LoginModuleRegistry.getInstance();
  }

  @FunctionalInterface
  private interface AuthOp {

    boolean check(final LoginModule loginModule) throws LoginException;
  }
}
