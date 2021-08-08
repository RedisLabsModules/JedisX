package com.redislabs.jedis.providers;

import com.redislabs.jedis.JedisSocketConnection;
import com.redislabs.jedis.commands.ProtocolCommand;

public class ManagedJedisConnectionProvider implements JedisConnectionProvider {

  private JedisSocketConnection conn;

  public final void setConnection(JedisSocketConnection conn) {
    this.conn = conn;
  }

  @Override
  public JedisSocketConnection getConnection(ProtocolCommand command, int slot) {
    return this.conn;
  }

  @Override
  public void returnConnection(int slot, JedisSocketConnection conn) {
  }
}
