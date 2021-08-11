package com.redislabs.jedis.providers;

import com.redislabs.jedis.HostAndPort;
import com.redislabs.jedis.JedisClientConfig;
import com.redislabs.jedis.JedisConnection;
import com.redislabs.jedis.commands.ProtocolCommand;

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
    connection.close();
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
