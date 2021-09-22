package redis.clients.jedis;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.util.IOUtils;
import redis.clients.jedis.util.Pool;
import redis.clients.jedis.util.RedisInputStream;
import redis.clients.jedis.util.RedisOutputStream;
import redis.clients.jedis.util.SafeEncoder;

public class JedisConnection implements Closeable {

  private static final byte[][] EMPTY_ARGS = new byte[0][];

  private final Pool<JedisConnection> memberOf;
  private final JedisSocketFactory socketFactory;
  private Socket socket;
  private RedisOutputStream outputStream;
  private RedisInputStream inputStream;
  private int infiniteSoTimeout = 0;
  private boolean broken = false;

  public JedisConnection() {
    this(Protocol.DEFAULT_HOST, Protocol.DEFAULT_PORT);
  }

  public JedisConnection(final String host, final int port) {
    this(new HostAndPort(host, port), DefaultJedisClientConfig.builder().build());
  }

  public JedisConnection(final HostAndPort hostAndPort, final JedisClientConfig clientConfig) {
    this(new DefaultJedisSocketFactory(hostAndPort, clientConfig));
    this.infiniteSoTimeout = clientConfig.getBlockingSocketTimeoutMillis();
    initializeFromClientConfig(clientConfig);
  }

  public JedisConnection(final JedisSocketFactory socketFactory, JedisClientConfig clientConfig) {
    this(socketFactory, clientConfig, null);
  }

  public JedisConnection(final JedisSocketFactory socketFactory, JedisClientConfig clientConfig, Pool<JedisConnection> pool) {
    this.socketFactory = socketFactory;
    this.infiniteSoTimeout = clientConfig.getBlockingSocketTimeoutMillis();
    initializeFromClientConfig(clientConfig);
    this.memberOf = pool;
  }

  public JedisConnection(final JedisSocketFactory socketFactory) {
    this.socketFactory = socketFactory;
    this.memberOf = null;
  }

  @Override
  public String toString() {
    return "Connection{" + socketFactory + "}";
  }

  public void setSoTimeout(int soTimeout) {
    socketFactory.setSocketTimeout(soTimeout);
    if (this.socket != null) {
      try {
        this.socket.setSoTimeout(soTimeout);
      } catch (SocketException ex) {
        broken = true;
        throw new JedisConnectionException(ex);
      }
    }
  }

  public void setTimeoutInfinite() {
    try {
      if (!isConnected()) {
        connect();
      }
      socket.setSoTimeout(infiniteSoTimeout);
    } catch (SocketException ex) {
      broken = true;
      throw new JedisConnectionException(ex);
    }
  }

  public void rollbackTimeout() {
    try {
      socket.setSoTimeout(socketFactory.getSocketTimeout());
    } catch (SocketException ex) {
      broken = true;
      throw new JedisConnectionException(ex);
    }
  }

  public Object executeCommand(final ProtocolCommand cmd) {
    sendCommand(cmd);
    return getOne();
  }

  public void sendCommand(final ProtocolCommand cmd, final String... args) {
    final byte[][] bargs = new byte[args.length][];
    for (int i = 0; i < args.length; i++) {
      bargs[i] = SafeEncoder.encode(args[i]);
    }
    sendCommand(cmd, bargs);
  }

  public void sendCommand(final ProtocolCommand cmd) {
    sendCommand(cmd, EMPTY_ARGS);
  }

  public void sendCommand(final ProtocolCommand cmd, final byte[]... args) {
    try {
      connect();
      Protocol.sendCommand(outputStream, cmd, args);
    } catch (JedisConnectionException ex) {
      /*
       * When client send request which formed by invalid protocol, Redis send back error message
       * before close connection. We try to read it to provide reason of failure.
       */
      try {
        String errorMessage = Protocol.readErrorLineIfPossible(inputStream);
        if (errorMessage != null && errorMessage.length() > 0) {
          ex = new JedisConnectionException(errorMessage, ex.getCause());
        }
      } catch (Exception e) {
        /*
         * Catch any IOException or JedisConnectionException occurred from InputStream#read and just
         * ignore. This approach is safe because reading error message is optional and connection
         * will eventually be closed.
         */
      }
      // Any other exceptions related to connection?
      broken = true;
      throw ex;
    }
  }

  public <T> T executeCommand(final CommandObject<T> commandObject) {
    sendCommand(commandObject.getArguments());
    return commandObject.getBuilder().build(getOne());
  }

  public void sendCommand(final CommandArguments args) {
    try {
      connect();
      Protocol.sendCommand(outputStream, args);
    } catch (JedisConnectionException ex) {
      /*
       * When client send request which formed by invalid protocol, Redis send back error message
       * before close connection. We try to read it to provide reason of failure.
       */
      try {
        String errorMessage = Protocol.readErrorLineIfPossible(inputStream);
        if (errorMessage != null && errorMessage.length() > 0) {
          ex = new JedisConnectionException(errorMessage, ex.getCause());
        }
      } catch (Exception e) {
        /*
         * Catch any IOException or JedisConnectionException occurred from InputStream#read and just
         * ignore. This approach is safe because reading error message is optional and connection
         * will eventually be closed.
         */
      }
      // Any other exceptions related to connection?
      broken = true;
      throw ex;
    }
  }

  public void connect() throws JedisConnectionException {
    if (!isConnected()) {
      try {
        socket = socketFactory.createSocket();

        outputStream = new RedisOutputStream(socket.getOutputStream());
        inputStream = new RedisInputStream(socket.getInputStream());
      } catch (JedisConnectionException jce) {
        broken = true;
        throw jce;
      } catch (IOException ioe) {
        broken = true;
        throw new JedisConnectionException("Failed to create input/output stream", ioe);
      } finally {
        if (broken) {
          IOUtils.closeQuietly(socket);
        }
      }
    }
  }

  @Override
  public void close() {
    if (this.memberOf != null) {
      if (isBroken()) {
        this.memberOf.returnBrokenResource(this);
      } else {
        this.memberOf.returnResource(this);
      }
    } else {
      // disconnect();
    }
  }

  public void disconnect() {
    if (isConnected()) {
      try {
        outputStream.flush();
        socket.close();
      } catch (IOException ex) {
        broken = true;
        throw new JedisConnectionException(ex);
      } finally {
        IOUtils.closeQuietly(socket);
      }
    }
  }

  public boolean isConnected() {
    return socket != null && socket.isBound() && !socket.isClosed() && socket.isConnected()
        && !socket.isInputShutdown() && !socket.isOutputShutdown();
  }

  public String getStatusCodeReply() {
    flush();
    final byte[] resp = (byte[]) readProtocolWithCheckingBroken();
    if (null == resp) {
      return null;
    } else {
      return SafeEncoder.encode(resp);
    }
  }

  public String getBulkReply() {
    final byte[] result = getBinaryBulkReply();
    if (null != result) {
      return SafeEncoder.encode(result);
    } else {
      return null;
    }
  }

  public byte[] getBinaryBulkReply() {
    flush();
    return (byte[]) readProtocolWithCheckingBroken();
  }

  public Long getIntegerReply() {
    flush();
    return (Long) readProtocolWithCheckingBroken();
  }

  public List<String> getMultiBulkReply() {
//    return BuilderFactory.STRING_LIST.build(getBinaryMultiBulkReply());
    return getBinaryMultiBulkReply().stream().map(binary -> SafeEncoder.encode(binary)).collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public List<byte[]> getBinaryMultiBulkReply() {
    flush();
    return (List<byte[]>) readProtocolWithCheckingBroken();
  }

  @Deprecated
  public List<Object> getRawObjectMultiBulkReply() {
    return getUnflushedObjectMultiBulkReply();
  }

  @SuppressWarnings("unchecked")
  public List<Object> getUnflushedObjectMultiBulkReply() {
    return (List<Object>) readProtocolWithCheckingBroken();
  }

  public List<Object> getObjectMultiBulkReply() {
    flush();
    return getUnflushedObjectMultiBulkReply();
  }

  @SuppressWarnings("unchecked")
  public List<Long> getIntegerMultiBulkReply() {
    flush();
    return (List<Long>) readProtocolWithCheckingBroken();
  }

  public Object getOne() {
    flush();
    return readProtocolWithCheckingBroken();
  }

  public boolean isBroken() {
    return broken;
  }

  protected void flush() {
    try {
      outputStream.flush();
    } catch (IOException ex) {
      broken = true;
      throw new JedisConnectionException(ex);
    }
  }

  protected Object readProtocolWithCheckingBroken() {
    if (broken) {
      throw new JedisConnectionException("Attempting to read from a broken connection");
    }

    try {
      return Protocol.read(inputStream);
    } catch (JedisConnectionException exc) {
      broken = true;
      throw exc;
    }
  }

  public List<Object> getMany(final int count) {
    flush();
    final List<Object> responses = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      try {
        responses.add(readProtocolWithCheckingBroken());
      } catch (JedisDataException e) {
        responses.add(e);
      }
    }
    return responses;
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