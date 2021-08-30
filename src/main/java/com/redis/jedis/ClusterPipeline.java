package com.redis.jedis;

import com.redis.jedis.commands.PipelineCommands;
import com.redis.jedis.providers.JedisClusterConnectionProvider;

public class ClusterPipeline extends MultiNodePipelineBase implements PipelineCommands {

  private final JedisClusterConnectionProvider provider;
  private final RedisCommandObjects commandObjects;

  public ClusterPipeline(JedisClusterConnectionProvider provider) {
    this.provider = provider;
    this.commandObjects = new RedisCommandObjects();
  }

  @Override
  protected JedisConnection getConnection(HostAndPort nodeKey) {
    return provider.getConnection(nodeKey);
  }

  @Override
  public Response<Long> del(String key) {
    return appendCommand(provider.getNodeKey(key), commandObjects.del(key));
  }

  @Override
  public Response<String> get(String key) {
    return appendCommand(provider.getNodeKey(key), commandObjects.get(key));
  }

  @Override
  public Response<String> set(String key, String value) {
    return appendCommand(provider.getNodeKey(key), commandObjects.set(key, value));
  }

}
