package com.redis.jedis.giant;

import com.redis.jedis.*;
import com.redis.jedis.commands.JedisCommands;
import com.redis.jedis.hash.commands.HashCommands;
import com.redis.jedis.providers.JedisConnectionProvider;
import com.redis.jedis.set.commands.SetCommands;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GiantJedis implements JedisCommands, HashCommands, SetCommands {

  protected final JedisConnectionProvider provider;

  public GiantJedis(JedisConnectionProvider provider) {
    this.provider = provider;
  }

  @Override
  public long del(String key) {
    try (JedisConnection conn = provider.getConnection(Protocol.Command.DEL, key)) {
      conn.sendCommand(Protocol.Command.DEL, key);
      return conn.getIntegerReply();
    }
  }

  @Override
  public String set(String key, String value) {
    try (JedisConnection conn = provider.getConnection(Protocol.Command.SET, key)) {
      conn.sendCommand(Protocol.Command.SET, key, value);
      return conn.getStatusCodeReply();
    }
  }

  @Override
  public String get(String key) {
    try (JedisConnection conn = provider.getConnection(Protocol.Command.GET, key)) {
      conn.sendCommand(Protocol.Command.GET, key);
      return conn.getBulkReply();
    }
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
