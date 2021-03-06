# JedisX

Branch Progression
---

- main
    - attempt-1
        - provider-1
            - provider-2
                - multi-pipe-1
                    - args-1
                        - args-2
                            - <strike>resp3-1</strike>
                            - retry-2

Branch Description
---

### attempt-1

The base of all operations is a connection class.
It is named `JedisSocketConnection` which contains only socket operations.
`JedisPreparedConnection` extending previous class is for Redis specific initializations (AUTH, etc).

### provider-1

A provider interface is used in `Jedis` constructors instead of connection class.
Able to extend this idea to managed connection provider, pooled connection provider.

A sample module implementation is done with `Hash` data structure.

### provider-2

Cluster is implemented. It needed to unify JedisSocketConnection and JedisPreparedConnection classes into single `JedisConnection` class.

Pipeline and Transaction for standalone instance is implemented.

*Giant* and *Umbrella* classes implemented.

A second sample module implementation is done with `Set` data structure; to show proper giant or umbrella implementation.

Giant variant of pipeline and transaction are implemented. No umbrella variant for these.

### multi-pipe-1

Cluster Pipeline is implemented. [Not very satisfied with the pattern.]

Reliable Transaction is implemented.

### args-1

Provider provides connection by arguments. [remains completion]

### args-2

Provider provides connection by arguments.

### retry-2

Retry-ability. [tests required]
