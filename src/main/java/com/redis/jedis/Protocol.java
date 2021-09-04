package com.redis.jedis;

import com.redis.jedis.exceptions.JedisDataException;
import com.redis.jedis.exceptions.JedisNoScriptException;
import com.redis.jedis.exceptions.JedisConnectionException;
import com.redis.jedis.exceptions.JedisAskDataException;
import com.redis.jedis.exceptions.JedisMovedDataException;
import com.redis.jedis.exceptions.JedisBusyException;
import com.redis.jedis.exceptions.JedisClusterException;
import com.redis.jedis.exceptions.JedisAccessControlException;
import com.redis.jedis.args.Rawable;
import com.redis.jedis.commands.ProtocolCommand;
import com.redis.jedis.util.RedisInputStream;
import com.redis.jedis.util.RedisOutputStream;
import com.redis.jedis.util.SafeEncoder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Protocol {

  public static final String DEFAULT_HOST = "127.0.0.1";
  public static final int DEFAULT_PORT = 6379;
  public static final int DEFAULT_SENTINEL_PORT = 26379;
  public static final int DEFAULT_TIMEOUT = 2000;
  public static final int DEFAULT_DATABASE = 0;

  public static final byte DOLLAR_BYTE = '$';
  public static final byte ASTERISK_BYTE = '*';
  public static final byte PLUS_BYTE = '+';
  public static final byte MINUS_BYTE = '-';
  public static final byte COLON_BYTE = ':';

  public static final byte[] BYTES_TRUE = toByteArray(1);
  public static final byte[] BYTES_FALSE = toByteArray(0);
  public static final byte[] BYTES_TILDE = SafeEncoder.encode("~");
  public static final byte[] BYTES_EQUAL = SafeEncoder.encode("=");
  public static final byte[] BYTES_ASTERISK = SafeEncoder.encode("*");

  public static final byte[] POSITIVE_INFINITY_BYTES = "+inf".getBytes();
  public static final byte[] NEGATIVE_INFINITY_BYTES = "-inf".getBytes();

  private static final String ASK_PREFIX = "ASK ";
  private static final String MOVED_PREFIX = "MOVED ";
  private static final String CLUSTERDOWN_PREFIX = "CLUSTERDOWN ";
  private static final String BUSY_PREFIX = "BUSY ";
  private static final String NOSCRIPT_PREFIX = "NOSCRIPT ";
  private static final String WRONGPASS_PREFIX = "WRONGPASS";
  private static final String NOPERM_PREFIX = "NOPERM";

  private Protocol() {
    // this prevent the class from instantiation
  }

  public static void sendCommand(final RedisOutputStream os, final Rawable command,
      final byte[]... args) {
    sendCommand(os, command.getRaw(), args);
  }

  private static void sendCommand(final RedisOutputStream os, final byte[] command,
      final byte[]... args) {
    try {
      os.write(ASTERISK_BYTE);
      os.writeIntCrLf(args.length + 1);
      os.write(DOLLAR_BYTE);
      os.writeIntCrLf(command.length);
      os.write(command);
      os.writeCrLf();

      for (final byte[] arg : args) {
        os.write(DOLLAR_BYTE);
        os.writeIntCrLf(arg.length);
        os.write(arg);
        os.writeCrLf();
      }
    } catch (IOException e) {
      throw new JedisConnectionException(e);
    }
  }

  public static void sendCommand(final RedisOutputStream os, CommandArguments args) {
    try {
      os.write(ASTERISK_BYTE);
      os.writeIntCrLf(args.size());
      for (Rawable arg : args) {
        os.write(DOLLAR_BYTE);
        final byte[] bin = arg.getRaw();
        os.writeIntCrLf(bin.length);
        os.write(bin);
        os.writeCrLf();
      }
    } catch (IOException e) {
      throw new JedisConnectionException(e);
    }
  }

  private static void processError(final RedisInputStream is) {
    String message = is.readLine();
    // TODO: I'm not sure if this is the best way to do this.
    // Maybe Read only first 5 bytes instead?
    if (message.startsWith(MOVED_PREFIX)) {
      String[] movedInfo = parseTargetHostAndSlot(message);
//      throw new JedisMovedDataException(message, new HostAndPort(movedInfo[1],
//          Integer.parseInt(movedInfo[2])), Integer.parseInt(movedInfo[0]));
      throw new JedisMovedDataException(message, HostAndPort.from(movedInfo[1]), Integer.parseInt(movedInfo[0]));
    } else if (message.startsWith(ASK_PREFIX)) {
      String[] askInfo = parseTargetHostAndSlot(message);
//      throw new JedisAskDataException(message, new HostAndPort(askInfo[1],
//          Integer.parseInt(askInfo[2])), Integer.parseInt(askInfo[0]));
      throw new JedisAskDataException(message, HostAndPort.from(askInfo[1]), Integer.parseInt(askInfo[0]));
    } else if (message.startsWith(CLUSTERDOWN_PREFIX)) {
      throw new JedisClusterException(message);
    } else if (message.startsWith(BUSY_PREFIX)) {
      throw new JedisBusyException(message);
    } else if (message.startsWith(NOSCRIPT_PREFIX)) {
      throw new JedisNoScriptException(message);
    } else if (message.startsWith(WRONGPASS_PREFIX)) {
      throw new JedisAccessControlException(message);
    } else if (message.startsWith(NOPERM_PREFIX)) {
      throw new JedisAccessControlException(message);
    }
    throw new JedisDataException(message);
  }

  public static String readErrorLineIfPossible(RedisInputStream is) {
    final byte b = is.readByte();
    // if buffer contains other type of response, just ignore.
    if (b != MINUS_BYTE) {
      return null;
    }
    return is.readLine();
  }

//  private static String[] parseTargetHostAndSlot(String clusterRedirectResponse) {
//    String[] response = new String[3];
//    String[] messageInfo = clusterRedirectResponse.split(" ");
//    String[] targetHostAndPort = HostAndPort.extractParts(messageInfo[2]);
//    response[0] = messageInfo[1];
//    response[1] = targetHostAndPort[0];
//    response[2] = targetHostAndPort[1];
//    return response;
//  }
  private static String[] parseTargetHostAndSlot(String clusterRedirectResponse) {
    String[] response = new String[2];
    String[] messageInfo = clusterRedirectResponse.split(" ");
    response[0] = messageInfo[1];
    response[1] = messageInfo[2];
    return response;
  }

  private static Object process(final RedisInputStream is) {
    final byte b = is.readByte();
    switch (b) {
    case PLUS_BYTE:
      return processStatusCodeReply(is);
    case DOLLAR_BYTE:
      return processBulkReply(is);
    case ASTERISK_BYTE:
      return processMultiBulkReply(is);
    case COLON_BYTE:
      return processInteger(is);
    case MINUS_BYTE:
      processError(is);
      return null;
    default:
      throw new JedisConnectionException("Unknown reply: " + (char) b);
    }
  }

  private static byte[] processStatusCodeReply(final RedisInputStream is) {
    return is.readLineBytes();
  }

  private static byte[] processBulkReply(final RedisInputStream is) {
    final int len = is.readIntCrLf();
    if (len == -1) {
      return null;
    }

    final byte[] read = new byte[len];
    int offset = 0;
    while (offset < len) {
      final int size = is.read(read, offset, (len - offset));
      if (size == -1) throw new JedisConnectionException(
          "It seems like server has closed the connection.");
      offset += size;
    }

    // read 2 more bytes for the command delimiter
    is.readByte();
    is.readByte();

    return read;
  }

  private static Long processInteger(final RedisInputStream is) {
    return is.readLongCrLf();
  }

  private static List<Object> processMultiBulkReply(final RedisInputStream is) {
    final int num = is.readIntCrLf();
    if (num == -1) {
      return null;
    }
    final List<Object> ret = new ArrayList<>(num);
    for (int i = 0; i < num; i++) {
      try {
        ret.add(process(is));
      } catch (JedisDataException e) {
        ret.add(e);
      }
    }
    return ret;
  }

  public static Object read(final RedisInputStream is) {
    return process(is);
  }

  public static final byte[] toByteArray(final boolean value) {
    return value ? BYTES_TRUE : BYTES_FALSE;
  }

  public static final byte[] toByteArray(final int value) {
    return SafeEncoder.encode(String.valueOf(value));
  }

  public static final byte[] toByteArray(final long value) {
    return SafeEncoder.encode(String.valueOf(value));
  }

  public static final byte[] toByteArray(final double value) {
    if (value == Double.POSITIVE_INFINITY) {
      return POSITIVE_INFINITY_BYTES;
    } else if (value == Double.NEGATIVE_INFINITY) {
      return NEGATIVE_INFINITY_BYTES;
    } else {
      return SafeEncoder.encode(String.valueOf(value));
    }
  }

  public static enum Command implements ProtocolCommand {
    PING, SET, GET, GETDEL, GETEX, QUIT, EXISTS, DEL, UNLINK, TYPE, FLUSHDB, KEYS, RANDOMKEY, RENAME,
    RENAMENX, RENAMEX, DBSIZE, EXPIRE, EXPIREAT, TTL, SELECT, MOVE, FLUSHALL, GETSET, MGET, SETNX,
    SETEX, MSET, MSETNX, DECRBY, DECR, INCRBY, INCR, APPEND, SUBSTR, HSET, HGET, HSETNX, HMSET,
    HMGET, HINCRBY, HEXISTS, HDEL, HLEN, HKEYS, HVALS, HGETALL, HRANDFIELD, RPUSH, LPUSH, LLEN, LRANGE, LTRIM,
    LINDEX, LSET, LREM, LPOP, RPOP, RPOPLPUSH, SADD, SMEMBERS, SREM, SPOP, SMOVE, SCARD, SISMEMBER,
    SINTER, SINTERSTORE, SUNION, SUNIONSTORE, SDIFF, SDIFFSTORE, SRANDMEMBER, ZADD, ZDIFF, ZDIFFSTORE, ZRANGE, ZREM,
    ZINCRBY, ZRANK, ZREVRANK, ZREVRANGE, ZRANDMEMBER, ZCARD, ZSCORE, ZPOPMAX, ZPOPMIN, MULTI, DISCARD, EXEC,
    WATCH, UNWATCH, SORT, BLPOP, BRPOP, AUTH, SUBSCRIBE, PUBLISH, UNSUBSCRIBE, PSUBSCRIBE,
    PUNSUBSCRIBE, PUBSUB, ZCOUNT, ZRANGEBYSCORE, ZREVRANGEBYSCORE, ZREMRANGEBYRANK,
    ZREMRANGEBYSCORE, ZUNION, ZUNIONSTORE, ZINTER, ZINTERSTORE, ZLEXCOUNT, ZRANGEBYLEX, ZREVRANGEBYLEX,
    ZREMRANGEBYLEX, SAVE, BGSAVE, BGREWRITEAOF, LASTSAVE, SHUTDOWN, INFO, MONITOR, SLAVEOF, CONFIG,
    STRLEN, LPUSHX, PERSIST, RPUSHX, ECHO, LINSERT, DEBUG, BRPOPLPUSH, SETBIT, GETBIT,
    BITPOS, SETRANGE, GETRANGE, EVAL, EVALSHA, SCRIPT, SLOWLOG, OBJECT, BITCOUNT, BITOP, SENTINEL,
    DUMP, RESTORE, PEXPIRE, PEXPIREAT, PTTL, INCRBYFLOAT, PSETEX, CLIENT, TIME, MIGRATE,
    HINCRBYFLOAT, SCAN, HSCAN, SSCAN, ZSCAN, WAIT, CLUSTER, ASKING, PFADD, PFCOUNT, PFMERGE,
    READONLY, GEOADD, GEODIST, GEOHASH, GEOPOS, GEORADIUS, GEORADIUS_RO, GEORADIUSBYMEMBER,
    GEORADIUSBYMEMBER_RO, MODULE, BITFIELD, HSTRLEN, TOUCH, SWAPDB, MEMORY, XADD, XLEN, XDEL,
    XTRIM, XRANGE, XREVRANGE, XREAD, XACK, XGROUP, XREADGROUP, XPENDING, XCLAIM, XAUTOCLAIM, ACL, XINFO,
    BITFIELD_RO, LPOS, SMISMEMBER, ZMSCORE, BZPOPMIN, BZPOPMAX, BLMOVE, LMOVE, COPY;

    private final byte[] raw;

    Command() {
      raw = SafeEncoder.encode(this.name());
    }

    @Override
    public byte[] getRaw() {
      return raw;
    }
  }

  public static enum Keyword implements Rawable {
    AGGREGATE, ALPHA, ASC, BY, DESC, GET, LIMIT, MESSAGE, NO, NOSORT, PMESSAGE, PSUBSCRIBE,
    PUNSUBSCRIBE, OK, ONE, QUEUED, SET, STORE, SUBSCRIBE, UNSUBSCRIBE, WEIGHTS, WITHSCORES,
    RESETSTAT, REWRITE, RESET, FLUSH, EXISTS, LOAD, KILL, LEN, REFCOUNT, ENCODING, IDLETIME,
    GETNAME, SETNAME, LIST, MATCH, COUNT, TYPE, PING, PONG, UNLOAD, REPLACE, KEYS, PAUSE, DOCTOR,
    BLOCK, NOACK, STREAMS, KEY, CREATE, MKSTREAM, SETID, DESTROY, DELCONSUMER, MAXLEN, GROUP, ID,
    IDLE, TIME, RETRYCOUNT, FORCE, USAGE, SAMPLES, STREAM, GROUPS, CONSUMERS, HELP, FREQ, SETUSER,
    GETUSER, DELUSER, WHOAMI, CAT, GENPASS, USERS, LOG, INCR, SAVE, JUSTID, WITHVALUES, UNBLOCK,
    NOMKSTREAM, MINID, DB, ABSTTL;

    private final byte[] raw;

    Keyword() {
      raw = SafeEncoder.encode(this.name().toLowerCase(Locale.ENGLISH));
    }

    @Override
    public byte[] getRaw() {
      return raw;
    }
  }
}