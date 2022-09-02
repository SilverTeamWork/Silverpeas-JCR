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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.silverpeas.core.admin.user.model.SilverpeasRole;
import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.test.extention.EnableSilverTestEnv;
import org.silverpeas.core.test.extention.SystemProperty;
import org.silverpeas.core.test.extention.TestManagedBeans;
import org.silverpeas.core.test.extention.TestedBean;
import org.silverpeas.jcr.impl.RepositorySettings;
import org.silverpeas.jcr.security.SecurityTest;
import org.silverpeas.test.TestUser;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import java.util.Calendar;
import java.util.Date;

import static javax.jcr.nodetype.NodeType.MIX_SIMPLE_VERSIONABLE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.silverpeas.jcr.RepositoryProviderTest.JCR_HOME;
import static org.silverpeas.jcr.RepositoryProviderTest.OAK_CONFIG;
import static org.silverpeas.jcr.util.JCRConstants.*;

/**
 * Test the registering of the JCR schema for Silverpeas use.
 * @author mmoquillon
 */
@EnableSilverTestEnv
@SystemProperty(key = RepositorySettings.JCR_HOME, value = JCR_HOME)
@SystemProperty(key = RepositorySettings.JCR_CONF, value = OAK_CONFIG)
@TestManagedBeans({RepositoryProvider.class})
class SilverpeasJCRSchemaRegisterTest extends SecurityTest {

  @TestedBean
  SilverpeasJCRSchemaRegister schemaRegister;

  final User user = new TestUser.Builder()
      .setFirstName("Bart")
      .setLastName("Simpson")
      .setId("42")
      .setDomainId("0")
      .build();

  @Test
  @DisplayName("Loading the Silverpeas JCR Schema into the JCR should succeed")
  void loadTheSchemaIntoJCR() {
    assertDoesNotThrow(() -> schemaRegister.init());
  }

  @Test
  void createANodeAccordingToTheSchema() throws Exception {
    schemaRegister.init();
    String instanceId = "kmelia42";

    assertDoesNotThrow(() -> {
      try (JCRSession session = JCRSession.openSystemSession()) {
        Node root = session.getRootNode();
        Node componentInstance = root.addNode(instanceId, NT_FOLDER);
        Node nodeType = componentInstance.addNode("attachments", NT_FOLDER);
        Node simpleDoc = nodeType.addNode("simpledoc_42", SLV_SIMPLE_DOCUMENT);
        simpleDoc.setProperty(SLV_PROPERTY_FOREIGN_KEY, "12");
        simpleDoc.setProperty(SLV_PROPERTY_VERSIONED, false);
        simpleDoc.setProperty(SLV_PROPERTY_ORDER, 0);
        simpleDoc.setProperty(SLV_PROPERTY_OLD_ID, "666");
        simpleDoc.setProperty(SLV_PROPERTY_INSTANCEID, instanceId);
        simpleDoc.setProperty(SLV_PROPERTY_OWNER, user.getId());
        simpleDoc.setProperty(SLV_PROPERTY_COMMENT, "");
        simpleDoc.setProperty(SLV_PROPERTY_STATUS, "0");
        simpleDoc.setProperty(SLV_PROPERTY_ALERT_DATE, convertToJCRValue(session, new Date()));
        simpleDoc.setProperty(SLV_PROPERTY_EXPIRY_DATE, convertToJCRValue(session, new Date()));
        simpleDoc.setProperty(SLV_PROPERTY_RESERVATION_DATE,
            convertToJCRValue(session, new Date()));
        simpleDoc.setProperty(SLV_PROPERTY_CLONE, "");

        //downloadable mixin
        simpleDoc.addMixin(SLV_DOWNLOADABLE_MIXIN);
        simpleDoc.setProperty(SLV_PROPERTY_FORBIDDEN_DOWNLOAD_FOR_ROLES,
            SilverpeasRole.asString(SilverpeasRole.READER_ROLES));

        // viewable mixin
        simpleDoc.addMixin(SLV_VIEWABLE_MIXIN);
        simpleDoc.setProperty(SLV_PROPERTY_DISPLAYABLE_AS_CONTENT, true);

        // versionable mixin
        simpleDoc.addMixin(MIX_SIMPLE_VERSIONABLE);

        session.save();
      }
    });
  }

  private Value convertToJCRValue(final Session session, final Date date)
      throws RepositoryException {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return session.getValueFactory().createValue(calendar);
  }
}