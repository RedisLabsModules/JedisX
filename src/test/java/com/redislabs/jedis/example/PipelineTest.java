package com.redislabs.jedis.example;

import com.redislabs.jedis.JedisConnection;
import com.redislabs.jedis.Protocol;
import com.redislabs.jedis.Pipeline;
import com.redislabs.jedis.Response;
import com.redislabs.jedis.hash.HashPipeline;

import java.util.Collections;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static com.redislabs.jedis.example.ExamplesTest.DEFAULT_CLIENT_CONFIG;
import static com.redislabs.jedis.example.ExamplesTest.DEFAULT_HOST_AND_PORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PipelineTest {

  @Before
  public void setUp() {
    try (JedisConnection connection = new JedisConnection(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG)) {
      connection.sendCommand(Protocol.Command.FLUSHALL);
    }
  }

  @Test
  public void simplePipeline() {
    JedisConnection connection = new JedisConnection(ExamplesTest.DEFAULT_HOST_AND_PORT, ExamplesTest.DEFAULT_CLIENT_CONFIG);
    Pipeline pipe = new Pipeline(connection);
    Response<String> nul = pipe.get("foo");
    Response<String> set = pipe.set("foo", "bar");
    Response<String> get = pipe.get("foo");
    pipe.sync();
    assertNull(nul.get());
    assertEquals("OK", set.get());
    assertEquals("bar", get.get());
    connection.close();
  }

  @Test
  public void modulePipeline() {
    JedisConnection connection = new JedisConnection(ExamplesTest.DEFAULT_HOST_AND_PORT, ExamplesTest.DEFAULT_CLIENT_CONFIG);
    HashPipeline pipe = new HashPipeline(connection);
    Response<Long> hset = pipe.hset("key", Collections.singletonMap("foo", "bar"));
    Response<Map<String, String>> map = pipe.hgetAll("key");
    Response<Long> del = pipe.del("key");
    Response<Map<String, String>> empty = pipe.hgetAll("key");
    pipe.sync();
    assertEquals(Long.valueOf(1), hset.get());
    assertEquals(Collections.singletonMap("foo", "bar"), map.get());
    assertEquals(Long.valueOf(1), del.get());
    assertEquals(Collections.emptyMap(), empty.get());
    connection.close();
  }
}
