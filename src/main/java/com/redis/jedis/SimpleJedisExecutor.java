package com.redis.jedis;

import com.redis.jedis.providers.JedisConnectionProvider;

public class SimpleJedisExecutor implements JedisCommandExecutor {

  private final JedisConnectionProvider provider;

  public SimpleJedisExecutor(JedisConnectionProvider provider) {
    this.provider = provider;
  }

  @Override
  public final <T> T executeCommand(CommandObject<T> commandObject) {
    try (JedisConnection connection = provider.getConnection(commandObject.getArguments())) {
      connection.sendCommand(commandObject.getArguments());
      return commandObject.getBuilder().build(connection.getOne());
    }
  }
}
