package com.redislabs.jedis;

import com.redislabs.jedis.commands.RedisPipelineCommands;

public class RedisPipeline extends PipelineBase implements RedisPipelineCommands {

  public RedisPipeline(JedisConnection connection) {
    super(connection);
  }

  @Override
  public Response<Long> del(String key) {
    connection.sendCommand(Protocol.Command.DEL, key);
    return getResponse(BuilderFactory.LONG);
  }

  @Override
  public Response<String> get(String key) {
    connection.sendCommand(Protocol.Command.GET, key);
    return getResponse(BuilderFactory.STRING);
  }

  @Override
  public Response<String> set(String key, String value) {
    connection.sendCommand(Protocol.Command.SET, key, value);
    return getResponse(BuilderFactory.STRING);
  }

}
