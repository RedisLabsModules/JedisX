package com.redislabs.jedis;

import com.redislabs.jedis.commands.JedisCommands;
import com.redislabs.jedis.providers.JedisConnectionProvider;

public class Jedis implements JedisCommands {

  protected final JedisConnectionProvider provider;

  public Jedis(JedisConnectionProvider provider) {
    this.provider = provider;
  }

  @Override
  public String set(String key, String value) {
    JedisSocketConnection conn = provider.getConnection(Protocol.Command.SET, -1);
    try {
      conn.sendCommand(Protocol.Command.SET, key, value);
      return conn.getStatusCodeReply();
    } finally {
      provider.returnConnection(-1, conn);
    }
  }

  @Override
  public String get(String key) {
    JedisSocketConnection conn = provider.getConnection(Protocol.Command.GET, -1);
    try {
      conn.sendCommand(Protocol.Command.GET, key);
      return conn.getBulkReply();
    } finally {
      provider.returnConnection(-1, conn);
    }
  }

  @Override
  public long del(String key) {
    JedisSocketConnection conn = provider.getConnection(Protocol.Command.DEL, -1);
    try {
      conn.sendCommand(Protocol.Command.DEL, key);
      return conn.getIntegerReply();
    } finally {
      provider.returnConnection(-1, conn);
    }
  }
}
