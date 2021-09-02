package com.redis.set;

import com.redis.jedis.BuilderFactory;
import com.redis.jedis.CommandArguments;
import com.redis.jedis.CommandObject;
import com.redis.jedis.Protocol;
import java.util.Collection;
import java.util.Set;

public class SetCommandObjects {

  public CommandObject<Long> sadd(String key, Collection<String> members) {
    CommandArguments args = new CommandArguments(Protocol.Command.SADD);
    args.add(key);
    for (String member : members) {
      args.add(member);
    }
    return new CommandObject<>(args, BuilderFactory.LONG);
  }

  public CommandObject<Set<String>> smembers(String key) {
    return new CommandObject<>(CommandArguments.of(Protocol.Command.SMEMBERS, key), BuilderFactory.STRING_SET);
  }
}
