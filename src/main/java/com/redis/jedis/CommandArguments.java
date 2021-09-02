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

  public boolean add(String string) {
    return super.add(RawableFactory.from(string));
  }

  public boolean add(byte[] binary) {
    return super.add(RawableFactory.from(binary));
  }

  public boolean addObject(Object arg) {
    if (arg instanceof Rawable) {
      super.add((Rawable) arg);
    } else if (arg instanceof byte[]) {
      this.add((byte[]) arg);
    } else if (arg instanceof String) {
      this.add((String) arg);
    } else {
      throw new IllegalArgumentException("\"" + arg.toString() + "\" is not a valid argument.");
    }
    return true;
  }

  public static CommandArguments of(ProtocolCommand command, Object... args) {
    CommandArguments ca = new CommandArguments(command);
    for (Object arg : args) {
      ca.addObject(arg);
    }
    return ca;
  }
}
