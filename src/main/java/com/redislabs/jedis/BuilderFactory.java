package com.redislabs.jedis;

import com.redislabs.jedis.util.SafeEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BuilderFactory {

  public static final Builder<Object> ENCODED_OBJECT = new Builder<Object>() {
    @Override
    public Object build(Object data) {
      return SafeEncoder.encodeObject(data);
    }

    @Override
    public String toString() {
      return "Object";
    }
  };

  public static final Builder<List<Object>> ENCODED_OBJECT_LIST = new Builder<List<Object>>() {
    @Override
    public List<Object> build(Object data) {
      return (List<Object>) SafeEncoder.encodeObject(data);
    }

    @Override
    public String toString() {
      return "List<Object>";
    }
  };

  public static final Builder<Long> LONG = new Builder<Long>() {
    @Override
    public Long build(Object data) {
      return (Long) data;
    }

    @Override
    public String toString() {
      return "long"; // TODO: Long
    }

  };

  public static final Builder<List<Long>> LONG_LIST = new Builder<List<Long>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<Long> build(Object data) {
      if (null == data) {
        return null;
      }
      return (List<Long>) data;
    }

    @Override
    public String toString() {
      return "List<Long>";
    }

  };

  public static final Builder<Double> DOUBLE = new Builder<Double>() {
    @Override
    public Double build(Object data) {
      String string = STRING.build(data);
      if (string == null) return null;
      try {
        return Double.valueOf(string);
      } catch (NumberFormatException e) {
        if (string.equals("inf") || string.equals("+inf")) return Double.POSITIVE_INFINITY;
        if (string.equals("-inf")) return Double.NEGATIVE_INFINITY;
        throw e;
      }
    }

    @Override
    public String toString() {
      return "double"; // TODO: Double
    }
  };

  public static final Builder<List<Double>> DOUBLE_LIST = new Builder<List<Double>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<Double> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> values = (List<byte[]>) data;
      List<Double> doubles = new ArrayList<>(values.size());
      for (byte[] value : values) {
        doubles.add(DOUBLE.build(value));
      }
      return doubles;
    }

    @Override
    public String toString() {
      return "List<Double>";
    }
  };

  public static final Builder<Boolean> BOOLEAN = new Builder<Boolean>() {
    @Override
    public Boolean build(Object data) {
      return ((Long) data) == 1L;
    }

    @Override
    public String toString() {
      return "boolean"; // Boolean?
    }
  };

  public static final Builder<List<Boolean>> BOOLEAN_LIST = new Builder<List<Boolean>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<Boolean> build(Object data) {
      if (null == data) {
        return null;
      }
      List<Long> longs = (List<Long>) data;
      List<Boolean> booleans = new ArrayList<>(longs.size());
      for (Long value : longs) {
        booleans.add(value == 1L);
      }
      return booleans;
    }

    @Override
    public String toString() {
      return "List<Boolean>";
    }
  };

  public static final Builder<String> STRING = new Builder<String>() {
    @Override
    public String build(Object data) {
      return data == null ? null : SafeEncoder.encode((byte[]) data);
    }

    @Override
    public String toString() {
      return "string"; // TODO: String
    }

  };

  public static final Builder<List<String>> STRING_LIST = new Builder<List<String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public List<String> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final ArrayList<String> result = new ArrayList<>(l.size());
      for (final byte[] barray : l) {
        if (barray == null) {
          result.add(null);
        } else {
          result.add(SafeEncoder.encode(barray));
        }
      }
      return result;
    }

    @Override
    public String toString() {
      return "List<String>";
    }

  };

  public static final Builder<Set<String>> STRING_SET = new Builder<Set<String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> build(Object data) {
      if (null == data) {
        return null;
      }
      List<byte[]> l = (List<byte[]>) data;
      final Set<String> result = new HashSet<>(l.size(), 1);
      for (final byte[] barray : l) {
        if (barray == null) {
          result.add(null);
        } else {
          result.add(SafeEncoder.encode(barray));
        }
      }
      return result;
    }

    @Override
    public String toString() {
      return "Set<String>";
    }

  };

  public static final Builder<Map<String, String>> STRING_MAP = new Builder<Map<String, String>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> build(Object data) {
      final List<byte[]> flatHash = (List<byte[]>) data;
      final Map<String, String> hash = new HashMap<>(flatHash.size() / 2, 1);
      final Iterator<byte[]> iterator = flatHash.iterator();
      while (iterator.hasNext()) {
        hash.put(SafeEncoder.encode(iterator.next()), SafeEncoder.encode(iterator.next()));
      }

      return hash;
    }

    @Override
    public String toString() {
      return "Map<String, String>";
    }

  };

}
