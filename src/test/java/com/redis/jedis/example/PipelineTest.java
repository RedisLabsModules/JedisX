package com.redis.jedis.example;

import com.redis.jedis.JedisConnection;
import com.redis.jedis.Protocol;
import com.redis.jedis.Pipeline;
import com.redis.jedis.ReliableTransaction;
import com.redis.jedis.Response;
import com.redis.jedis.Transaction;
import com.redis.hash.HashPipeline;
import com.redis.hash.HashTransaction;

import java.util.Collections;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static com.redis.jedis.example.ExamplesTest.DEFAULT_CLIENT_CONFIG;
import static com.redis.jedis.example.ExamplesTest.DEFAULT_HOST_AND_PORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PipelineTest {

  @Before
  public void setUp() {
    JedisConnection connection = new JedisConnection(DEFAULT_HOST_AND_PORT, DEFAULT_CLIENT_CONFIG);
    connection.executeCommand(Protocol.Command.FLUSHALL);
    connection.disconnect();
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
  public void simpleTransaction() {
    JedisConnection connection = new JedisConnection(ExamplesTest.DEFAULT_HOST_AND_PORT, ExamplesTest.DEFAULT_CLIENT_CONFIG);
    Transaction tran = new Transaction(connection);
    Response<String> nul = tran.get("foo");
    Response<String> set = tran.set("foo", "bar");
    Response<String> get = tran.get("foo");
    tran.exec();
    assertNull(nul.get());
    assertEquals("OK", set.get());
    assertEquals("bar", get.get());
    connection.close();
  }

  @Test
  public void simpleReliableTransaction() {
    JedisConnection connection = new JedisConnection(ExamplesTest.DEFAULT_HOST_AND_PORT, ExamplesTest.DEFAULT_CLIENT_CONFIG);
    ReliableTransaction tran = new ReliableTransaction(connection);
    Response<String> nul = tran.get("foo");
    Response<String> set = tran.set("foo", "bar");
    Response<String> get = tran.get("foo");
    tran.exec();
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

  @Test
  public void moduleTransaction() {
    JedisConnection connection = new JedisConnection(ExamplesTest.DEFAULT_HOST_AND_PORT, ExamplesTest.DEFAULT_CLIENT_CONFIG);
    HashTransaction tran = new HashTransaction(connection);
    Response<Long> hset = tran.hset("key", Collections.singletonMap("foo", "bar"));
    Response<Map<String, String>> map = tran.hgetAll("key");
    Response<Long> del = tran.del("key");
    Response<Map<String, String>> empty = tran.hgetAll("key");
    tran.exec();
    assertEquals(Long.valueOf(1), hset.get());
    assertEquals(Collections.singletonMap("foo", "bar"), map.get());
    assertEquals(Long.valueOf(1), del.get());
    assertEquals(Collections.emptyMap(), empty.get());
    connection.close();
  }
}
