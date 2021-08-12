package com.redis.jedis.example;

import com.redis.jedis.DefaultJedisClientConfig;
import com.redis.jedis.HostAndPort;
import com.redis.jedis.JedisClientConfig;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.Protocol;
import com.redis.jedis.Response;
import com.redis.jedis.giant.GiantJedis;
import com.redis.jedis.giant.GiantPipeline;
import com.redis.jedis.giant.GiantTransaction;
import com.redis.jedis.providers.SimpleJedisConnectionProvider;
import com.redis.jedis.util.HostAndPortUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class GiantTest {

  static final HostAndPort DEFAULT_HOST_AND_PORT = HostAndPortUtil.getRedisServers().get(0);
  static final JedisClientConfig DEFAULT_CLIENT_CONFIG = DefaultJedisClientConfig.builder().password("foobared").build();

  @Before
  public void setUp() {
    try (JedisConnection connection = new JedisConnection(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG)) {
      connection.sendCommand(Protocol.Command.FLUSHALL);
    }
  }

  @Test
  public void giant() {
    SimpleJedisConnectionProvider simple = new SimpleJedisConnectionProvider(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    GiantJedis all = new GiantJedis(simple);

    assertEquals("OK", all.set("string", "value"));
    assertEquals("value", all.get("string"));
    assertEquals(1, all.del("string"));
    assertNull(all.get("string"));

    assertEquals(1, all.hset("hash", Collections.singletonMap("foo", "bar")));
    assertEquals(Collections.singletonMap("foo", "bar"), all.hgetAll("hash"));
    assertEquals(1, all.del("hash"));
    assertEquals(Collections.<String, String>emptyMap(), all.hgetAll("hash"));

    assertEquals(1, all.sadd("set", Collections.singleton("member")));
    assertEquals(Collections.singleton("member"), all.smembers("set"));
    assertEquals(1, all.del("set"));
    assertEquals(Collections.<String>emptySet(), all.smembers("set"));

    simple.close();
  }

  @Test
  public void giantPipeline() {
    JedisConnection connection = new JedisConnection(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    GiantPipeline pipe = new GiantPipeline(connection);

    pipe.set("string", "value");
    pipe.hset("hash", Collections.singletonMap("foo", "bar"));
    pipe.sadd("set", Collections.singleton("member"));

    Response<?> str1 = pipe.get("string");
    Response<?> hash1 = pipe.hgetAll("hash");
    Response<?> set1 = pipe.smembers("set");

    pipe.del("string");
    pipe.del("hash");
    pipe.del("set");

    Response<?> str2 = pipe.get("string");
    Response<?> hash2 = pipe.hgetAll("hash");
    Response<?> set2 = pipe.smembers("set");

    pipe.sync();
    connection.close();

    assertEquals("value", str1.get());
    assertEquals(Collections.singletonMap("foo", "bar"), (Map) hash1.get());
    assertEquals(Collections.singleton("member"), (Set) set1.get());

    assertNull(str2.get());
    assertEquals(Collections.<String, String>emptyMap(), hash2.get());
    assertEquals(Collections.<String>emptySet(), set2.get());
  }

  @Test
  public void giantTransaction() {
    JedisConnection connection = new JedisConnection(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    GiantTransaction tran = new GiantTransaction(connection);

    tran.set("string", "value");
    tran.hset("hash", Collections.singletonMap("foo", "bar"));
    tran.sadd("set", Collections.singleton("member"));

    Response<?> str1 = tran.get("string");
    Response<?> hash1 = tran.hgetAll("hash");
    Response<?> set1 = tran.smembers("set");

    tran.del("string");
    tran.del("hash");
    tran.del("set");

    Response<?> str2 = tran.get("string");
    Response<?> hash2 = tran.hgetAll("hash");
    Response<?> set2 = tran.smembers("set");

    tran.exec();
    connection.close();

    assertEquals("value", str1.get());
    assertEquals(Collections.singletonMap("foo", "bar"), (Map) hash1.get());
    assertEquals(Collections.singleton("member"), (Set) set1.get());

    assertNull(str2.get());
    assertEquals(Collections.<String, String>emptyMap(), hash2.get());
    assertEquals(Collections.<String>emptySet(), set2.get());
  }
}
