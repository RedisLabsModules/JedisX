package com.redis.jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ResponseBuilders {

  Builder<Object> encodedObject();

  Builder<List<Object>> encodedObjectList();

  Builder<Long> integer();

  Builder<List<Long>> integerList();

  Builder<Double> floatingPoint();

  Builder<List<Double>> floatingPointList();

  Builder<Boolean> trueFalse();

  Builder<List<Boolean>> trueFalseList();

  Builder<String> string();

  Builder<List<String>> stringList();

  Builder<Set<String>> stringSet();

  Builder<Map<String, String>> stringMap();
}
