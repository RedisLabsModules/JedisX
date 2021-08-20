package com.redis.jedis.providers;

import com.redis.jedis.HostAndPort;
import com.redis.jedis.JedisClientConfig;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.cluster.JedisClusterConnectionHandler;
import com.redis.jedis.commands.ProtocolCommand;
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
  public JedisConnection getConnection(ProtocolCommand command) {
    return this.connectionHandler.getConnection();
  }

  public JedisConnection getConnection(ProtocolCommand command, int slot) {
    return this.connectionHandler.getConnectionFromSlot(slot);
  }

  @Override
  public JedisConnection getConnection(ProtocolCommand command, byte[] key) {
    return getConnection(command, JedisClusterCRC16.getSlot(key));
  }

  @Override
  public JedisConnection getConnection(ProtocolCommand command, String key) {
    return getConnection(command, JedisClusterCRC16.getSlot(key));
  }
}
