package com.redis.jedis.commands;

import com.redis.jedis.Response;

public interface PipelineCommands {

  Response<Long> del(String key);

  Response<String> get(String key);

  Response<String> set(String key, String value);
}
