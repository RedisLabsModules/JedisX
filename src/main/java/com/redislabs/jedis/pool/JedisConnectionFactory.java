package com.redislabs.jedis.pool;

import com.redislabs.jedis.DefaultJedisSocketFactory;
import com.redislabs.jedis.HostAndPort;
import com.redislabs.jedis.JedisClientConfig;
import com.redislabs.jedis.JedisPreparedConnection;
import com.redislabs.jedis.JedisSocketFactory;
import com.redislabs.jedis.exceptions.JedisException;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JedisConnectionFactory implements PooledObjectFactory<JedisPreparedConnection> {

  private static final Logger logger = LoggerFactory.getLogger(JedisConnectionFactory.class);

  private HostAndPort hostAndPort;
  private JedisClientConfig clientConfig;
  private final JedisSocketFactory factory;

  public JedisConnectionFactory(HostAndPort hostAndPort, JedisClientConfig clientConfig) {
    this(new DefaultJedisSocketFactory(hostAndPort, clientConfig));
    this.hostAndPort = hostAndPort;
    this.clientConfig = clientConfig;
  }

  public JedisConnectionFactory(JedisSocketFactory factory) {
    this.factory = factory;
  }

  public void setHostAndPort(final HostAndPort hostAndPort) {
    this.hostAndPort = hostAndPort;
    if (this.factory instanceof DefaultJedisSocketFactory) {
      ((DefaultJedisSocketFactory) this.factory).updateHostAndPort(hostAndPort);
    }
  }

  public void setPassword(final String password) {
    this.clientConfig.updatePassword(password);
  }

  @Override
  public void activateObject(PooledObject<JedisPreparedConnection> pooledJedis) throws Exception {
//    final JedisPreparedConnection jedis = pooledJedis.getObject();
//    jedis.select(clientConfig.getDatabase());
  }

  @Override
  public void destroyObject(PooledObject<JedisPreparedConnection> pooledJedis) throws Exception {
    final JedisPreparedConnection jedis = pooledJedis.getObject();
    if (jedis.isConnected()) {
      try {
        // need a proper test, probably with mock
        if (!jedis.isBroken()) {
          jedis.quit();
        }
      } catch (Exception e) {
        logger.warn("Error while QUIT", e);
      }
      try {
        jedis.close();
      } catch (Exception e) {
        logger.warn("Error while close", e);
      }
    }
  }

  @Override
  public PooledObject<JedisPreparedConnection> makeObject() throws Exception {
    JedisPreparedConnection jedis = null;
    try {
      jedis = new JedisPreparedConnection(factory, clientConfig);
      jedis.connect();
      return new DefaultPooledObject<>(jedis);
    } catch (JedisException je) {
      if (jedis != null) {
        try {
          jedis.quit();
        } catch (Exception e) {
          logger.warn("Error while QUIT", e);
        }
        try {
          jedis.close();
        } catch (Exception e) {
          logger.warn("Error while close", e);
        }
      }
      throw je;
    }
  }

  @Override
  public void passivateObject(PooledObject<JedisPreparedConnection> pooledJedis) throws Exception {
    // TODO maybe should select db 0? Not sure right now.
  }

  @Override
  public boolean validateObject(PooledObject<JedisPreparedConnection> pooledJedis) {
    final JedisPreparedConnection jedis = pooledJedis.getObject();
    try {
//      String host = jedisSocketFactory.getHost();
//      int port = jedisSocketFactory.getPort();
//
//      String connectionHost = jedis.getClient().getHost();
//      int connectionPort = jedis.getClient().getPort();
//
//      return host.equals(connectionHost)
//          && port == connectionPort && jedis.isConnected()
//          && jedis.ping().equals("PONG");
      jedis.ping();
      return true;
    } catch (final Exception e) {
      logger.error("Error while validating pooled Jedis object.", e);
      return false;
    }
  }
}
