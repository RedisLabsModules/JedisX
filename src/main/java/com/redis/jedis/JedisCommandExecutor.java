package com.redis.jedis;

public interface JedisCommandExecutor {

  <T> T executeCommand(CommandObject<T> commandObject);
}
