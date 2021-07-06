package com.redislabs.jedis;

import com.redislabs.jedis.commands.ProtocolCommand;
import com.redislabs.jedis.exceptions.JedisConnectionException;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConnectionTest {

  private JedisSocketConnection client;

  @After
  public void tearDown() throws Exception {
    if (client != null) {
      client.close();
    }
  }

  @Test(expected = JedisConnectionException.class)
  public void checkUnkownHost() {
    client = new JedisSocketConnection("someunknownhost", Protocol.DEFAULT_PORT);
    client.connect();
  }

  @Test(expected = JedisConnectionException.class)
  public void checkWrongPort() {
    client = new JedisSocketConnection(Protocol.DEFAULT_HOST, 55665);
    client.connect();
  }

  @Test
  public void connectIfNotConnectedWhenSettingTimeoutInfinite() {
    client = new JedisSocketConnection("localhost", 6379);
    client.setTimeoutInfinite();
  }

  @Test
  public void checkCloseable() {
    client = new JedisSocketConnection("localhost", 6379);
    client.connect();
    client.close();
  }

  @Test
  public void getErrorMultibulkLength() throws Exception {
    class TestConnection extends JedisSocketConnection {

      public TestConnection() {
        super("localhost", 6379);
      }

      @Override
      public void sendCommand(ProtocolCommand cmd, byte[]... args) {
        super.sendCommand(cmd, args);
      }
    }

    TestConnection conn = new TestConnection();

    try {
      conn.sendCommand(Protocol.Command.HMSET, new byte[1024 * 1024 + 1][0]);
      fail("Should throw exception");
    } catch (JedisConnectionException jce) {
      assertEquals("ERR Protocol error: invalid multibulk length", jce.getMessage());
    }
  }

  @Test
  public void readWithBrokenConnection() {
    class BrokenConnection extends JedisSocketConnection {

      private BrokenConnection() {
        super("nonexistinghost", 0);
        try {
          connect();
          fail("Client should fail connecting to nonexistinghost");
        } catch (JedisConnectionException ignored) {
        }
      }

      private Object read() {
        return readProtocolWithCheckingBroken();
      }
    }

    BrokenConnection conn = new BrokenConnection();
    try {
      conn.read();
      fail("Read should fail as connection is broken");
    } catch (JedisConnectionException jce) {
      assertEquals("Attempting to read from a broken connection", jce.getMessage());
    }
  }
}
