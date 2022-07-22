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

import java.util.List;
import java.util.Properties;

/**
 * <p>
 * Configuration parameters of a document storage.
 * </p>
 * <p>
 * The Oak document storage is a database in which both node data and binaries are stored. Because
 * the database can be of different types, the DocumentNodeStore supports a number of backends, with
 * a storage abstraction called DocumentStore:
 * </p>
 * <ul>
 *   <li>MongoDocumentStore: stores documents in a MongoDB.</li>
 *   <li>RDBDocumentStore: stores documents in a relational data base.</li>
 *   <li>MemoryDocumentStore: keeps documents in memory. This implementation should only be used
 *   for testing purposes and it isn't addressed here.</li>
 * </ul>
 * <h3>The MongoDocumentStore</h1>
 * <p>
 *  The MongoDocumentStore use MongoDB to persist nodes as documents. For production deployments
 *  use a replica-set with at least three mongod instances and a majority write concern. Fewer
 *  than three instances (e.g. two instances and an arbiter) may lead to data loss when the
 *  primary fails.
 * <p>
 * When using MongoDB 3.4 or newer, set the maxStalenessSeconds option in the MongoDB URI to 90.
 * This is an additional safeguard and will prevent reads from a secondary that is too far behind.
 * <p>
 * Initializing a DocumentNodeStore on MongoDB with default values will also use MongoDB to store
 * blobs. While this is convenient for development and tests, the use of MongoDB as a blob store
 * in production is not recommended. MongoDB replicates all changes through a single op-log.
 * Large blobs can lead to a significantly reduced op-log window and cause delay in replicating
 * other changes between the replica-set members. See available blob stores alternatives for
 * production use.
 * <h3>The RDBDocumentStore</h3>
 * <p>
 * The RDBDocumentStore uses relational databases to persist nodes as documents, mainly emulating
 * the native capabilities of MongoDocumentStore. H2DB, PostgreSQL, Microsoft SQL Server, and
 * Oracle are supported.
 * <p>
 * relies on JDBC, and thus, in general, can not create database instances (that said, certain
 * DBs such as Apache Derby or H2DB can create the database automatically when it's not there yet
 * - consult the DB documentation in general and the JDBC URL syntax specifically).
 * <p>
 * So in general, the administrator will have to take care of creating the database. There are
 * only a few requirements for the database, but these are critical for the correct operation:
 * </p>
 * <ul>
 * <li>character fields must be able to store any Unicode code point - UTF-8 encoding is
 * recommended</li>
 * <li>the collation for character fields needs to sort by Unicode code points</li>
 * <li>BLOBs need to support sizes of ~16MB</li>
 * </ul>
 * @author mmoquillon
 */
public class DocumentNodeStoreConfiguration extends NodeStoreConfiguration {

  /**
   * Specifies the MongoURI required to connect to Mongo Database.
   */
  public String getUri() {
    return getString("document.uri", "mongodb://localhost:27017");
  }

  /**
   * Name of the database in Mongo.
   */
  public String getDBName() {
    return getString("document.db", "oak");
  }

  /**
   * Enables socket keep-alive for MongoDB connections.
   */
  public boolean getSocketKeepAlive() {
    return getBoolean("document.socketKeepAlive", true);
  }

  /**
   * Cache size in MB. This is distributed among various caches used in DocumentNodeStore.
   */
  public int getCacheSize() {
    return getInteger("document.cache", 256);
  }

  /**
   * Determines the duration in seconds beyond which it can be safely assumed that state on
   * secondary would be consistent with primary, and it's safe to read from them. (See OAK-1645). By
   * default 6 hours.
   */
  public int getMaxReplicationLag() {
    return getInteger("document.maxReplicationLagInSecs", 21600);
  }

  /**
   * Oak uses MVCC model to store the data. So each update to a node results in new version getting
   * created. This duration controls how much old revision data should be kept. For example if a
   * node is deleted at time T1 then its content would only be marked deleted at revision for T1 but
   * its content would not be removed. Only when a Revision GC is run then its content would be
   * removed and that too only after (currentTime -T1 > versionGcMaxAgeInSecs).
   */
  public int getVersionGCMaxAge() {
    return getInteger("document.versionGCMaxAgeInSecs", 86400);
  }

  /**
   * Journal entries older than journalGCMaxAge can be removed by the journal garbage collector. The
   * maximum age is specified in milliseconds. By default, 24 hours.
   */
  public long getJournalGCMaxAge() {
    return getLong("document.journalGCMaxAge", 86400000L);
  }

  /**
   * DocumentNodeStore when running with Mongo will use MongoBlobStore by default unless a custom
   * BlobStore is configured. In such scenario the size of in memory cache in MB for the frequently
   * used blobs can be configured via blobCacheSize.
   */
  public int getBlobCacheSize() {
    return getInteger("document.blobCacheSize", 16);
  }

  /**
   * List of paths defining the subtrees that are cached.
   */
  public List<String> getSubtreesInPersistentCache() {
    return getList("document.persistentCacheIncludes", List.of("/"));
  }

  /**
   * Percentage of cache allocated for nodeCache.
   */
  public int getNodeCachePercentage() {
    return getInteger("document.nodeCachePercentage", 35);
  }

  /**
   * Percentage of cache allocated for prevDocCache.
   */
  public int getPrevDocCachePercentage() {
    return getInteger("document.nodeCachePercentage", 4);
  }

  /**
   * Percentage of cache allocated for childrenCache.
   */
  public int getChildrenCachePercentage() {
    return getInteger("document.childrenCachePercentage", 15);
  }

  /**
   * Percentage of cache allocated for diffCache.
   */
  public int getDiffCachePercentage() {
    return getInteger("document.diffCachePercentage", 30);
  }

  /**
   * The number of segments in the LIRS cache.
   */
  public int getCacheSegmentCount() {
    return getInteger("document.cacheSegmentCount", 16);
  }

  /**
   * The delay to move entries to the head of the queue in the LIRS cache.
   */
  public int getCacheStackMoveDistance() {
    return getInteger("document.cacheStackMoveDistance", 16);
  }

  /**
   * The number of updates kept in memory until changes are written to a branch in the
   * DocumentStore.
   */
  public int getUpdateLimit() {
    return getInteger("document.updateLimit", 100000);
  }

  /**
   * The lease check mode. STRICT is the default and will stop the DocumentNodeStore as soon as the
   * lease expires. LENIENT will give the background lease update a chance to renew the lease even
   * when the lease expired. This mode is only recommended for development, e.g. when debugging an
   * application and the lease may expire when the JVM is stopped at a breakpoint.
   */
  public String getLeaseCheckMode() {
    return getString("document.leaseCheckMode", "STRICT");
  }

  /**
   * "MONGO" for MongoDocumentStore, “RDB” for RDBDocumentStore. Latter will require a configured
   * Sling DataSource called oak.
   */
  public DocumentStoreType getDocumentStoreType() {
    return DocumentStoreType.valueOf(getString("document.storeType", "MONGO"));
  }

  DocumentNodeStoreConfiguration(Properties props) {
    super(props);
  }

  /**
   * The type of document-based datasource.
   */
  public enum DocumentStoreType {
    /**
     * The datasource is MongoDB.
     */
    MONGO,
    /**
     * The datasource is a relational database.
     */
    RDB;

    @Override
    public String toString() {
      String name = name();
      return name.charAt(0) + name.substring(1).toLowerCase();
    }
  }

}
