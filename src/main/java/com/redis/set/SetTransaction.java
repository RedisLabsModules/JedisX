package com.redis.set;

import com.redis.jedis.JedisConnection;
import com.redis.jedis.Response;
import com.redis.jedis.Transaction;
import com.redis.set.commands.SetPipelineCommands;

import java.util.Collection;
import java.util.Set;

public class SetTransaction extends Transaction implements SetPipelineCommands {

  private final SetCommandObjects setCommandObjects;

  public SetTransaction(JedisConnection connection) {
    super(connection);
    this.setCommandObjects = new SetCommandObjects();
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
