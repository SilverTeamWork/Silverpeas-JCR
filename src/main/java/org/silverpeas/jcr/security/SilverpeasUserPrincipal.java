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

import java.security.Principal;

/**
 * Principal representing a user in Silverpeas.
 * @author mmoquillon
 */
public class SilverpeasUserPrincipal implements Principal {

  private final User user;

  SilverpeasUserPrincipal(final User user) {
    this.user = user;
  }

  @Override
  public String getName() {
    return user.getDisplayedName();
  }

  /**
   * Gets the user behind this principal.
   * @return a user in Silverpeas.
   */
  public User getUser() {
    return this.user;
  }
}
