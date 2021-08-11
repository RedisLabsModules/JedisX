package com.redislabs.jedis.example;

import com.redislabs.jedis.DefaultJedisClientConfig;
import com.redislabs.jedis.HostAndPort;
import com.redislabs.jedis.Jedis;
import com.redislabs.jedis.JedisClientConfig;
import com.redislabs.jedis.JedisConnection;
import com.redislabs.jedis.hash.RedisHash;
import com.redislabs.jedis.JedisConnectionPool;
import com.redislabs.jedis.Protocol;
import com.redislabs.jedis.providers.*;
import com.redislabs.jedis.util.HostAndPortUtil;
import com.redislabs.jedis.util.Pool;

import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExamplesTest {

  static final HostAndPort DEFAULT_HOST_AND_PORT = HostAndPortUtil.getRedisServers().get(0);
  static final JedisClientConfig DEFAULT_CLIENT_CONFIG = DefaultJedisClientConfig.builder().password("foobared").build();

  @Before
  public void setUp() {
    try (JedisConnection connection = new JedisConnection(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG)) {
      connection.sendCommand(Protocol.Command.FLUSHALL);
    }
  }

  @Test
  public void simple() {
    SimpleJedisConnectionProvider simple = new SimpleJedisConnectionProvider(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    Jedis jedis = new Jedis(simple);
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
    conn.close();
  }

  @Test
  public void pooled() {
    Pool<JedisConnection> pool = new JedisConnectionPool(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    PooledJedisConnectionProvider pooled = new PooledJedisConnectionProvider(pool);
    Jedis jedis = new Jedis(pooled);
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
