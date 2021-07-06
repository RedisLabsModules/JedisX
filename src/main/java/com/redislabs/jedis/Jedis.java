package com.redislabs.jedis;

import com.redislabs.jedis.commands.JedisCommands;
import java.io.Closeable;
import java.io.IOException;

public class Jedis implements JedisCommands, Closeable {

  private final JedisSocketConnection sc;

  public Jedis(JedisSocketConnection connection) {
    sc = connection;
  }

  @Override
  public void close() throws IOException {
    sc.close();
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
