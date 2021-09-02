package com.redis.hash;

import com.redis.hash.commands.HashPipelineCommands;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.Pipeline;
import com.redis.jedis.Response;
import java.util.Map;

public class HashPipeline extends Pipeline implements HashPipelineCommands {

  private final HashCommandObjects hashCommandObjects;

  public HashPipeline(JedisConnection connection) {
    super(connection);
    this.hashCommandObjects = new HashCommandObjects();
  }

  @Override
  public Response<Long> hset(String key, Map<String, String> fieldValues) {
    return appendCommand(hashCommandObjects.hset(key, fieldValues));
  }

  @Override
  public Response<Map<String, String>> hgetAll(String key) {
    return appendCommand(hashCommandObjects.hgetAll(key));
  }

}
