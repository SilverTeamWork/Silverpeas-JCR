/*
 * Copyright (C) 2000 - 2023 Silverpeas
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

package org.silverpeas.jcr.impl.oak;

import org.apache.jackrabbit.core.fs.local.FileUtil;
import org.apache.jackrabbit.value.BinaryImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.test.extention.SystemProperty;
import org.silverpeas.core.test.extention.TestManagedBeans;
import org.silverpeas.jcr.JCRSession;
import org.silverpeas.jcr.RepositoryProvider;
import org.silverpeas.jcr.impl.RepositorySettings;
import org.silverpeas.jcr.security.SecurityTest;
import org.silverpeas.test.TestUser;

import javax.jcr.Node;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static javax.jcr.Property.*;
import static javax.jcr.nodetype.NodeType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.silverpeas.jcr.impl.oak.SegmentStoreTest.JCR_HOME;
import static org.silverpeas.jcr.impl.oak.SegmentStoreTest.OAK_CONFIG;

/**
 * Unit test on the initialization of the JCR repository backed by a segment storage.
 * @author mmoquillon
 */
@SystemProperty(key = RepositorySettings.JCR_HOME, value = JCR_HOME)
@SystemProperty(key = RepositorySettings.JCR_CONF, value = OAK_CONFIG)
@TestManagedBeans({RepositoryProvider.class})
public class SegmentStoreTest extends SecurityTest {

  public static final String JCR_HOME = "/tmp/jcr";
  public static final String OAK_CONFIG = "classpath:/silverpeas-oak-segment.properties";

  final User user = new TestUser.Builder()
      .setFirstName("Bart")
      .setLastName("Simpson")
      .setId("42")
      .setDomainId("0")
      .build();

  @BeforeAll
  public static void prepareFileStorage() throws IOException {
    Path jcrHome = Path.of(JCR_HOME);
    if (!Files.exists(jcrHome)) {
      Files.createDirectories(jcrHome);
    }
  }

  @AfterAll
  public static void purgeFileStorage() throws IOException {
    Path jcrHome = Path.of(JCR_HOME);
    if (Files.exists(jcrHome)) {
      FileUtil.delete(jcrHome.toFile());
    }
  }

  @Test
  @DisplayName("Create a node into the JCR backed by a segment storage")
  void createANode() {
    assertDoesNotThrow(() -> {
      try (JCRSession session = JCRSession.openSystemSession()) {
        InputStream data = getClass().getResourceAsStream("/silverpeas-oak-segment.properties");
        assertThat(data, notNullValue());

        Node root = session.getRootNode();
        Node expected = root.addNode("GED_1", NT_FOLDER)
            .addNode("files", NT_FOLDER)
            .addNode("myfile_1", NT_FILE)
            .addNode(JCR_CONTENT, NT_RESOURCE);
        expected.setProperty(JCR_MIMETYPE, "plain/text");
        expected.setProperty(JCR_LAST_MODIFIED_BY, user.getId());
        expected.setProperty(JCR_ENCODING, "ISO-8859-1");
        expected.setProperty(JCR_DATA, new BinaryImpl(data));
        session.save();

        Node actual = session.getNodeByIdentifier(expected.getIdentifier());
        assertThat(actual, notNullValue());
        assertThat(actual.getName(), is(expected.getName()));
        assertThat(actual.getPath(), is(expected.getPath()));
        assertThat(actual.getProperty(JCR_LAST_MODIFIED_BY).getString(),
            is(user.getId()));
        assertThat(actual.getProperty(JCR_MIMETYPE).getString(),
            is("plain/text"));
        assertThat(actual.getProperty(JCR_ENCODING).getString(),
            is("ISO-8859-1"));
      }
    });
  }

  @Test
  @DisplayName("Create a versioned node into the JCR backed by a segment storage")
  void createAVersionedNode() {
    assertDoesNotThrow(() -> {
      try (JCRSession session = JCRSession.openSystemSession()) {
        InputStream data = getClass().getResourceAsStream("/silverpeas-oak-segment.properties");
        assertThat(data, notNullValue());

        Node root = session.getRootNode();
        Node file = root.addNode("GED_2", NT_FOLDER)
            .addNode("files", NT_FOLDER)
            .addNode("myfile_1", NT_FILE);
        file.addMixin(MIX_VERSIONABLE);

        Node expected = file.addNode(JCR_CONTENT, NT_RESOURCE);
        expected.setProperty(JCR_MIMETYPE, "plain/text");
        expected.setProperty(JCR_LAST_MODIFIED_BY, user.getId());
        expected.setProperty(JCR_ENCODING, "ISO-8859-1");
        expected.setProperty(JCR_DATA, new BinaryImpl(data));

        session.save();

        Node actual = session.getNodeByIdentifier(expected.getIdentifier());
        assertThat(actual, notNullValue());
        assertThat(actual.getName(), is(expected.getName()));
        assertThat(actual.getPath(), is(expected.getPath()));
        assertThat(actual.getProperty(JCR_LAST_MODIFIED_BY).getString(),
            is(user.getId()));
        assertThat(actual.getProperty(JCR_MIMETYPE).getString(),
            is("plain/text"));
        assertThat(actual.getProperty(JCR_ENCODING).getString(),
            is("ISO-8859-1"));
        assertThat(Arrays.stream(actual.getParent().getMixinNodeTypes())
            .anyMatch(m -> m.isNodeType(MIX_VERSIONABLE)), is(true));

        VersionManager versionManager = session.getWorkspace().getVersionManager();
        VersionHistory history = versionManager.getVersionHistory(actual.getParent().getPath());
        assertThat(history, notNullValue());

        Version rootVersion = history.getRootVersion();
        assertThat(rootVersion, notNullValue());
        assertThat(rootVersion.getIdentifier(), notNullValue());

        Version baseVersion = versionManager.getBaseVersion(actual.getParent().getPath());
        assertThat(baseVersion, notNullValue());
        assertThat(baseVersion.getIdentifier(), notNullValue());

        assertThat(rootVersion.getIdentifier(), is(baseVersion.getIdentifier()));

        VersionIterator versionIterator = history.getAllVersions();
        // because versionIterator#getSize() depends on the implementation of the JCR, we cannot
        // be sure of his return value
        if (versionIterator.getSize() == -1L) {
          assertThat(versionIterator.hasNext(), is(true));
          Version version = versionIterator.nextVersion();
          assertThat(version, notNullValue());
          assertThat(version.getIdentifier(), is(rootVersion.getIdentifier()));
          assertThat(versionIterator.hasNext(), is(false));
        } else {
          assertThat(versionIterator.getSize(), is(1L));
        }
      }
    });
  }
}
