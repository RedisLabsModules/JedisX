package com.redis.jedis.providers;

import com.redis.jedis.CommandArguments;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.util.Pool;
import java.io.IOException;

public class PooledJedisConnectionProvider<C extends JedisConnection> implements JedisConnectionProvider, AutoCloseable {

  private final Pool<C> pool;

  public PooledJedisConnectionProvider(Pool<C> pool) {
    this.pool = pool;
  }

  @Override
  public void close() throws IOException {
    pool.close();
  }

  @Override
  public JedisConnection getConnection(CommandArguments args) {
    return pool.getResource();
  }
}
