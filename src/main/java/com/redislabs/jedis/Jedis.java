package com.redislabs.jedis;

import com.redislabs.jedis.commands.JedisCommands;
import com.redislabs.jedis.providers.JedisConnectionProvider;

public class Jedis implements JedisCommands {

  protected final JedisConnectionProvider provider;

  public Jedis(JedisConnectionProvider provider) {
    this.provider = provider;
  }

  @Override
  public long del(String key) {
    try (JedisConnection conn = provider.getConnection(Protocol.Command.DEL, key)) {
      conn.sendCommand(Protocol.Command.DEL, key);
      return conn.getIntegerReply();
    }
  }

  @Override
  public String set(String key, String value) {
    try (JedisConnection conn = provider.getConnection(Protocol.Command.SET, key)) {
      conn.sendCommand(Protocol.Command.SET, key, value);
      return conn.getStatusCodeReply();
    }
  }

  @Override
  public String get(String key) {
    try (JedisConnection conn = provider.getConnection(Protocol.Command.GET, key)) {
      conn.sendCommand(Protocol.Command.GET, key);
      return conn.getBulkReply();
    }
  }
}
