package com.redis.set;

import com.redis.jedis.BuilderFactory;
import com.redis.jedis.CommandArguments;
import com.redis.jedis.CommandObject;
import com.redis.jedis.Protocol;
import com.redis.jedis.commands.ProtocolCommand;
import java.util.Collection;
import java.util.Set;

public class SetCommandObjects {

  protected CommandArguments commandArguments(ProtocolCommand command) {
    return new CommandArguments(command);
  }

  public CommandObject<Long> sadd(String key, Collection<String> members) {
    CommandArguments args = commandArguments(Protocol.Command.SADD);
    args.addKeyObject(key);
    for (String member : members) {
      args.addObject(member);
    }
    return new CommandObject<>(args, BuilderFactory.LONG);
  }

  public CommandObject<Set<String>> smembers(String key) {
    return new CommandObject<>(commandArguments(Protocol.Command.SMEMBERS).addKeyObject(key), BuilderFactory.STRING_SET);
  }
}
