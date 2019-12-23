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
package com.aspectran.core.component.session.redis.lettuce.cluster;

import com.aspectran.core.component.session.SessionData;
import com.aspectran.core.component.session.redis.lettuce.ConnectionPool;
import com.aspectran.core.component.session.redis.lettuce.SessionDataCodec;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.Arrays;

/**
 * Redis cluster connection pool based on Lettuce.
 *
 * <p>Created: 2019/12/08</p>
 */
public class RedisClusterConnectionPool implements ConnectionPool<StatefulRedisClusterConnection<String, SessionData>> {

    private final RedisClusterConnectionPoolConfig poolConfig;

    private RedisClusterClient client;

    private GenericObjectPool<StatefulRedisClusterConnection<String, SessionData>> pool;

    public RedisClusterConnectionPool(RedisClusterConnectionPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public StatefulRedisClusterConnection<String, SessionData> getConnection() throws Exception {
        if (pool == null) {
            throw new IllegalStateException("RedisClusterConnectionPool is not initialized");
        }
        return pool.borrowObject();
    }

    @Override
    public void initialize(SessionDataCodec codec) {
        if (client != null) {
            throw new IllegalStateException("RedisClusterConnectionPool is already initialized");
        }
        client = RedisClusterClient.create(Arrays.asList(poolConfig.getRedisURIs()));
        if (poolConfig.getClusterClientOptions() != null) {
            client.setOptions(poolConfig.getClusterClientOptions());
        }
        pool = ConnectionPoolSupport
                .createGenericObjectPool(()
                        -> client.connect(codec), poolConfig);
    }

    @Override
    public void destroy() {
        if (pool != null) {
            pool.close();
        }
        if (client != null) {
            client.shutdown();
        }
    }

}
