package com.redis.jedis.cluster;

import com.redis.jedis.DefaultJedisClientConfig;
import com.redis.jedis.HostAndPort;
import com.redis.jedis.JedisClientConfig;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.exceptions.JedisClusterOperationException;
import com.redis.jedis.exceptions.JedisConnectionException;
import com.redis.jedis.exceptions.JedisException;
import com.redis.jedis.util.Pool;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JedisClusterConnectionHandler implements Closeable {

  protected final JedisClusterInfoCache cache;

  public JedisClusterConnectionHandler(Set<HostAndPort> nodes,
      GenericObjectPoolConfig<JedisConnection> poolConfig, int connectionTimeout, int soTimeout,
      String password) {
    this(nodes, poolConfig, connectionTimeout, soTimeout, password, null);
  }

  public JedisClusterConnectionHandler(Set<HostAndPort> nodes,
      GenericObjectPoolConfig<JedisConnection> poolConfig, int connectionTimeout, int soTimeout,
      String password, String clientName) {
    this(nodes, poolConfig, connectionTimeout, soTimeout, null, password, clientName);
  }

  public JedisClusterConnectionHandler(Set<HostAndPort> nodes,
      final GenericObjectPoolConfig<JedisConnection> poolConfig, int connectionTimeout, int soTimeout,
      String user, String password, String clientName) {
    this(nodes, poolConfig, connectionTimeout, soTimeout, 0, user, password, clientName);
  }

  public JedisClusterConnectionHandler(Set<HostAndPort> nodes,
      final GenericObjectPoolConfig<JedisConnection> poolConfig, int connectionTimeout, int soTimeout,
      int infiniteSoTimeout, String user, String password, String clientName) {
    this(nodes, poolConfig,
        DefaultJedisClientConfig.builder().connectionTimeoutMillis(connectionTimeout)
            .socketTimeoutMillis(soTimeout).blockingSocketTimeoutMillis(infiniteSoTimeout)
            .user(user).password(password).clientName(clientName).build());
  }

  public JedisClusterConnectionHandler(Set<HostAndPort> nodes,
      final GenericObjectPoolConfig<JedisConnection> poolConfig, final JedisClientConfig clientConfig) {
    this.cache = new JedisClusterInfoCache(poolConfig, clientConfig);
    initializeSlotsCache(nodes, clientConfig);
  }

  public JedisConnection getConnectionFromNode(HostAndPort node) {
    return cache.setupNodeIfNotExist(node).getResource();
  }

  public Map<String, Pool<JedisConnection>> getNodes() {
    return cache.getNodes();
  }

  private void initializeSlotsCache(Set<HostAndPort> startNodes, JedisClientConfig clientConfig) {
    ArrayList<HostAndPort> startNodeList = new ArrayList<>(startNodes);
    Collections.shuffle(startNodeList);

    for (HostAndPort hostAndPort : startNodeList) {
      try (JedisConnection jedis = new JedisConnection(hostAndPort, clientConfig)) {
        cache.discoverClusterNodesAndSlots(jedis);
        return;
      } catch (JedisConnectionException e) {
        // try next nodes
      }
    }
  }

  public void renewSlotCache() {
    cache.renewClusterSlots(null);
  }

  public void renewSlotCache(JedisConnection jedis) {
    cache.renewClusterSlots(jedis);
  }

  @Override
  public void close() {
    cache.reset();
  }

  public JedisConnection getConnection() {
    // In antirez's redis-rb-cluster implementation, getRandomConnection always
    // return valid connection (able to ping-pong) or exception if all
    // connections are invalid

    List<Pool<JedisConnection>> pools = cache.getShuffledNodesPool();

    JedisException suppressed = null;
    for (Pool<JedisConnection> pool : pools) {
      JedisConnection jedis = null;
      try {
        jedis = pool.getResource();
        if (jedis == null) {
          continue;
        }

        jedis.ping();
        return jedis;

      } catch (JedisException ex) {
        if (suppressed == null) { // remembering first suppressed exception
          suppressed = ex;
        }
        if (jedis != null) {
          jedis.close();
        }
      }
    }

    JedisClusterOperationException noReachableNode = new JedisClusterOperationException("No reachable node in cluster.");
    if (suppressed != null) {
      noReachableNode.addSuppressed(suppressed);
    }
    throw noReachableNode;
  }

  public JedisConnection getConnectionFromSlot(int slot) {
    Pool<JedisConnection> connectionPool = cache.getSlotPool(slot);
    if (connectionPool != null) {
      // It can't guaranteed to get valid connection because of node assignment
      return connectionPool.getResource();
    } else {
      // It's abnormal situation for cluster mode that we have just nothing for slot.
      // Try to rediscover state
      renewSlotCache();
      connectionPool = cache.getSlotPool(slot);
      if (connectionPool != null) {
        return connectionPool.getResource();
      } else {
        // no choice, fallback to new connection to random node
        return getConnection();
      }
    }
  }

  public HostAndPort getNodeInfoFromSlot(int slot) {
    return cache.getSlotNode(slot);
  }
}
