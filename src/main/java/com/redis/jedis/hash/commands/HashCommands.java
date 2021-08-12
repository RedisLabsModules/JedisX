package com.redis.jedis.hash.commands;

import java.util.Map;

public interface HashCommands {

  long hset(String key, Map<String, String> fieldValues);

  Map<String, String> hgetAll(String key);
}
