package com.redis.jedis;

import com.redis.jedis.commands.PipelineCommands;

public class Pipeline extends PipelineBase implements PipelineCommands {

  public Pipeline(JedisConnection connection) {
    super(connection);
  }

  @Override
  public Response<Long> del(String key) {
    connection.sendCommand(Protocol.Command.DEL, key);
    return enqueResponse(BuilderFactory.LONG);
  }

  @Override
  public Response<String> get(String key) {
    connection.sendCommand(Protocol.Command.GET, key);
    return enqueResponse(BuilderFactory.STRING);
  }

  @Override
  public Response<String> set(String key, String value) {
    connection.sendCommand(Protocol.Command.SET, key, value);
    return enqueResponse(BuilderFactory.STRING);
  }

}
