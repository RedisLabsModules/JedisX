package com.redis.set;

import com.redis.jedis.BuilderFactory;
import com.redis.jedis.Jedis;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.Protocol;
import com.redis.jedis.providers.JedisConnectionProvider;
import com.redis.set.commands.SetCommands;

import java.util.Collection;
import java.util.Set;

public class RedisSet extends Jedis implements SetCommands {

  public RedisSet(JedisConnectionProvider provider) {
    super(provider);
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
