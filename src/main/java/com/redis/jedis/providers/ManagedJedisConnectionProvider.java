package com.redis.jedis.providers;

import com.redis.jedis.CommandArguments;
import com.redis.jedis.JedisConnection;

public class ManagedJedisConnectionProvider implements JedisConnectionProvider {

  private JedisConnection connection;

  public final void setConnection(JedisConnection connection) {
    this.connection = connection;
  }

  @Override
  public JedisConnection getConnection(CommandArguments args) {
    return connection;
  }
}
