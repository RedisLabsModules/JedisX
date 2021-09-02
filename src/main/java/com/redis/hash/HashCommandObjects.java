package com.redis.hash;

import com.redis.jedis.*;
import com.redis.jedis.args.RawableFactory;
import java.util.Map;

public class HashCommandObjects {

  public CommandObject<Long> hset(String key, Map<String, String> fieldValues) {
    CommandArguments args = new CommandArguments(Protocol.Command.HSET);
    args.add(RawableFactory.from(key));
    for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
      args.add(entry.getKey());
      args.add(entry.getValue());
    }
    return new CommandObject<>(args, BuilderFactory.LONG);
  }

  public CommandObject<Map<String, String>> hgetAll(String key) {
    return new CommandObject<>(CommandArguments.of(Protocol.Command.HGETALL, key), BuilderFactory.STRING_MAP);
  }
}
