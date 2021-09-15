package redis.clients.jedis.providers;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.JedisConnection;

public interface JedisConnectionProvider {
//
//  JedisConnection getConnection();

  JedisConnection getConnection(CommandArguments args);
}
