package com.redis.jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.redis.jedis.Resp2BuilderFactory.*;

public class Resp2Builders implements ResponseBuilders {

  @Override
  public Builder<Object> encodedObject() {
    return ENCODED_OBJECT;
  }

  @Override
  public Builder<List<Object>> encodedObjectList() {
    return ENCODED_OBJECT_LIST;
  }

  @Override
  public Builder<Long> integer() {
    return LONG;
  }

  @Override
  public Builder<List<Long>> integerList() {
    return LONG_LIST;
  }

  @Override
  public Builder<Double> floatingPoint() {
    return DOUBLE;
  }

  @Override
  public Builder<List<Double>> floatingPointList() {
    return DOUBLE_LIST;
  }

  @Override
  public Builder<Boolean> trueFalse() {
    return BOOLEAN;
  }

  @Override
  public Builder<List<Boolean>> trueFalseList() {
    return BOOLEAN_LIST;
  }

  @Override
  public Builder<String> string() {
    return STRING;
  }

  @Override
  public Builder<List<String>> stringList() {
    return STRING_LIST;
  }

  @Override
  public Builder<Set<String>> stringSet() {
    return STRING_SET;
  }

  @Override
  public Builder<Map<String, String>> stringMap() {
    return STRING_MAP;
  }

}
