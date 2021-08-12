package com.redis.jedis.util;

import com.redis.jedis.DefaultJedisClientConfig;
import com.redis.jedis.HostAndPort;
import com.redis.jedis.JedisConnection;
import com.redis.jedis.Protocol;

public class JedisClusterUtil {

  public static final int HASHSLOTS = 16384;

  private static final String LOCAL_IP = "127.0.0.1";

  private static final HostAndPort nodeInfo1 = HostAndPortUtil.getClusterServers().get(0);
  private static final HostAndPort nodeInfo2 = HostAndPortUtil.getClusterServers().get(1);
  private static final HostAndPort nodeInfo3 = HostAndPortUtil.getClusterServers().get(2);

  private static JedisConnection node1;
  private static JedisConnection node2;
  private static JedisConnection node3;

  public static void setUp() throws InterruptedException {
    node1 = new JedisConnection(nodeInfo1, DefaultJedisClientConfig.builder().password("cluster").build());
    node2 = new JedisConnection(nodeInfo2, DefaultJedisClientConfig.builder().password("cluster").build());
    node3 = new JedisConnection(nodeInfo3, DefaultJedisClientConfig.builder().password("cluster").build());

    // add nodes to cluster
    clusterMeet(node1, LOCAL_IP, nodeInfo2.getPort());
    clusterMeet(node1, LOCAL_IP, nodeInfo3.getPort());

    // split available slots across the three nodes
    int slotsPerNode = HASHSLOTS / 3;
    int[] node1Slots = new int[slotsPerNode];
    int[] node2Slots = new int[slotsPerNode + 1];
    int[] node3Slots = new int[slotsPerNode];
    for (int i = 0, slot1 = 0, slot2 = 0, slot3 = 0; i < HASHSLOTS; i++) {
      if (i < slotsPerNode) {
        node1Slots[slot1++] = i;
      } else if (i > slotsPerNode * 2) {
        node3Slots[slot3++] = i;
      } else {
        node2Slots[slot2++] = i;
      }
    }

    clusterAddSlots(node1, node1Slots);
    clusterAddSlots(node2, node2Slots);
    clusterAddSlots(node3, node3Slots);

    waitForClusterReady(node1, node2, node3);
  }

  public static void cleanUp() {
    clusterResetSoft(node1);
    clusterResetSoft(node2);
    clusterResetSoft(node3);
  }

  private static void waitForClusterReady(JedisConnection... nodes) throws InterruptedException {
    boolean clusterOk = false;
    while (!clusterOk) {
      boolean isOk = true;
      for (JedisConnection node : nodes) {
        if (!clusterInfo(node).split("\n")[0].contains("ok")) {
          isOk = false;
          break;
        }
      }

      if (isOk) {
        clusterOk = true;
      }

      Thread.sleep(50);
    }
  }

  private static void clusterMeet(JedisConnection jedis, String ip, int port) {
    byte[][] args = new byte[][]{SafeEncoder.encode("MEET"), SafeEncoder.encode(ip), Protocol.toByteArray(port)};
    jedis.sendCommand(Protocol.Command.CLUSTER, args);
    jedis.getOne();
  }

  private static void clusterAddSlots(JedisConnection jedis, int... slots) {
    byte[][] args = new byte[1 + slots.length][];
    int index = 0;
    args[index++] = SafeEncoder.encode("ADDSLOTS");
    for (int slot : slots) {
      args[index++] = Protocol.toByteArray(slot);
    }
    jedis.sendCommand(Protocol.Command.CLUSTER, args);
    jedis.getOne();
  }

  private static String clusterInfo(JedisConnection jedis) {
    jedis.sendCommand(Protocol.Command.CLUSTER, "INFO");
    return jedis.getBulkReply();
  }

  private static void clusterResetSoft(JedisConnection jedis) {
    jedis.sendCommand(Protocol.Command.CLUSTER, "RESET", "SOFT");
    jedis.getOne();
  }
}
