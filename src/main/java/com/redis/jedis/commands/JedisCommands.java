package com.redis.jedis.commands;

public interface JedisCommands {

  long del(String key);

  String set(String key, String value);

  String get(String key);
}
