package com.redis.jedis;

import com.redis.jedis.args.Rawable;
import com.redis.jedis.args.RawableFactory;
import com.redis.jedis.commands.ProtocolCommand;
import java.util.ArrayList;

public final class CommandArguments extends ArrayList<Rawable> {

  private CommandArguments() {
    throw new InstantiationError();
  }

  public CommandArguments(ProtocolCommand command) {
    super();
    super.add(command);
  }

  public ProtocolCommand getCommand() {
    return (ProtocolCommand) get(0);
  }

  public static CommandArguments of(ProtocolCommand command, Object... args) {
    CommandArguments ca = new CommandArguments(command);
    for (Object arg : args) {
      if (arg instanceof Rawable) {
        ca.add((Rawable) arg);
      } else if (arg instanceof byte[]) {
        ca.add(RawableFactory.from((byte[]) arg));
      } else if (arg instanceof String) {
        ca.add(RawableFactory.from((String) arg));
      } else {
        throw new IllegalArgumentException("\"" + arg.toString() + "\" is not a valid argument.");
      }
    }
    return ca;
  }
}
