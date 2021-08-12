package com.redis.jedis;

import java.io.Closeable;
import java.util.List;

public class PipelineBase extends Queable implements Closeable {

  protected final JedisConnection connection;
//
//  public PipelineBase(JedisConnectionProvider provider) {
//    this(provider.getConnection());
//  }

  public PipelineBase(JedisConnection connection) {
    this.connection = connection;
  }

  @Override
  public void close() {
    sync();
  }

  /**
   * Synchronize pipeline by reading all responses. This operation close the pipeline. In order to
   * get return values from pipelined commands, capture the different Response&lt;?&gt; of the
   * commands you execute.
   */
  public final void sync() {
    List<Object> unformatted = connection.getMany(getPipelinedResponseLength());
    for (Object o : unformatted) {
      generateResponse(o);
    }
  }
}
