package com.redis.jedis.giant;

import com.redis.hash.HashCommandObjects;
import com.redis.jedis.commands.JedisCommands;
import com.redis.hash.commands.HashCommands;
import com.redis.jedis.Jedis;
import com.redis.jedis.providers.JedisConnectionProvider;
import com.redis.set.SetCommandObjects;
import com.redis.set.commands.SetCommands;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GiantJedis extends Jedis implements JedisCommands, HashCommands, SetCommands {

  private final HashCommandObjects hashCommandObjects;
  private final SetCommandObjects setCommandObjects;

  public GiantJedis(JedisConnectionProvider provider) {
    super(provider);
    this.hashCommandObjects = new HashCommandObjects();
    this.setCommandObjects = new SetCommandObjects();
  }

  @Override
  public long hset(String key, Map<String, String> fieldValues) {
    return executeCommand(hashCommandObjects.hset(key, fieldValues));
  }

  @Override
  public Map<String, String> hgetAll(String key) {
    return executeCommand(hashCommandObjects.hgetAll(key));
  }

  @Override
  public long sadd(String key, Collection<String> members) {
    return executeCommand(setCommandObjects.sadd(key, members));
  }

  @Override
  public Set<String> smembers(String key) {
    return executeCommand(setCommandObjects.smembers(key));
  }

}
