package com.redis.set;

import com.redis.jedis.Jedis;
import com.redis.jedis.providers.JedisClusterConnectionProvider;
import com.redis.jedis.providers.JedisConnectionProvider;
import com.redis.set.commands.SetCommands;
import java.util.Collection;
import java.util.Set;

public class RedisSet extends Jedis implements SetCommands {

  private final SetCommandObjects setCommandObjects;

  public RedisSet(JedisConnectionProvider provider) {
    super(provider);
    this.setCommandObjects = (provider instanceof JedisClusterConnectionProvider) ?
        new SetClusterCommandObjects() : new SetCommandObjects();
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
