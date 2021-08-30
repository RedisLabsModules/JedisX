package com.redis.hash;

import com.redis.hash.commands.HashPipelineCommands;
import com.redis.jedis.BuilderFactory;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.Protocol;
import com.redis.jedis.Response;
import com.redis.jedis.Transaction;
import java.util.Map;

public class HashTransaction extends Transaction implements HashPipelineCommands {

  public HashTransaction(JedisConnection connection) {
    super(connection);
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

}
