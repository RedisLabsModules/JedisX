package com.redis.jedis.util;

import com.redis.jedis.HostAndPort;
import com.redis.jedis.Protocol;
import java.util.ArrayList;
import java.util.List;

public final class HostAndPortUtil {
  private static List<HostAndPort> redisHostAndPortList = new ArrayList<>();
  private static List<HostAndPort> sentinelHostAndPortList = new ArrayList<>();
  private static List<HostAndPort> clusterHostAndPortList = new ArrayList<>();

  private HostAndPortUtil() {
    throw new InstantiationError("Must not instantiate this class");
  }

  static {
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT));
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT + 1));
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT + 2));
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT + 3));
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT + 4));
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT + 5));
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT + 6));
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT + 7));
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT + 8));
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT + 9));
    redisHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_PORT + 10));

    sentinelHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_SENTINEL_PORT));
    sentinelHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_SENTINEL_PORT + 1));
    sentinelHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_SENTINEL_PORT + 2));
    sentinelHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_SENTINEL_PORT + 3));
    sentinelHostAndPortList.add(new HostAndPort("localhost", Protocol.DEFAULT_SENTINEL_PORT + 4));

    clusterHostAndPortList.add(new HostAndPort("localhost", 7379));
    clusterHostAndPortList.add(new HostAndPort("localhost", 7380));
    clusterHostAndPortList.add(new HostAndPort("localhost", 7381));
    clusterHostAndPortList.add(new HostAndPort("localhost", 7382));
    clusterHostAndPortList.add(new HostAndPort("localhost", 7383));
    clusterHostAndPortList.add(new HostAndPort("localhost", 7384));
  }

  public static List<HostAndPort> parseHosts(String envHosts,
      List<HostAndPort> existingHostsAndPorts) {

    if (null != envHosts && 0 < envHosts.length()) {

      String[] hostDefs = envHosts.split(",");

      if (null != hostDefs && 2 <= hostDefs.length) {

        List<HostAndPort> envHostsAndPorts = new ArrayList<>(hostDefs.length);

        for (String hostDef : hostDefs) {

          HostAndPort hostPort = HostAndPort.from(hostDef);

          envHostsAndPorts.add(hostPort);
        }

        return envHostsAndPorts;
      }
    }

    return existingHostsAndPorts;
  }

  public static List<HostAndPort> getRedisServers() {
    return redisHostAndPortList;
  }

  public static List<HostAndPort> getSentinelServers() {
    return sentinelHostAndPortList;
  }

  public static List<HostAndPort> getClusterServers() {
    return clusterHostAndPortList;
  }
}