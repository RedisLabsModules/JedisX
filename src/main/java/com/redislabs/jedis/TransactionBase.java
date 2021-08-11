package com.redislabs.jedis;

import java.io.Closeable;
import java.util.List;

/**
 * Transaction is nearly identical to Pipeline, only differences are the multi/discard behaviors
 */
public class TransactionBase extends Queable implements Closeable {

  private boolean inTransaction = true;

  protected final JedisConnection connection;

  public TransactionBase(JedisConnection connection) {
    this.connection = connection;
    this.connection.sendCommand(Protocol.Command.MULTI);
  }

  @Override
  public void close() {
    clear();
  }

  public void clear() {
    if (inTransaction) {
      discard();
    }
  }

  public final void exec() {
    // ignore QUEUED or ERROR
    connection.getMany(1 + getPipelinedResponseLength());
    connection.sendCommand(Protocol.Command.EXEC);
    inTransaction = false;

    List<Object> unformatted = connection.getObjectMultiBulkReply();
    unformatted.stream().forEachOrdered(u -> generateResponse(u));
  }

  public void discard() {
    // ignore QUEUED or ERROR
    connection.getMany(1 + getPipelinedResponseLength());
    connection.sendCommand(Protocol.Command.DISCARD);
    connection.getStatusCodeReply(); // OK
    inTransaction = false;
    clean();
  }
}
