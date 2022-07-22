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

package org.silverpeas.jcr.impl.oak.configuration;

import org.apache.jackrabbit.oak.segment.CachingSegmentReader;
import org.apache.jackrabbit.oak.segment.SegmentCache;
import org.apache.jackrabbit.oak.segment.compaction.SegmentGCOptions;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;

import java.util.Properties;

/**
 * <p>
 * Configuration parameters of a segment storage.
 * </p>
 * <p>
 * Oak Segment Tar is an Oak storage backend that stores content as various types of records within
 * larger segments. Segments themselves are collected within tar files along with further auxiliary
 * information. A journal is used to track the latest state of the repository. It is based on the
 * following key principles:
 * </p>
 * <ul>
 * <li>Immutability. Segments are immutable, which makes is easy to cache frequently accessed
 * segments. This also makes it less likely for programming or system errors to cause
 * repository inconsistencies, and simplifies features like backups or master-slave clustering.</li>
 * <li>Compactness. The formatting of records is optimized for size to reduce IO costs and to fit
 * as much content in caches as possible.</li>
 * <li>Locality. Segments are written so that related records, like a node and its immediate
 * children, usually end up stored in the same segment. This makes tree traversals very fast
 * and avoids most cache misses for typical clients that access more than one related node
 * per session.</li>
 * </ul>
 * <p>
 * The content tree and all its revisions are stored in a collection of immutable records within
 * segments. Each segment is identified by a UUID and typically contains a continuous subset of
 * the content tree, for example a node with its properties and closest child nodes. Some
 * segments might also be used to store commonly occurring property values or other shared data.
 * Segments can be up to 256KiB in size. See Segments and records for a detailed description of
 * the segments and records.
 * <p>
 * Segments are collectively stored in tar files and check-summed to ensure their integrity. Tar
 * files also contain an index of the tar segments, the graph of segment references of all
 * segments it contains and an index of all external binaries referenced from the segments in the
 * tar file. See Structure of TAR files for details.
 * <p>
 * The journal is a special, atomically updated file that records the state of the repository as
 * a sequence of references to successive root node records. For crash resiliency the journal is
 * always only updated with a new reference once the referenced record has been flushed to disk.
 * The most recent root node reference stored in the journal is used as the starting point for
 * garbage collection. All content currently visible to clients must be accessible through that
 * reference.
 * @author mmoquillon
 */
public class SegmentNodeStoreConfiguration extends NodeStoreConfiguration {

  SegmentNodeStoreConfiguration(final Properties props) {
    super(props);
  }

  /**
   * Gets the absolute path on the file system where repository data will be stored. The Segment
   * Store persists its data in a subdirectory of this home directory named
   * <code>segmentstore</code> and the backups of the Node Store will be stored in a subdirectory
   * of this homde directory named <code>segmentstore-backup</code>.
   * @return the absolute path of the JCR in the filesystem.
   */
  public String getRepositoryHome() {
    return getString("segment.repository.home");
  }

  /**
   * Gets the maximum size of TAR files on disk in MB. The data are stored as various types of
   * records within larger segments. Segments themselves are collected within tar files along with
   * further auxiliary information.
   * @return the maximum size of the tar files in MB.
   */
  public int getTarMaxSize() {
    return getInteger("segment.tar.size", FileStoreBuilder.DEFAULT_MAX_FILE_SIZE);
  }

  /**
   * Gets the maximum size of the segment cache in MB. The segment cache keeps a subset of the
   * segments in memory and avoids performing I/O operations when those segments are used.
   * @return the maximum size of the segment cache in MB
   */
  public int getSegmentCacheSize() {
    return getInteger("segment.segmentCache.size", SegmentCache.DEFAULT_SEGMENT_CACHE_MB);
  }

  /**
   * Gets the maximum size of the strings cache in MB. The string cache keeps a subset of the string
   * records in memory and avoids performing I/O operations when those strings are used.
   * @return the maximum size of the strings cache in MB
   */
  public int getStringCacheSize() {
    return getInteger("segment.stringCache.size", CachingSegmentReader.DEFAULT_STRING_CACHE_MB);
  }

  /**
   * Gets the maximum size of the template cache in MB. The template cache keeps a subset of the
   * template records in memory and avoids performing I/O operations when those templates are used.
   * @return the maximum size of the template cache in MB
   */
  public int getTemplateCacheSize() {
    return getInteger("segment.templateCache.size", CachingSegmentReader.DEFAULT_TEMPLATE_CACHE_MB);
  }

  /**
   * Gets the maximum size of the string deduplication cache in number of items. The string
   * deduplication cache tracks string records across different GC generations. It avoids
   * duplicating a string record to the current GC generation if it was already duplicated in the
   * past.
   * @return the maximum size of the string deduplication cache in number of items
   */
  public int getStringDeduplicationCacheSize() {
    return getInteger("segment.stringDeduplicationCache.size", 15000);
  }

  /**
   * Gets the maximum size of the template deduplication cache in number of items. The template
   * deduplication cache tracks template records across different GC generations. It avoids
   * duplicating a template record to the current GC generation if it was already duplicated in the
   * past.
   * @return the maximum size of the template deduplication cache in number of items
   */
  public int getTemplateDeduplicationCacheSize() {
    return getInteger("segment.templateDeduplicationCache.size", 3000);
  }

  /**
   * Gets the maximum size of the node deduplication cache in number of items. The node
   * deduplication cache tracks node records across different GC generations. It avoids duplicating
   * a node record to the current generation if it was already duplicated in the past.
   * @return the maximum size of the node deduplication cache in number of items
   */
  public int getNodeDeduplicationCacheSize() {
    return getInteger("segment.nodeDeduplicationCache.size", 1048576);
  }

  /**
   * Determines if online compaction should be executed. If this property is true, both the
   * estimation and compaction phases of the online compaction process are not executed.
   * @return true if online compaction should be executed. False otherwise.
   */
  public boolean isPauseCompaction() {
    return getBoolean("segment.compaction.pause", SegmentGCOptions.PAUSE_DEFAULT);
  }

  /**
   * Gets the number of commit attempts the online compaction process should try before giving up.
   * This property determines how many times the online compaction process should try to merge the
   * compacted repository state with the user-generated state produced by commits executed
   * concurrently during compaction.
   * @return the number of commit attempts of compaction.
   */
  public int getCompactionRetryCount() {
    return getInteger("segment.compaction.retryCount", SegmentGCOptions.RETRY_COUNT_DEFAULT);
  }

  /**
   * Gets the amount of time the online compaction process is allowed to exclusively lock the store,
   * in seconds. If this property is set to a positive value, if the compaction process fails to
   * commit the compacted state concurrently with other commits, it will acquire an exclusive lock
   * on the Node Store. The exclusive lock prevents other commits for completion, giving the
   * compaction process a possibility to commit the compacted state. This property determines how
   * long the compaction process is allowed to use the Node Store in exclusive mode. If this
   * property is set to zero or to a negative value, the compaction process will not acquire an
   * exclusive lock on the Node Store and will just give up if too many concurrent commits are
   * detected.
   * @return the amount of time in seconds the compaction is allowed to lock the store.
   */
  public int getCompactionForceTimeout() {
    return getInteger("segment.compaction.forceTimeout", SegmentGCOptions.FORCE_TIMEOUT_DEFAULT);
  }

  /**
   * Gets the increase in size of the Node Store (in bytes) since the last successful compaction
   * that will trigger another execution of the compaction phase.
   * @return the delta in size of the node store (in bytes) between two compactions.
   */
  public long getCompactionSizeDeltaEstimation() {
    return getLong("segment.compaction.sizeDeltaEstimation",
        SegmentGCOptions.SIZE_DELTA_ESTIMATION_DEFAULT);
  }

  /**
   * Disables the estimation phase of the online compaction process. If this property is set to
   * true, the estimation phase of the compaction process will never run, and compaction will always
   * be triggered for any amount of garbage in the Node Store.
   * @return true if the estimation phase for compaction isn't performed. False otherwise.
   */
  public boolean isCompactionDisableEstimation() {
    return getBoolean("segment.compaction.disableEstimation",
        SegmentGCOptions.DISABLE_ESTIMATION_DEFAULT);
  }

  /**
   * Gets the percentage of heap memory that should always be free while compaction runs. If the
   * available heap memory falls below the specified percentage, compaction will not be started or
   * it will be aborted if it is already running.
   * @return the threshold of heap memory in percentage to keep free for compaction.
   */
  public int getCompactionMemoryThreshold() {
    return getInteger("segment.compaction.memoryThreshold",
        SegmentGCOptions.MEMORY_THRESHOLD_DEFAULT);
  }

  /**
   * Enables compaction progress logging at each set of compacted nodes. A value of -1 disables the
   * log.
   * @return the number of compacted nodes for logging the compaction progress. -1 means the
   * progress logging is disabled.
   */
  public long getCompactionProgressLog() {
    return getLong("segment.compaction.progressLog", SegmentGCOptions.GC_PROGRESS_LOG_DEFAULT);
  }
}
