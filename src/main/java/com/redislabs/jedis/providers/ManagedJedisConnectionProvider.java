package com.redislabs.jedis.providers;

import com.redislabs.jedis.JedisConnection;
import com.redislabs.jedis.commands.ProtocolCommand;

public class ManagedJedisConnectionProvider implements JedisConnectionProvider {

  private JedisConnection conn;

  public final void setConnection(JedisConnection conn) {
    this.conn = conn;
  }

  @Override
  public JedisConnection getConnection(ProtocolCommand command) {
    return conn;
  }

  @Override
  public JedisConnection getConnection(ProtocolCommand command, byte[] key) {
    return conn;
  }

  @Override
  public JedisConnection getConnection(ProtocolCommand command, String key) {
    return conn;
  }
}
