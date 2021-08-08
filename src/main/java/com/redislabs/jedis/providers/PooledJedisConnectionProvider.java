package com.redislabs.jedis.providers;

import com.redislabs.jedis.JedisSocketConnection;
import com.redislabs.jedis.commands.ProtocolCommand;
import com.redislabs.jedis.util.Pool;
import java.io.Closeable;
import java.io.IOException;

public class PooledJedisConnectionProvider<C extends JedisSocketConnection> implements JedisConnectionProvider, Closeable {

  private final Pool<C> pool;

  public PooledJedisConnectionProvider(Pool<C> pool) {
    this.pool = pool;
  }

  @Override
  public void close() throws IOException {
    pool.close();
  }

  @Override
  public C getConnection(ProtocolCommand command, int slot) {
    return pool.getResource();
  }

  @Override
  public void returnConnection(int slot, JedisSocketConnection conn) {
    if (conn.isBroken()) {
      this.pool.returnBrokenResource((C) conn);
    } else {
      this.pool.returnResource((C) conn);
    }
  }
}
