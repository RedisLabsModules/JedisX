package com.redis.jedis.providers;

import com.redis.jedis.CommandArguments;
import com.redis.jedis.HostAndPort;
import com.redis.jedis.JedisClientConfig;
import com.redis.jedis.JedisConnection;

public class SimpleJedisConnectionProvider implements JedisConnectionProvider, AutoCloseable {

  private final JedisConnection connection;

  public SimpleJedisConnectionProvider(HostAndPort hap, JedisClientConfig config) {
    this.connection = new JedisConnection(hap, config);
  }

  public SimpleJedisConnectionProvider(JedisConnection connection) {
    this.connection = connection;
  }

  @Override
  public void close() {
    connection.disconnect();
  }

  @Override
  public JedisConnection getConnection(CommandArguments args) {
    return connection;
  }

}
