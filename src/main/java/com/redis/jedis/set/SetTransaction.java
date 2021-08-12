package com.redis.jedis.set;

import com.redis.jedis.BuilderFactory;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.Protocol;
import com.redis.jedis.Response;
import com.redis.jedis.Transaction;
import com.redis.jedis.set.commands.SetPipelineCommands;

import java.util.Collection;
import java.util.Set;

public class SetTransaction extends Transaction implements SetPipelineCommands {

  public SetTransaction(JedisConnection connection) {
    super(connection);
  }

  @Override
  public Response<Long> sadd(String key, Collection<String> members) {
    String[] args = new String[1 + members.size()];
    int i = 0;
    args[i++] = key;
    for (String member : members) {
      args[i++] = member;
    }
    connection.sendCommand(Protocol.Command.SADD, args);
    return enqueResponse(BuilderFactory.LONG);
  }

  @Override
  public Response<Set<String>> smembers(String key) {
    connection.sendCommand(Protocol.Command.SMEMBERS, key);
    return enqueResponse(BuilderFactory.STRING_SET);
  }

}
