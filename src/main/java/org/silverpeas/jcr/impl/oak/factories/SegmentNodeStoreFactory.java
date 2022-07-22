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

import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.compaction.SegmentGCOptions;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.apache.jackrabbit.oak.spi.state.NodeStore;
import org.silverpeas.core.SilverpeasRuntimeException;
import org.silverpeas.core.util.StringUtil;
import org.silverpeas.jcr.impl.oak.configuration.OakRepositoryConfiguration;
import org.silverpeas.jcr.impl.oak.configuration.SegmentNodeStoreConfiguration;
import org.silverpeas.jcr.impl.oak.configuration.StorageType;

import java.io.File;
import java.io.IOException;

/**
 * Factory of a {@link org.apache.jackrabbit.oak.segment.SegmentNodeStore} instance. This is for
 * the segment storage in Oak.
 * @author mmoquillon
 */
public class SegmentNodeStoreFactory implements NodeStoreFactory {

  @Override
  public NodeStore create(final String jcrHomePath, final OakRepositoryConfiguration conf) {
    if (conf.getStorageType() != StorageType.SEGMENT_NODE_STORE) {
      return null;
    }

    SegmentNodeStoreConfiguration parameters = conf.getSegmentNodeStore();
    String repoHome =
        StringUtil.isDefined(parameters.getRepositoryHome()) ? parameters.getRepositoryHome() :
            jcrHomePath;
    FileStore fs;
    try {
      fs = FileStoreBuilder.fileStoreBuilder(new File(repoHome))
          .withMaxFileSize(parameters.getTarMaxSize())
          .withSegmentCacheSize(parameters.getSegmentCacheSize())
          .withStringCacheSize(parameters.getStringCacheSize())
          .withTemplateCacheSize(parameters.getTemplateCacheSize())
          .withStringDeduplicationCacheSize(parameters.getStringDeduplicationCacheSize())
          .withTemplateDeduplicationCacheSize(parameters.getTemplateDeduplicationCacheSize())
          .withNodeDeduplicationCacheSize(parameters.getNodeDeduplicationCacheSize())
          .withGCOptions(SegmentGCOptions.defaultGCOptions()
              .setPaused(parameters.isPauseCompaction())
              .setRetryCount(parameters.getCompactionRetryCount())
              .setForceTimeout(parameters.getCompactionForceTimeout())
              .setGcSizeDeltaEstimation(parameters.getCompactionSizeDeltaEstimation())
              .setEstimationDisabled(parameters.isCompactionDisableEstimation())
              .setMemoryThreshold(parameters.getCompactionMemoryThreshold())
              .setGCLogInterval(parameters.getCompactionProgressLog()))
          .build();
    } catch (InvalidFileStoreVersionException | IOException e) {
      throw new SilverpeasRuntimeException(e);
    }
    return SegmentNodeStoreBuilders.builder(fs).build();
  }
}
