package com.redislabs.jedis.cluster;

import com.redislabs.jedis.HostAndPort;
import com.redislabs.jedis.JedisClientConfig;
import com.redislabs.jedis.JedisConnection;
import com.redislabs.jedis.exceptions.JedisClusterOperationException;
import com.redislabs.jedis.exceptions.JedisException;
import com.redislabs.jedis.util.Pool;

import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JedisSlotBasedConnectionHandler extends JedisClusterConnectionHandler {

  public JedisSlotBasedConnectionHandler(Set<HostAndPort> nodes,
      final GenericObjectPoolConfig<JedisConnection> poolConfig, int timeout) {
    this(nodes, poolConfig, timeout, timeout);
  }

  public JedisSlotBasedConnectionHandler(Set<HostAndPort> nodes,
      final GenericObjectPoolConfig<JedisConnection> poolConfig, int connectionTimeout, int soTimeout) {
    this(nodes, poolConfig, connectionTimeout, soTimeout, null);
  }

  public JedisSlotBasedConnectionHandler(Set<HostAndPort> nodes,
      GenericObjectPoolConfig<JedisConnection> poolConfig, int connectionTimeout, int soTimeout,
      String password) {
    super(nodes, poolConfig, connectionTimeout, soTimeout, password);
  }

  public JedisSlotBasedConnectionHandler(Set<HostAndPort> nodes,
      GenericObjectPoolConfig<JedisConnection> poolConfig, int connectionTimeout, int soTimeout,
      String password, String clientName) {
    super(nodes, poolConfig, connectionTimeout, soTimeout, password, clientName);
  }

  public JedisSlotBasedConnectionHandler(Set<HostAndPort> nodes,
      GenericObjectPoolConfig<JedisConnection> poolConfig, int connectionTimeout, int soTimeout, String user,
      String password, String clientName) {
    super(nodes, poolConfig, connectionTimeout, soTimeout, user, password, clientName);
  }

  public JedisSlotBasedConnectionHandler(Set<HostAndPort> nodes,
      GenericObjectPoolConfig<JedisConnection> poolConfig, int connectionTimeout, int soTimeout,
      int infiniteSoTimeout, String user, String password, String clientName) {
    super(nodes, poolConfig, connectionTimeout, soTimeout, infiniteSoTimeout, user, password,
        clientName);
  }

  public JedisSlotBasedConnectionHandler(Set<HostAndPort> nodes,
      GenericObjectPoolConfig<JedisConnection> poolConfig, JedisClientConfig clientConfig) {
    super(nodes, poolConfig, clientConfig);
  }

  @Override
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

  @Override
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
}
