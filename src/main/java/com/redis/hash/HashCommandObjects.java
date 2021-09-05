package com.redis.hash;

import com.redis.jedis.*;
import com.redis.jedis.commands.ProtocolCommand;
import java.util.Map;

public class HashCommandObjects {

  protected CommandArguments commandArguments(ProtocolCommand command) {
    return new CommandArguments(command);
  }

  public CommandObject<Long> hset(String key, Map<String, String> fieldValues) {
    CommandArguments args = commandArguments(Protocol.Command.HSET);
    args.addKeyObject(key);
    for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
      args.addObject(entry.getKey());
      args.addObject(entry.getValue());
    }
    return new CommandObject<>(args, Resp2BuilderFactory.LONG);
  }

  public CommandObject<Map<String, String>> hgetAll(String key) {
    return new CommandObject<>(commandArguments(Protocol.Command.HGETALL).addKeyObject(key), Resp2BuilderFactory.STRING_MAP);
  }
}
