package com.redis.jedis.providers;

import com.redis.jedis.HostAndPort;
import com.redis.jedis.JedisClientConfig;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.commands.ProtocolCommand;

public class SimpleJedisConnectionProvider implements JedisConnectionProvider, AutoCloseable {

  private final JedisConnection connection;

  public SimpleJedisConnectionProvider(HostAndPort hap, JedisClientConfig config) {
    this.connection = new JedisConnection(hap, config);
  }

  public SimpleJedisConnectionProvider(JedisConnection connection) {
    this.connection = connection;
  }

  @Override
  public void close() {
    connection.disconnect();
  }

  @Override
  public JedisConnection getConnection(ProtocolCommand command) {
    return connection;
  }

  @Override
  public JedisConnection getConnection(ProtocolCommand command, byte[] key) {
    return connection;
  }

  @Override
  public JedisConnection getConnection(ProtocolCommand command, String key) {
    return connection;
  }

}
