package com.redislabs.jedis.cluster;

import com.redislabs.jedis.DefaultJedisClientConfig;
import com.redislabs.jedis.HostAndPort;
import com.redislabs.jedis.JedisClientConfig;
import com.redislabs.jedis.JedisConnection;
import com.redislabs.jedis.exceptions.JedisConnectionException;
import com.redislabs.jedis.util.Pool;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public abstract class JedisClusterConnectionHandler implements Closeable {
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

  public abstract JedisConnection getConnection();

  public abstract JedisConnection getConnectionFromSlot(int slot);

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
}
