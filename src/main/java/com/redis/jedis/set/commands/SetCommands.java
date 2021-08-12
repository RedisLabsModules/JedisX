package com.redis.jedis.set.commands;

import java.util.Collection;
import java.util.Set;

public interface SetCommands {

  long sadd(String key, Collection<String> members);

  Set<String> smembers(String key);
}
