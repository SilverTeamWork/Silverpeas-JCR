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

/**
 * Provides the classes required by the implementation of the JCR when a user in Silverpeas is
 * accessing the repository in order to delegate both the authentication and the authorization to
 * Silverpeas itself. Actually, the expectation of the JCR is the users and the groups of users
 * should be managed within the JCR repository in order to perform automatically and transparently
 * both the authentication and the authorization when accessing the content of the repository. But
 * the users and the groups of users are managed by Silverpeas itself, and they don't need to be
 * synchronized with the repository to both avoid double security checkups and to keep the accesses
 * strongly controlled by Silverpeas.
 * @author mmoquillon
 */
package org.silverpeas.jcr.security;