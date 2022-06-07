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
package org.silverpeas.jcr.security.oak;

import com.google.common.collect.ImmutableList;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.SecurityConfiguration;
import org.apache.jackrabbit.oak.spi.security.SecurityProvider;
import org.apache.jackrabbit.oak.spi.security.authentication.AuthenticationConfiguration;
import org.apache.jackrabbit.oak.spi.security.authentication.OpenAuthenticationConfiguration;
import org.apache.jackrabbit.oak.spi.security.authorization.AuthorizationConfiguration;
import org.apache.jackrabbit.oak.spi.security.authorization.OpenAuthorizationConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Security provider for the Oak implementation of the JCR. It provides the objects required by Oak
 * to authenticate a user accessing a repository and to authorize him to navigate across the
 * repository's content tree.
 * <p>
 * The security provider provides to Oak both a custom
 * {@link org.apache.jackrabbit.oak.spi.security.authentication.AuthenticationConfiguration}
 * and a custom
 * {@link org.apache.jackrabbit.oak.spi.security.authorization.AuthorizationConfiguration}
 * objects that defines the rules to apply when authenticating and authorizing a user accessing the
 * JCR repository. Those configuration objects define a bridge between the Oak implementation of the
 * JCR and the Silverpeas world.
 * </p>
 * @author mmoquillon
 */
public class SilverpeasSecurityProvider implements SecurityProvider {

  @Override
  public @NotNull ConfigurationParameters getParameters(@Nullable final String name) {
    return ConfigurationParameters.EMPTY;
  }

  @Override
  public @NotNull Iterable<? extends SecurityConfiguration> getConfigurations() {
    return List.of(new SilverpeasAuthenticationConfiguration(),
        new SilverpeasAuthorizationConfiguration());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @NotNull T getConfiguration(@NotNull final Class<T> configClass) {
    if (AuthenticationConfiguration.class == configClass) {
      return (T) new SilverpeasAuthenticationConfiguration();
    } else if (AuthorizationConfiguration.class == configClass) {
      return (T) new SilverpeasAuthorizationConfiguration();
    } else {
      throw new IllegalArgumentException("Unsupported security configuration class " + configClass);
    }
  }
}
