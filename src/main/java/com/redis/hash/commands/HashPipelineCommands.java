package com.redis.hash.commands;

import com.redis.jedis.Response;
import java.util.Map;

public interface HashPipelineCommands {

  Response<Long> hset(String key, Map<String, String> fieldValues);

  Response<Map<String, String>> hgetAll(String key);
}
