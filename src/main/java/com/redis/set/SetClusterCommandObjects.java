package com.redis.set;

import com.redis.jedis.ClusterCommandArguments;
import com.redis.jedis.commands.ProtocolCommand;

public class SetClusterCommandObjects extends SetCommandObjects {

  @Override
  protected ClusterCommandArguments commandArguments(ProtocolCommand command) {
    return new ClusterCommandArguments(command);
  }
}
