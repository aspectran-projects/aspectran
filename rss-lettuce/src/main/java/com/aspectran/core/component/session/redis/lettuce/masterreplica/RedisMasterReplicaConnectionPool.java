/*
 * Copyright (c) 2008-2019 The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.component.session.redis.lettuce.masterreplica;

import com.aspectran.core.component.session.SessionData;
import com.aspectran.core.component.session.redis.lettuce.AbstractConnectionPool;
import com.aspectran.core.component.session.redis.lettuce.ConnectionPool;
import com.aspectran.core.component.session.redis.lettuce.SessionDataCodec;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.Arrays;

/**
 * Redis Master-Replica connection pool using Lettuce.
 *
 * <p>Created: 2019/12/08</p>
 */
public class RedisMasterReplicaConnectionPool extends AbstractConnectionPool
        implements ConnectionPool<StatefulRedisConnection<String, SessionData>> {

    private final RedisMasterReplicaConnectionPoolConfig poolConfig;

    private RedisClient client;

    private GenericObjectPool<StatefulRedisConnection<String, SessionData>> pool;

    public RedisMasterReplicaConnectionPool(RedisMasterReplicaConnectionPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public StatefulRedisConnection<String, SessionData> getConnection() throws Exception {
        if (pool == null) {
            throw new IllegalStateException("RedisMasterReplicaConnectionPool is not initialized");
        }
        return pool.borrowObject();
    }

    public void initialize() {
        if (client != null) {
            throw new IllegalStateException("RedisMasterReplicaConnectionPool is already initialized");
        }
        client = RedisClient.create();
        if (poolConfig.getClientOptions() != null) {
            client.setOptions(poolConfig.getClientOptions());
        }
        RedisCodec<String, SessionData> codec = new SessionDataCodec(getNonPersistentAttributes());
        pool = ConnectionPoolSupport
                .createGenericObjectPool(() -> {
                    StatefulRedisMasterReplicaConnection<String, SessionData> connection =
                        MasterReplica.connect(client, codec, Arrays.asList(poolConfig.getRedisURIs()));
                    connection.setReadFrom(ReadFrom.MASTER_PREFERRED);
                    return connection;
                }, poolConfig);
    }

    public void destroy() {
        if (pool != null) {
            pool.close();
        }
        if (client != null) {
            client.shutdown();
        }
    }

}
