package com.redislabs.jedis.providers;

import com.redislabs.jedis.HostAndPort;
import com.redislabs.jedis.JedisClientConfig;
import com.redislabs.jedis.JedisConnection;
import com.redislabs.jedis.cluster.JedisClusterConnectionHandler;
import com.redislabs.jedis.cluster.JedisSlotBasedConnectionHandler;
import com.redislabs.jedis.commands.ProtocolCommand;
import com.redislabs.jedis.util.JedisClusterCRC16;
import java.util.Set;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JedisClusterConnectionProvider implements JedisConnectionProvider, AutoCloseable {

  protected final JedisClusterConnectionHandler connectionHandler;

  public JedisClusterConnectionProvider(Set<HostAndPort> jedisClusterNode, JedisClientConfig clientConfig,
      GenericObjectPoolConfig<JedisConnection> poolConfig) {
    this.connectionHandler = new JedisSlotBasedConnectionHandler(jedisClusterNode, poolConfig, clientConfig);
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
