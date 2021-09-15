package redis.clients.jedis;

public interface JedisCommandExecutor {

  <T> T executeCommand(CommandObject<T> commandObject);
}
