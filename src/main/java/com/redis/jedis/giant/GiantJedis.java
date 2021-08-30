package com.redis.jedis.giant;

import com.redis.jedis.*;
import com.redis.jedis.commands.JedisCommands;
import com.redis.hash.commands.HashCommands;
import com.redis.jedis.providers.JedisConnectionProvider;
import com.redis.set.commands.SetCommands;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GiantJedis extends Jedis implements JedisCommands, HashCommands, SetCommands {

  public GiantJedis(JedisConnectionProvider provider) {
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

  @Override
  public long sadd(String key, Collection<String> members) {
    try (JedisConnection conn = provider.getConnection(Protocol.Command.SADD, key)) {
      String[] args = new String[1 + members.size()];
      int i = 0;
      args[i++] = key;
      for (String member : members) {
        args[i++] = member;
      }
      conn.sendCommand(Protocol.Command.SADD, args);
      return conn.getIntegerReply();
    }
  }

  @Override
  public Set<String> smembers(String key) {
    try (JedisConnection conn = provider.getConnection(Protocol.Command.SMEMBERS, key)) {
      conn.sendCommand(Protocol.Command.SMEMBERS, key);
      return BuilderFactory.STRING_SET.build(conn.getOne());
    }
  }

}
