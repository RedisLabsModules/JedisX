package com.redislabs.jedis.example;

import com.redislabs.jedis.DefaultJedisClientConfig;
import com.redislabs.jedis.HostAndPort;
import com.redislabs.jedis.Jedis;
import com.redislabs.jedis.JedisClientConfig;
import com.redislabs.jedis.JedisPreparedConnection;
import com.redislabs.jedis.hash.RedisHash;
import com.redislabs.jedis.pool.JedisConnectionPool;
import com.redislabs.jedis.providers.ManagedJedisConnectionProvider;
import com.redislabs.jedis.providers.PooledJedisConnectionProvider;
import com.redislabs.jedis.util.HostAndPortUtil;
import com.redislabs.jedis.util.Pool;

import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;

public class Examples {

  private static final HostAndPort DEFAULT_HOST_AND_PORT = HostAndPortUtil.getRedisServers().get(0);
  private static final JedisClientConfig DEFAULT_CLIENT_CONFIG = DefaultJedisClientConfig.builder().password("foobared").build();

  @Test
  public void managed() {
    JedisPreparedConnection conn = new JedisPreparedConnection(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    ManagedJedisConnectionProvider managed = new ManagedJedisConnectionProvider();
    Jedis jedis = new Jedis(managed);
    try {
      jedis.get("foo");
    } catch (Exception ex) {
      System.out.println(ex);
    }
    managed.setConnection(conn);
    Assert.assertNull(jedis.get("foo"));
    conn.close();
  }

  @Test
  public void pooled() {
    Pool<JedisPreparedConnection> pool = new JedisConnectionPool(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    PooledJedisConnectionProvider pooled = new PooledJedisConnectionProvider(pool);
    Jedis jedis = new Jedis(pooled);
    try {
      jedis.get("foo");
    } catch (Exception ex) {
      System.out.println(ex);
    }
    Assert.assertNull(jedis.get("foo"));
    pool.close();
  }

  @Test
  public void extendedModule() {
    Pool<JedisPreparedConnection> pool = new JedisConnectionPool(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    PooledJedisConnectionProvider pooled = new PooledJedisConnectionProvider(pool);
    RedisHash hash = new RedisHash(pooled);
    hash.hset("hash", Collections.singletonMap("foo", "bar"));
    Assert.assertEquals(Collections.singletonMap("foo", "bar"), hash.hgetAll("hash"));
    hash.del("hash");
    Assert.assertEquals(Collections.<String, String>emptyMap(), hash.hgetAll("hash"));
    pool.close();
  }
}
