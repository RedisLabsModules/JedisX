package com.redis.jedis;

public class RedisCommandObjects {

  public CommandObject<Long> del(String key) {
    return new CommandObject<>(CommandArguments.of(Protocol.Command.DEL, key), BuilderFactory.LONG);
  }

  public CommandObject<String> set(String key, String value) {
    return new CommandObject<>(CommandArguments.of(Protocol.Command.SET, key, value), BuilderFactory.STRING);
  }

  public CommandObject<String> get(String key) {
    return new CommandObject<>(CommandArguments.of(Protocol.Command.GET, key), BuilderFactory.STRING);
  }
}
