package com.redis.jedis.providers;

import com.redis.jedis.ClusterCommandArguments;
import com.redis.jedis.CommandArguments;
import com.redis.jedis.HostAndPort;
import com.redis.jedis.JedisClientConfig;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.cluster.JedisClusterConnectionHandler;
import com.redis.jedis.cluster.util.JedisClusterCRC16;

import java.util.Set;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JedisClusterConnectionProvider implements JedisConnectionProvider, AutoCloseable {

  protected final JedisClusterConnectionHandler connectionHandler;

  public JedisClusterConnectionProvider(Set<HostAndPort> jedisClusterNode, JedisClientConfig clientConfig,
      GenericObjectPoolConfig<JedisConnection> poolConfig) {
    this.connectionHandler = new JedisClusterConnectionHandler(jedisClusterNode, poolConfig, clientConfig);
  }

  @Override
  public void close() {
    this.connectionHandler.close();
  }

  @Override
  public JedisConnection getConnection(CommandArguments args) {
    final int slot = ((ClusterCommandArguments) args).getCommandHashSlot();
    return slot >= 0 ? this.connectionHandler.getConnectionFromSlot(slot) : this.connectionHandler.getConnection();
  }

  public JedisConnection getConnection(HostAndPort nodeKey) {
    return this.connectionHandler.getConnectionFromNode(nodeKey);
  }

  public HostAndPort getNodeKey(String key) {
    return this.connectionHandler.getNodeInfoFromSlot(JedisClusterCRC16.getSlot(key));
  }
}
