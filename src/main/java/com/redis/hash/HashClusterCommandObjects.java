package com.redis.hash;

import com.redis.jedis.ClusterCommandArguments;
import com.redis.jedis.commands.ProtocolCommand;

public class HashClusterCommandObjects extends HashCommandObjects {

  @Override
  protected ClusterCommandArguments commandArguments(ProtocolCommand command) {
    return new ClusterCommandArguments(command);
  }
}
