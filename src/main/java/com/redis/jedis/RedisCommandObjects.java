package com.redis.jedis;

import com.redis.jedis.commands.ProtocolCommand;

public class RedisCommandObjects {

  protected CommandArguments commandArguments(ProtocolCommand command) {
    return new CommandArguments(command);
  }

  public CommandObject<Long> del(String key) {
    return new CommandObject<>(commandArguments(Protocol.Command.DEL).addKeyObject(key), BuilderFactory.LONG);
  }

  public CommandObject<String> set(String key, String value) {
    return new CommandObject<>(commandArguments(Protocol.Command.SET).addKeyObject(key).addObject(value), BuilderFactory.STRING);
  }

  public CommandObject<String> get(String key) {
    return new CommandObject<>(commandArguments(Protocol.Command.GET).addKeyObject(key), BuilderFactory.STRING);
  }
}
