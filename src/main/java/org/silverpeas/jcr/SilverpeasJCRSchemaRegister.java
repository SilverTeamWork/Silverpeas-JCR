/*
 * Copyright (C) 2000 - 2022 Silverpeas
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
 * "https://www.silverpeas.org/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.silverpeas.jcr;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.silverpeas.core.annotation.Service;
import org.silverpeas.core.initialization.Initialization;
import org.silverpeas.core.util.logging.SilverLogger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * An initialization service aiming to register the Silverpeas specific schema into the JCR to
 * be used by Silverpeas. The schema is registered only the first time the repository is spawned; it
 * is no more initialized if it already exists in the underlying JCR. The JCR schema for Silverpeas
 * is defined in the file silverpeas-jcr.cnd in the classpath of this library.
 * @author mmoquillon
 */
@Service
public class SilverpeasJCRSchemaRegister implements Initialization {

  private static final String SILVERPEAS_JCR_SCHEMA = "/silverpeas-jcr.cnd";

  @Override
  public void init() throws Exception {
    InputStream schema = getClass().getResourceAsStream(SILVERPEAS_JCR_SCHEMA);
    Objects.requireNonNull(schema, "No file " + SILVERPEAS_JCR_SCHEMA + " found in the classpath!");

    try (InputStreamReader reader = new InputStreamReader(schema, StandardCharsets.UTF_8);
         JCRSession session = JCRSession.openSystemSession()) {
      SilverLogger.getLogger(this).info("Silverpeas specific JCR schema registering...");
      CndImporter.registerNodeTypes(reader, session);
    }
  }
}
