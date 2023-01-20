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

package org.silverpeas.jcr.impl.oak.factories;

import org.apache.jackrabbit.oak.plugins.memory.MemoryNodeStore;
import org.apache.jackrabbit.oak.spi.state.NodeStore;
import org.silverpeas.jcr.impl.oak.configuration.OakRepositoryConfiguration;

/**
 * Factory of a {@link MemoryNodeStore} instance. This is for the memory storage in Oak for testing
 * purpose.
 * @author mmoquillon
 */
public class MemoryNodeStoreFactory implements NodeStoreFactory {

  @Override
  public NodeStore create(final String jcrHomePath, final OakRepositoryConfiguration conf) {
    return new MemoryNodeStore();
  }

  @Override
  public void dispose(final NodeStore store) {
    // nothing to do here
  }
}
