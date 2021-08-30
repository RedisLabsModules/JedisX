package com.redis.jedis;

import com.redis.jedis.commands.PipelineCommands;

public class ReliableTransaction extends ReliableTransactionBase implements PipelineCommands {

  public ReliableTransaction(JedisConnection connection) {
    super(connection);
  }

  @Override
  public Response<Long> del(String key) {
    return enqueResponse(BuilderFactory.LONG, Protocol.Command.DEL, key);
  }

  @Override
  public Response<String> get(String key) {
    return enqueResponse(BuilderFactory.STRING, Protocol.Command.GET, key);
  }

  @Override
  public Response<String> set(String key, String value) {
    return enqueResponse(BuilderFactory.STRING, Protocol.Command.SET, key, value);
  }

}
