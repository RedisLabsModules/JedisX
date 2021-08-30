package com.redis.jedis.example;

import com.redis.jedis.providers.ManagedJedisConnectionProvider;
import com.redis.jedis.providers.SimpleJedisConnectionProvider;
import com.redis.jedis.providers.PooledJedisConnectionProvider;
import com.redis.jedis.DefaultJedisClientConfig;
import com.redis.jedis.HostAndPort;
import com.redis.jedis.Jedis;
import com.redis.jedis.JedisClientConfig;
import com.redis.jedis.JedisConnection;
import com.redis.hash.RedisHash;
import com.redis.jedis.JedisConnectionPool;
import com.redis.jedis.Protocol;
import com.redis.jedis.util.HostAndPortUtil;
import com.redis.jedis.util.Pool;

import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExamplesTest {

  static final HostAndPort DEFAULT_HOST_AND_PORT = HostAndPortUtil.getRedisServers().get(0);
  static final JedisClientConfig DEFAULT_CLIENT_CONFIG = DefaultJedisClientConfig.builder().password("foobared").build();

  @Before
  public void setUp() {
    JedisConnection connection = new JedisConnection(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    connection.executeCommand(Protocol.Command.FLUSHALL);
    connection.disconnect();
  }

  @Test
  public void simple() {
    SimpleJedisConnectionProvider simple = new SimpleJedisConnectionProvider(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    Jedis jedis = new Jedis(simple);
    Assert.assertNull(jedis.get("foo"));
    Assert.assertNull(jedis.get("foo"));
    simple.close();
  }

  @Test
  public void managed() {
    JedisConnection conn = new JedisConnection(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    ManagedJedisConnectionProvider managed = new ManagedJedisConnectionProvider();
    Jedis jedis = new Jedis(managed);
    try {
      jedis.get("foo");
      Assert.fail();
    } catch (NullPointerException npe) {
      // expected
    }
    managed.setConnection(conn);
    Assert.assertNull(jedis.get("foo"));
    Assert.assertNull(jedis.get("foo"));
    conn.close();
  }

  @Test
  public void pooled() {
    Pool<JedisConnection> pool = new JedisConnectionPool(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    PooledJedisConnectionProvider pooled = new PooledJedisConnectionProvider(pool);
    Jedis jedis = new Jedis(pooled);
    Assert.assertNull(jedis.get("foo"));
    Assert.assertNull(jedis.get("foo"));
    pool.close();
  }

  @Test
  public void extendedModule() {
    Pool<JedisConnection> pool = new JedisConnectionPool(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    PooledJedisConnectionProvider pooled = new PooledJedisConnectionProvider(pool);
    RedisHash hash = new RedisHash(pooled);
    hash.hset("hash", Collections.singletonMap("foo", "bar"));
    Assert.assertEquals(Collections.singletonMap("foo", "bar"), hash.hgetAll("hash"));
    hash.del("hash");
    Assert.assertEquals(Collections.<String, String>emptyMap(), hash.hgetAll("hash"));
    pool.close();
  }
}
