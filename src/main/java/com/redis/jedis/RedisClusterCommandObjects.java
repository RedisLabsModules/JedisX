package com.redis.jedis;

import com.redis.jedis.commands.ProtocolCommand;

public class RedisClusterCommandObjects extends RedisCommandObjects {

  @Override
  protected ClusterCommandArguments commandArguments(ProtocolCommand command) {
    return new ClusterCommandArguments(command);
  }
}
