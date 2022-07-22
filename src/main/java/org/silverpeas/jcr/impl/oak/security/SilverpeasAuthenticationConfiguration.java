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

import org.apache.jackrabbit.api.security.authentication.token.TokenCredentials;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.spi.security.SecurityConfiguration;
import org.apache.jackrabbit.oak.spi.security.authentication.AuthenticationConfiguration;
import org.apache.jackrabbit.oak.spi.security.authentication.LoginContextProvider;

import javax.annotation.Nonnull;
import javax.jcr.SimpleCredentials;
import javax.security.auth.Subject;

/**
 * Configuration defining the authentication mechanism Oak has to apply when accessing a repository
 * within the context of Silverpeas. The goal is to delegate the authentication to Silverpeas
 * without using JAAS, for Silverpeas security isn't built upon JAAS, and to avoid the
 * synchronization of users and groups of users with the JCR repository.
 * @author mmoquillon
 */
public class SilverpeasAuthenticationConfiguration extends SecurityConfiguration.Default
    implements AuthenticationConfiguration {

  @Override
  @Nonnull
  public LoginContextProvider getLoginContextProvider(
      @Nonnull final ContentRepository contentRepository) {
    return (credentials, workspaceName) -> {
      Subject subject = new Subject();
      if (credentials != null) {
        if (credentials instanceof TokenCredentials || credentials instanceof SimpleCredentials) {
          // login/password credentials and token credentials are private
          subject.getPrivateCredentials().add(credentials);
        } else {
          // other credentials are public (the information doesn't need to be hidden)
          subject.getPublicCredentials().add(credentials);
        }
      }

      return new SilverpeasLoginContext(subject);
    };
  }

  @Override
  @Nonnull
  public String getName() {
    return NAME;
  }

}
