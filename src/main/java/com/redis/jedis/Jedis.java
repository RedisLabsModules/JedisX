package com.redis.jedis;

import com.redis.jedis.commands.JedisCommands;
import com.redis.jedis.providers.JedisConnectionProvider;

public class Jedis implements JedisCommands {

  protected final JedisConnectionProvider provider;
  private final RedisCommandObjects commandObjects;

  public Jedis(JedisConnectionProvider provider) {
    this.provider = provider;
    this.commandObjects = new RedisCommandObjects();
  }

  protected final <T> T executeCommand(CommandObject<T> commandObject) {
    try (JedisConnection connection = provider.getConnection(commandObject.getArguments().getCommand())) { // TODO
      connection.sendCommand(commandObject.getArguments());
      return commandObject.getBuilder().build(connection.getOne());
    }
  }

  @Override
  public long del(String key) {
    return executeCommand(commandObjects.del(key));
  }

  @Override
  public String set(String key, String value) {
    return executeCommand(commandObjects.set(key, value));
  }

  @Override
  public String get(String key) {
    return executeCommand(commandObjects.get(key));
  }
}
