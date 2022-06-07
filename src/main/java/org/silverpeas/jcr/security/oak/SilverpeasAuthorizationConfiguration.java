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

import org.apache.jackrabbit.oak.api.Root;
import org.apache.jackrabbit.oak.namepath.NamePathMapper;
import org.apache.jackrabbit.oak.spi.security.SecurityConfiguration;
import org.apache.jackrabbit.oak.spi.security.authorization.AuthorizationConfiguration;
import org.apache.jackrabbit.oak.spi.security.authorization.permission.OpenPermissionProvider;
import org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionProvider;
import org.apache.jackrabbit.oak.spi.security.authorization.restriction.RestrictionProvider;
import org.jetbrains.annotations.NotNull;

import javax.jcr.security.AccessControlManager;
import java.security.Principal;
import java.util.Set;

/**
 * Configuration defining the authorization mechanism Oak has to apply when a user walks across the
 * content's tree of a repository within the context of Silverpeas. The goal is to delegate the
 * authorization to Silverpeas without using JAAS, for Silverpeas security isn't built upon JAAS.
 * @author mmoquillon
 */
public class SilverpeasAuthorizationConfiguration extends SecurityConfiguration.Default
    implements AuthorizationConfiguration {

  @NotNull
  @Override
  public AccessControlManager getAccessControlManager(@NotNull Root root,
      @NotNull NamePathMapper namePathMapper) {
    throw new UnsupportedOperationException();
  }

  @NotNull
  @Override
  public RestrictionProvider getRestrictionProvider() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  @Override
  public PermissionProvider getPermissionProvider(@NotNull Root root, @NotNull String workspaceName,
      @NotNull Set<Principal> principals) {
    return OpenPermissionProvider.getInstance();
  }
}
