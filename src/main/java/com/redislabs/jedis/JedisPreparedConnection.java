package com.redislabs.jedis;

import com.redislabs.jedis.exceptions.JedisException;

public class JedisPreparedConnection extends JedisSocketConnection {

  public JedisPreparedConnection() {
    super();
  }

  public JedisPreparedConnection(final String host, final int port) {
    super(host, port);
  }

  public JedisPreparedConnection(final HostAndPort hostAndPort, final JedisClientConfig clientConfig) {
    super(hostAndPort, clientConfig);
    initializeFromClientConfig(clientConfig);
  }

  public JedisPreparedConnection(JedisSocketFactory factory, final JedisClientConfig clientConfig) {
    super(factory);
    initializeFromClientConfig(clientConfig);
  }

  private void initializeFromClientConfig(JedisClientConfig config) {
    try {
      connect();
      String password = config.getPassword();
      if (password != null) {
        String user = config.getUser();
        if (user != null) {
          auth(user, password);
        } else {
          auth(password);
        }
      }
      int dbIndex = config.getDatabase();
      if (dbIndex > 0) {
        select(dbIndex);
      }
      String clientName = config.getClientName();
      if (clientName != null) {
        // TODO: need to figure out something without encoding
        clientSetname(clientName);
      }
    } catch (JedisException je) {
      try {
        if (isConnected()) {
          quit();
        }
        disconnect();
      } catch (Exception e) {
        //
      }
      throw je;
    }
  }

  public String quit() {
    sendCommand(Protocol.Command.QUIT);
    String quitReturn = getStatusCodeReply();
    disconnect();
    return quitReturn;
  }

  private String auth(final String password) {
    sendCommand(Protocol.Command.AUTH, password);
    return getStatusCodeReply();
  }

  private String auth(final String user, final String password) {
    sendCommand(Protocol.Command.AUTH, user, password);
    return getStatusCodeReply();
  }

  public String select(final int index) {
    sendCommand(Protocol.Command.SELECT, Protocol.toByteArray(index));
    return getStatusCodeReply();
  }

  private String clientSetname(final String name) {
    sendCommand(Protocol.Command.CLIENT, Protocol.Keyword.SETNAME.name(), name);
    return getStatusCodeReply();
  }

  public void ping() {
    sendCommand(Protocol.Command.PING);
    String status = getStatusCodeReply();
    if (!"PONG".equals(status)) {
      throw new JedisException(status);
    }
  }

}
