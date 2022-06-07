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

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

/**
 * Provider of credentials for users in Silverpeas to authenticate themselves amongst the JCR
 * repository used by Silverpeas.
 * @author mmoquillon
 */
public final class JCRUserCredentialsProvider {

  /**
   * Identifier of the JCR system user in Silverpeas. It should be used to access without any
   * restrictions to the repository.
   */
  public static final String JCR_SYSTEM_ID = "jcr-system@domain0";

  /**
   * Gets the credentials of the JCR system user in Silverpeas.
   * @return the simple credentials corresponding to the JCR system user.
   */
  public static Credentials getJcrSystemCredentials() {
    return new SimpleCredentials(JCR_SYSTEM_ID, new char[0]);
  }
}
