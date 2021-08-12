package com.redis.jedis;

public abstract class Builder<T> {

  public abstract T build(Object data);
}
