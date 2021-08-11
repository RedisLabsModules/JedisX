package com.redislabs.jedis.hash;

import com.redislabs.jedis.BuilderFactory;
import com.redislabs.jedis.Jedis;
import com.redislabs.jedis.JedisConnection;
import com.redislabs.jedis.Protocol;
import com.redislabs.jedis.hash.commands.HashCommands;
import com.redislabs.jedis.providers.JedisConnectionProvider;
import java.util.Map;

public class RedisHash extends Jedis implements HashCommands {

  public RedisHash(JedisConnectionProvider provider) {
    super(provider);
  }

  @Override
  public long hset(String key, Map<String, String> fieldValues) {
    try (JedisConnection conn = provider.getConnection(Protocol.Command.HSET, key)) {
      String[] args = new String[1 + fieldValues.size() * 2];
      int i = 0;
      args[i++] = key;
      for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
        args[i++] = entry.getKey();
        args[i++] = entry.getValue();
      }
      conn.sendCommand(Protocol.Command.HSET, args);
      return conn.getIntegerReply();
    }
  }

  @Override
  public Map<String, String> hgetAll(String key) {
    try (JedisConnection conn = provider.getConnection(Protocol.Command.HGETALL, key)) {
      conn.sendCommand(Protocol.Command.HGETALL, key);
      return BuilderFactory.STRING_MAP.build(conn.getOne());
    }
  }

}
