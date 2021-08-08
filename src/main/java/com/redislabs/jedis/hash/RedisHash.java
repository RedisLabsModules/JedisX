package com.redislabs.jedis.hash;

import com.redislabs.jedis.Jedis;
import com.redislabs.jedis.JedisSocketConnection;
import com.redislabs.jedis.Protocol;
import com.redislabs.jedis.providers.JedisConnectionProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisHash extends Jedis implements HashCommands {

  public RedisHash(JedisConnectionProvider provider) {
    super(provider);
  }

  @Override
  public long hset(String key, Map<String, String> fieldValues) {
    JedisSocketConnection conn = provider.getConnection(Protocol.Command.HSET, -1);
    try {
      String[] args = new String[1 + fieldValues.size() * 2];
      int i = 0;
      args[i++] = key;
      for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
        args[i++] = entry.getKey();
        args[i++] = entry.getValue();
      }
      conn.sendCommand(Protocol.Command.HSET, args);
      return conn.getIntegerReply();
    } finally {
      provider.returnConnection(-1, conn);
    }
  }

  @Override
  public Map<String, String> hgetAll(String key) {
    JedisSocketConnection conn = provider.getConnection(Protocol.Command.HGETALL, -1);
    try {
      conn.sendCommand(Protocol.Command.HGETALL, key);
      List<String> list = conn.getMultiBulkReply();
      Map<String, String> map = new HashMap<>(list.size() / 2);
      for (int i = 0; i < list.size(); i += 2) {
        map.put(list.get(i), list.get(i + 1));
      }
      return map;
    } finally {
      provider.returnConnection(-1, conn);
    }
  }

}