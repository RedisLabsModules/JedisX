package com.redis.set.commands;

import java.util.Collection;
import java.util.Set;

public interface SetCommands {

  long sadd(String key, Collection<String> members);

  Set<String> smembers(String key);
}
