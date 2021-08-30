package com.redis.jedis.giant;

import com.redis.jedis.*;
import com.redis.jedis.commands.PipelineCommands;
import com.redis.hash.commands.HashPipelineCommands;
import com.redis.set.commands.SetPipelineCommands;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GiantPipeline extends PipelineBase implements PipelineCommands, HashPipelineCommands, SetPipelineCommands {

  public GiantPipeline(JedisConnection connection) {
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

  @Override
  public Response<Long> hset(String key, Map<String, String> fieldValues) {
    String[] args = new String[1 + fieldValues.size() * 2];
    int i = 0;
    args[i++] = key;
    for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
      args[i++] = entry.getKey();
      args[i++] = entry.getValue();
    }
    connection.sendCommand(Protocol.Command.HSET, args);
    return enqueResponse(BuilderFactory.LONG);
  }

  @Override
  public Response<Map<String, String>> hgetAll(String key) {
    connection.sendCommand(Protocol.Command.HGETALL, key);
    return enqueResponse(BuilderFactory.STRING_MAP);
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
