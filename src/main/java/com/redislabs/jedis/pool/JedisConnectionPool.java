package com.redislabs.jedis.pool;

import com.redislabs.jedis.HostAndPort;
import com.redislabs.jedis.JedisClientConfig;
import com.redislabs.jedis.JedisPreparedConnection;
import com.redislabs.jedis.util.Pool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JedisConnectionPool extends Pool<JedisPreparedConnection> {

  public JedisConnectionPool(HostAndPort hostAndPort, JedisClientConfig clientConfig) {
    this(new JedisConnectionFactory(hostAndPort, clientConfig));
  }

  public JedisConnectionPool(PooledObjectFactory<JedisPreparedConnection> factory) {
    super(new GenericObjectPoolConfig<JedisPreparedConnection>(), factory);
  }

  public JedisConnectionPool(GenericObjectPoolConfig<JedisPreparedConnection> poolConfig, PooledObjectFactory<JedisPreparedConnection> factory) {
    super(poolConfig, factory);
  }
}
