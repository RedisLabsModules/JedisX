package com.redis.jedis.giant;

import com.redis.hash.HashCommandObjects;
import com.redis.jedis.commands.PipelineCommands;
import com.redis.hash.commands.HashPipelineCommands;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.Pipeline;
import com.redis.jedis.Response;
import com.redis.set.SetCommandObjects;
import com.redis.set.commands.SetPipelineCommands;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GiantPipeline extends Pipeline implements PipelineCommands, HashPipelineCommands, SetPipelineCommands {

  private final HashCommandObjects hashCommandObjects;
  private final SetCommandObjects setCommandObjects;

  public GiantPipeline(JedisConnection connection) {
    super(connection);
    this.hashCommandObjects = new HashCommandObjects();
    this.setCommandObjects = new SetCommandObjects();
  }

  @Override
  public Response<Long> hset(String key, Map<String, String> fieldValues) {
    return appendCommand(hashCommandObjects.hset(key, fieldValues));
  }

  @Override
  public Response<Map<String, String>> hgetAll(String key) {
    return appendCommand(hashCommandObjects.hgetAll(key));
  }

  @Override
  public Response<Long> sadd(String key, Collection<String> members) {
    return appendCommand(setCommandObjects.sadd(key, members));
  }

  @Override
  public Response<Set<String>> smembers(String key) {
    return appendCommand(setCommandObjects.smembers(key));
  }

}
