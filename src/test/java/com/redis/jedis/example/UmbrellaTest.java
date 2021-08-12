package com.redis.jedis.example;

import com.redis.jedis.DefaultJedisClientConfig;
import com.redis.jedis.HostAndPort;
import com.redis.jedis.JedisClientConfig;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.Protocol;
import com.redis.jedis.providers.SimpleJedisConnectionProvider;
import com.redis.jedis.umbrella.JedisUmbrella;
import com.redis.jedis.util.HostAndPortUtil;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class UmbrellaTest {

  static final HostAndPort DEFAULT_HOST_AND_PORT = HostAndPortUtil.getRedisServers().get(0);
  static final JedisClientConfig DEFAULT_CLIENT_CONFIG = DefaultJedisClientConfig.builder().password("foobared").build();

  @Before
  public void setUp() {
    try (JedisConnection connection = new JedisConnection(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG)) {
      connection.sendCommand(Protocol.Command.FLUSHALL);
    }
  }

  @Test
  public void umbrella() {
    SimpleJedisConnectionProvider simple = new SimpleJedisConnectionProvider(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    JedisUmbrella all = new JedisUmbrella(simple);

    assertEquals("OK", all.jedis().set("string", "value"));
    assertEquals("value", all.jedis().get("string"));
    assertEquals(1, all.jedis().del("string"));
    assertNull(all.jedis().get("string"));

    assertEquals(1, all.hash().hset("hash", Collections.singletonMap("foo", "bar")));
    assertEquals(Collections.singletonMap("foo", "bar"), all.hash().hgetAll("hash"));
    assertEquals(1, all.jedis().del("hash"));
    assertEquals(Collections.<String, String>emptyMap(), all.hash().hgetAll("hash"));

    assertEquals(1, all.set().sadd("set", Collections.singleton("member")));
    assertEquals(Collections.singleton("member"), all.set().smembers("set"));
    assertEquals(1, all.jedis().del("set"));
    assertEquals(Collections.<String>emptySet(), all.set().smembers("set"));

    simple.close();
  }
}
