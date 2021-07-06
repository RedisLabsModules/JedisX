package com.redislabs.jedis.commands;

public interface JedisCommands {

  /**
   * @param key
   * @param value
   * @return {@code OK}
   */
  String set(String key, String value);

  /**
   * @param key
   * @return value
   */
  String get(String key);
}
