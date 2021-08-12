package com.redis.jedis.umbrella;

import com.redis.jedis.Jedis;
import com.redis.jedis.hash.RedisHash;
import com.redis.jedis.providers.JedisConnectionProvider;
import com.redis.jedis.set.RedisSet;

public class JedisUmbrella {

  private final Jedis jedis;
  private final RedisHash hash;
  private final RedisSet set;

  public JedisUmbrella(JedisConnectionProvider provider) {
    this.jedis = new Jedis(provider);
    this.hash = new RedisHash(provider);
    this.set = new RedisSet(provider);
  }

  public Jedis jedis() {
    return jedis;
  }

  public RedisHash hash() {
    return hash;
  }

  public RedisSet set() {
    return set;
  }

}
