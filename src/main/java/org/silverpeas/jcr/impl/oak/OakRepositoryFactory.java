package org.silverpeas.jcr.impl.oak;

import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.spi.state.NodeStore;
import org.silverpeas.core.NotSupportedException;
import org.silverpeas.core.SilverpeasRuntimeException;
import org.silverpeas.core.util.StringUtil;
import org.silverpeas.core.util.logging.SilverLogger;
import org.silverpeas.jcr.SilverpeasRepositoryFactory;
import org.silverpeas.jcr.impl.RepositorySettings;
import org.silverpeas.jcr.impl.oak.configuration.OakRepositoryConfiguration;
import org.silverpeas.jcr.impl.oak.configuration.StorageType;
import org.silverpeas.jcr.impl.oak.factories.DocumentNodeStoreFactory;
import org.silverpeas.jcr.impl.oak.factories.MemoryNodeStoreFactory;
import org.silverpeas.jcr.impl.oak.factories.NodeStoreFactory;
import org.silverpeas.jcr.impl.oak.factories.SegmentNodeStoreFactory;
import org.silverpeas.jcr.impl.oak.security.SilverpeasSecurityProvider;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * A factory of a {@link Repository} implemented by Apache Jackrabbit Oak. The factory checks the
 * required parameters are available to take the control of the construction of the
 * {@link Repository} instance. The repository is created by using the Oak API and from the
 * configuration parameters that were loaded from a properties file in the JCR home directory. The
 * expected parameters are:
 * </p>
 * <ul>
 *   <li>{@link RepositorySettings#JCR_HOME}: the absolute path of the JCR home directory,</li>
 *   <li>{@link RepositorySettings#JCR_CONF}: the absolute path of the configuration file in
 *   which are specified the type of repository backend to use with Oak and its configuration
 *   parameters.</li>
 * </ul>
 */
public class OakRepositoryFactory implements SilverpeasRepositoryFactory {

  private final Map<StorageType, Supplier<NodeStoreFactory>>
      nodeStoreBuilders = Map.of(
      StorageType.MEMORY_NODE_STORE, MemoryNodeStoreFactory::new,
      StorageType.SEGMENT_NODE_STORE, SegmentNodeStoreFactory::new,
      StorageType.DOCUMENT_NODE_STORE, DocumentNodeStoreFactory::new,
      StorageType.COMPOSITE_NODE_STORE, () -> (s, c) -> {
        throw new NotSupportedException("The composite node storage isn't yet supported!");
      }
  );

  private final Function<StorageType, NodeStoreFactory> invalidNodeStore = t -> (s, c) -> {
    SilverLogger.getLogger(this).error("Invalid storage type: " + t);
    return null;
  };

  @Override
  public Repository getRepository(final Map parameters) throws RepositoryException {
    try {
      String jcrHomePath = (String) parameters.get(RepositorySettings.JCR_HOME);
      String confPath = (String) parameters.get(RepositorySettings.JCR_CONF);
      if (StringUtil.isNotDefined(jcrHomePath) || StringUtil.isNotDefined(confPath)) {
        return null;
      }

      OakRepositoryConfiguration conf = OakRepositoryConfiguration.load(confPath);
      NodeStore nodeStore = nodeStoreBuilders.getOrDefault(conf.getStorageType(),
              () -> invalidNodeStore.apply(conf.getStorageType()))
          .get().create(jcrHomePath, conf);
      if (nodeStore != null) {
        return new Jcr(new Oak(nodeStore)).with(new SilverpeasSecurityProvider())
            .createRepository();
      }
      return null;
    } catch (SilverpeasRuntimeException | IOException e) {
      throw new RepositoryException(e);
    }
  }

}
