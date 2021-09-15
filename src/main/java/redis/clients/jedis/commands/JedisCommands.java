package redis.clients.jedis.commands;

public interface JedisCommands {

  long del(String key);

  String set(String key, String value);

  String get(String key);
}
