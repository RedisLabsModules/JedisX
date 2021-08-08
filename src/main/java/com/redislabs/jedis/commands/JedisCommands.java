package com.redislabs.jedis.commands;

public interface JedisCommands {

  String set(String key, String value);

  String get(String key);

  long del(String key);
}
