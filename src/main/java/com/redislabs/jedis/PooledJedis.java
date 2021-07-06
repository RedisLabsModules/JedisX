package com.redislabs.jedis;

import com.redislabs.jedis.commands.JedisCommands;
import com.redislabs.jedis.util.Pool;
import java.io.Closeable;
import java.io.IOException;

public class PooledJedis implements JedisCommands, Closeable {

  private final Pool<JedisSocketConnection> pool;

  public PooledJedis(Pool<JedisSocketConnection> pool) {
    this.pool = pool;
  }

  @Override
  public void close() throws IOException {
    pool.close();
  }

  @Override
  public String set(String key, String value) {
    try (ManagedJedis j = new ManagedJedis(pool.getResource())) {
      return j.set(key, value);
    }
  }

  @Override
  public String get(String key) {
    try (ManagedJedis j = new ManagedJedis(pool.getResource())) {
      return j.get(key);
    }
  }

}
