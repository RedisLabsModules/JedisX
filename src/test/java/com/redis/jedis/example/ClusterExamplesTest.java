package com.redis.jedis.example;

import com.redis.jedis.DefaultJedisClientConfig;
import com.redis.jedis.Jedis;
import com.redis.jedis.providers.JedisClusterConnectionProvider;
import com.redis.jedis.util.HostAndPortUtil;
import com.redis.jedis.util.JedisClusterUtil;

import java.util.Collections;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClusterExamplesTest {

  @Before
  public void setUp() throws InterruptedException {
    JedisClusterUtil.setUp();
  }

  @After
  public void cleanUp() {
    JedisClusterUtil.cleanUp();
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

}
