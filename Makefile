PATH := ./redis-git/src:${PATH}
STUNNEL_BIN := $(shell which stunnel)

define REDIS0_CONF
daemonize yes
protected-mode no
port 6379
user acljedis on allcommands allkeys >fizzbuzz
pidfile /tmp/redis0.pid
logfile /tmp/redis0.log
save ""
appendonly no
client-output-buffer-limit pubsub 256k 128k 5
endef

define REDIS1_CONF
daemonize yes
protected-mode no
port 6380
requirepass foobared
pidfile /tmp/redis1.pid
logfile /tmp/redis1.log
save ""
appendonly no
endef

#STUNNEL
define STUNNEL_CONF
cert = src/test/resources/private.pem
pid = /tmp/stunnel.pid
[redis_0]
accept = 127.0.0.1:6479
connect = 127.0.0.1:6379
endef

export REDIS0_CONF
export REDIS1_CONF

export STUNNEL_CONF
export STUNNEL_BIN

ifndef STUNNEL_BIN
    SKIP_SSL := !SSL*,
endif
export SKIP_SSL

start: stunnel cleanup
	echo "$$REDIS0_CONF" | redis-server -
	echo "$$REDIS1_CONF" | redis-server -

cleanup:
	- rm -vf /tmp/redis_cluster_node*.conf 2>/dev/null
	- rm dump.rdb appendonly.aof - 2>/dev/null

stunnel:
	@if [ -e "$$STUNNEL_BIN" ]; then\
	    echo "$$STUNNEL_CONF" | stunnel -fd 0;\
	fi

stop:
	kill `cat /tmp/redis0.pid`
	kill `cat /tmp/redis1.pid`

test: compile-module start
	sleep 2
	mvn -Dtest=${SKIP_SSL}${TEST} clean compile test
	make stop

package: start
	mvn clean package
	make stop

deploy: start
	mvn clean deploy
	make stop

format:
	mvn java-formatter:format

release:
	make start
	mvn release:clean
	mvn release:prepare
	mvn release:perform -DskipTests
	make stop

circleci-install:
	sudo apt-get install -y gcc-8 g++-8
	cd /usr/bin ;\
	sudo ln -sf gcc-8 gcc ;\
	sudo ln -sf g++-8 g++
	[ ! -e redis-git ] && git clone https://github.com/redis/redis.git --branch unstable --single-branch redis-git || true
	$(MAKE) -C redis-git clean
	$(MAKE) -C redis-git	

compile-module:
	gcc -shared -o /tmp/testmodule.so -fPIC src/test/resources/testmodule.c

.PHONY: test
