package com.redis.jedis.example;

import com.redis.jedis.ClusterPipeline;
import com.redis.jedis.DefaultJedisClientConfig;
import com.redis.jedis.Jedis;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.Response;
import com.redis.jedis.providers.JedisClusterConnectionProvider;
import com.redis.jedis.util.HostAndPortUtil;
import com.redis.jedis.util.JedisClusterTestUtil;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ClusterExamplesTest {

  @Before
  public void setUp() throws InterruptedException {
    JedisClusterTestUtil.setUp();
  }

  @After
  public void cleanUp() {
    JedisClusterTestUtil.cleanUp();
  }

  @Test
  public void cluster() {
    JedisClusterConnectionProvider cluster
        = new JedisClusterConnectionProvider(
            Collections.singleton(HostAndPortUtil.getClusterServers().get(0)),
            DefaultJedisClientConfig.builder().password("cluster").build(),
            new GenericObjectPoolConfig<>());
    Jedis jedis = new Jedis(cluster);
    Assert.assertNull(jedis.get("foo"));
    cluster.close();
  }

  @Test
  public void clusterPipeline() {
    final int count = 100;
    List<Response<?>> responses = new ArrayList<>(count * 4);
    GenericObjectPoolConfig<JedisConnection> poolConfig = new GenericObjectPoolConfig<>();
    poolConfig.setMaxTotal(1);

    JedisClusterConnectionProvider cluster
        = new JedisClusterConnectionProvider(
            Collections.singleton(HostAndPortUtil.getClusterServers().get(0)),
            DefaultJedisClientConfig.builder().password("cluster").build(),
            poolConfig);

    ClusterPipeline pipe = new ClusterPipeline(cluster);
    for (int i = 0; i < count; i++) {
      responses.add(pipe.set("key-" + i, "value-" + i));
    }
    for (int i = 0; i < count; i++) {
      responses.add(pipe.get("key-" + i));
    }
    for (int i = 0; i < count; i++) {
      responses.add(pipe.del("key-" + i));
    }
    for (int i = 0; i < count; i++) {
      responses.add(pipe.get("key-" + i));
    }
    pipe.sync();

    for (int i = 0; i < count; i++) {
      assertEquals("OK", responses.get(i).get());
    }
    for (int i = 0; i < count; i++) {
      assertEquals("value-" + i, responses.get(count + i).get());
    }
    for (int i = 0; i < count; i++) {
      assertEquals(Long.valueOf(1), responses.get(2 * count + i).get());
    }
    for (int i = 0; i < count; i++) {
      assertNull(responses.get(3 * count + i).get());
    }

    cluster.close();
  }

}
