package com.redis.jedis;

import com.redis.jedis.HostAndPort;
import com.redis.jedis.JedisClientConfig;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.util.Pool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JedisConnectionPool extends Pool<JedisConnection> {

  public JedisConnectionPool(GenericObjectPoolConfig<JedisConnection> poolConfig,
      HostAndPort hostAndPort, JedisClientConfig clientConfig) {
    this(poolConfig, new JedisConnectionFactory(hostAndPort, clientConfig));
  }

  public JedisConnectionPool(HostAndPort hostAndPort, JedisClientConfig clientConfig) {
    this(new JedisConnectionFactory(hostAndPort, clientConfig));
  }

  public JedisConnectionPool(PooledObjectFactory<JedisConnection> factory) {
    super(new GenericObjectPoolConfig<JedisConnection>(), factory);
  }

  public JedisConnectionPool(GenericObjectPoolConfig<JedisConnection> poolConfig, PooledObjectFactory<JedisConnection> factory) {
    super(poolConfig, factory);
  }
}
