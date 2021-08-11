package com.redislabs.jedis.hash.commands;

import com.redislabs.jedis.Response;
import java.util.Map;

public interface HashPipelineCommands {

  Response<Long> hset(String key, Map<String, String> fieldValues);

  Response<Map<String, String>> hgetAll(String key);
}
