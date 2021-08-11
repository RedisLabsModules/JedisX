package com.redislabs.jedis.hash;

import com.redislabs.jedis.BuilderFactory;
import com.redislabs.jedis.JedisConnection;
import com.redislabs.jedis.Protocol;
import com.redislabs.jedis.Response;
import com.redislabs.jedis.Transaction;
import com.redislabs.jedis.hash.commands.HashPipelineCommands;
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
