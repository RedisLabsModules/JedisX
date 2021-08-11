package com.redislabs.jedis.providers;

import com.redislabs.jedis.JedisConnection;
import com.redislabs.jedis.commands.ProtocolCommand;

public interface JedisConnectionProvider {
//
//  JedisConnection getConnection();

  JedisConnection getConnection(ProtocolCommand command);

  JedisConnection getConnection(ProtocolCommand command, byte[] key);

  JedisConnection getConnection(ProtocolCommand command, String key);
}
