package com.redis.jedis.providers;

import com.redis.jedis.CommandArguments;
import com.redis.jedis.JedisConnection;

public interface JedisConnectionProvider {
//
//  JedisConnection getConnection();

  JedisConnection getConnection(CommandArguments args);
}
