package com.redislabs.jedis;

import com.redislabs.jedis.commands.JedisCommands;
import java.io.IOException;

public class ManagedJedis implements JedisCommands, AutoCloseable {

  protected JedisSocketConnection sc;

  public ManagedJedis() {
  }

  public ManagedJedis(JedisSocketConnection connection) {
    this.sc = connection;
  }

  public void setJedisConnection(JedisSocketConnection connection) {
    this.sc = connection;
  }

  @Override
  public void close() {
  }

  @Override
  public String set(String key, String value) {
    sc.sendCommand(Protocol.Command.SET, key, value);
    return sc.getStatusCodeReply();
  }

  @Override
  public String get(String key) {
    sc.sendCommand(Protocol.Command.GET, key);
    return sc.getBulkReply();
  }

}
