package com.redis.jedis.set.commands;

import com.redis.jedis.Response;
import java.util.Collection;
import java.util.Set;

public interface SetPipelineCommands {

  Response<Long> sadd(String key, Collection<String> members);

  Response<Set<String>> smembers(String key);
}
