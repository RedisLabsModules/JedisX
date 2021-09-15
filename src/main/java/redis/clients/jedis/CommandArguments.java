package redis.clients.jedis;

import java.util.ArrayList;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.args.RawableFactory;
import redis.clients.jedis.commands.ProtocolCommand;

public class CommandArguments extends ArrayList<Rawable> {

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
//
//  public boolean add(String string) {
//    return super.add(RawableFactory.from(string));
//  }
//
//  public boolean add(byte[] binary) {
//    return super.add(RawableFactory.from(binary));
//  }

//  public boolean addObject(Object arg) {
  public CommandArguments addObject(Object arg) {
    if (arg instanceof Rawable) {
      super.add((Rawable) arg);
    } else if (arg instanceof byte[]) {
//      this.add((byte[]) arg);
      super.add(RawableFactory.from((byte[]) arg));
    } else if (arg instanceof String) {
//      this.add((String) arg);
      super.add(RawableFactory.from((String) arg));
    } else {
      throw new IllegalArgumentException("\"" + arg.toString() + "\" is not a valid argument.");
    }
//    return true;
    return this;
  }
//
//  public boolean addKey(String string) {
//    return this.add(string);
//  }
//
//  public boolean addKey(byte[] binary) {
//    return this.add(binary);
//  }

//  public boolean addKeyObject(Object arg) {
  public CommandArguments addKeyObject(Object arg) {
    if (arg instanceof Rawable) {
//      this.addKey(((Rawable) arg).getRaw());
      Rawable key = (Rawable) arg;
      processKey(key.getRaw());
      super.add(key);
    } else if (arg instanceof byte[]) {
//      this.addKey((byte[]) arg);
      byte[] key = (byte[]) arg;
      processKey(key);
      super.add(RawableFactory.from(key));
    } else if (arg instanceof String) {
//      this.addKey((String) arg);
      String key = (String) arg;
      processKey(key);
      super.add(RawableFactory.from(key));
    } else {
      throw new IllegalArgumentException("\"" + arg.toString() + "\" is not a valid argument.");
    }
//    return true;
    return this;
  }

  protected void processKey(byte[] key) {
    // do nothing
  }

  protected void processKey(String key) {
    // do nothing
  }
//
//  public static CommandArguments of(ProtocolCommand command, Object... args) {
//    CommandArguments ca = new CommandArguments(command);
//    for (Object arg : args) {
//      ca.addObject(arg);
//    }
//    return ca;
//  }
}
