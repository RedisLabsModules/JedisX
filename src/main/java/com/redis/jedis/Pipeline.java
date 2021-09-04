package com.redis.jedis;

import com.redis.jedis.commands.PipelineCommands;

public class Pipeline extends PipelineBase implements PipelineCommands {

  private final RedisCommandObjects commandObjects;

  public Pipeline(JedisConnection connection) {
    super(connection);
    this.commandObjects = new RedisCommandObjects();
  }

  @Override
  public Response<Long> del(String key) {
    return appendCommand(commandObjects.del(key));
  }

  @Override
  public Response<String> get(String key) {
    return appendCommand(commandObjects.get(key));
  }

  @Override
  public Response<String> set(String key, String value) {
    return appendCommand(commandObjects.set(key, value));
  }

}