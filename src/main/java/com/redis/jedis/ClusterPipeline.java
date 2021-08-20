package com.redis.jedis;

import com.redis.jedis.commands.PipelineCommands;
import com.redis.jedis.providers.JedisClusterConnectionProvider;

public class ClusterPipeline extends MultiNodePipelineBase implements PipelineCommands {

  private final JedisClusterConnectionProvider provider;

  public ClusterPipeline(JedisClusterConnectionProvider provider) {
    this.provider = provider;
  }

  @Override
  protected JedisConnection getConnection(HostAndPort nodeKey) {
    return provider.getConnection(nodeKey);
  }

  @Override
  public Response<Long> del(String key) {
    return enqueResponse(provider.getNodeKey(key), BuilderFactory.LONG, Protocol.Command.DEL, key);
  }

  @Override
  public Response<String> get(String key) {
    return enqueResponse(provider.getNodeKey(key), BuilderFactory.STRING, Protocol.Command.GET, key);
  }

  @Override
  public Response<String> set(String key, String value) {
    return enqueResponse(provider.getNodeKey(key), BuilderFactory.STRING, Protocol.Command.SET, key, value);
  }

}
