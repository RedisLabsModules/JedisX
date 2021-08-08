package com.redislabs.jedis.providers;

import com.redislabs.jedis.JedisSocketConnection;
import com.redislabs.jedis.commands.ProtocolCommand;

public interface JedisConnectionProvider {
  
  JedisSocketConnection getConnection(ProtocolCommand command, int slot);

  void returnConnection(int slot, JedisSocketConnection conn);
}
