package com.redis.jedis.providers;

import com.redis.jedis.JedisConnection;
import com.redis.jedis.commands.ProtocolCommand;

public interface JedisConnectionProvider {
//
//  JedisConnection getConnection();

  JedisConnection getConnection(ProtocolCommand command);

  JedisConnection getConnection(ProtocolCommand command, byte[] key);

  JedisConnection getConnection(ProtocolCommand command, String key);
}
