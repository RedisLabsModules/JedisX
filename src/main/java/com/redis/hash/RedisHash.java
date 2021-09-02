package com.redis.hash;

import com.redis.jedis.providers.JedisConnectionProvider;
import com.redis.jedis.Jedis;
import com.redis.hash.commands.HashCommands;
import java.util.Map;

public class RedisHash extends Jedis implements HashCommands {

  private final HashCommandObjects hashCommandObjects;

  public RedisHash(JedisConnectionProvider provider) {
    super(provider);
    hashCommandObjects = new HashCommandObjects();
  }

  @Override
  public long hset(String key, Map<String, String> fieldValues) {
    return executeCommand(hashCommandObjects.hset(key, fieldValues));
  }

  @Override
  public Map<String, String> hgetAll(String key) {
    return executeCommand(hashCommandObjects.hgetAll(key));
  }

}
