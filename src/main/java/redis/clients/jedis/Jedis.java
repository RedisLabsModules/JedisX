package redis.clients.jedis;

import java.time.Duration;

import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.providers.JedisClusterConnectionProvider;
import redis.clients.jedis.providers.JedisConnectionProvider;

public class Jedis implements JedisCommands {

  protected final JedisCommandExecutor executor;
  private final RedisCommandObjects commandObjects;

  public Jedis(JedisConnectionProvider provider) {
    this.executor = new SimpleJedisExecutor(provider);
    this.commandObjects = (provider instanceof JedisClusterConnectionProvider)
        ? new RedisClusterCommandObjects() : new RedisCommandObjects();
  }

  public Jedis(JedisClusterConnectionProvider provider, int maxAttempts, Duration maxTotalRetriesDuration) {
    if (provider instanceof JedisClusterConnectionProvider) {
      this.executor = new ClusterCommandExecutor(provider, maxAttempts, maxTotalRetriesDuration);
      this.commandObjects = new RedisClusterCommandObjects();
    } else {
      this.executor = new RetryableCommandExecutor(provider, maxAttempts, maxTotalRetriesDuration);
      this.commandObjects = new RedisCommandObjects();
    }
  }

  protected final <T> T executeCommand(CommandObject<T> commandObject) {
    return executor.executeCommand(commandObject);
  }

  @Override
  public long del(String key) {
    return executeCommand(commandObjects.del(key));
  }

  @Override
  public String set(String key, String value) {
    return executeCommand(commandObjects.set(key, value));
  }

  @Override
  public String get(String key) {
    return executeCommand(commandObjects.get(key));
  }
}
