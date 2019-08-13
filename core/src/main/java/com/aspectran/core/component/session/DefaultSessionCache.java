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
package com.aspectran.core.component.session;

import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import com.aspectran.core.util.statistic.CounterStatistic;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of {@code SessionCache}.
 *
 * <p>Created: 2017. 6. 24.</p>
 */
public class DefaultSessionCache extends AbstractSessionCache {

    private static final Log log = LogFactory.getLog(DefaultSessionCache.class);

    /** the cache of sessions in a HashMap */
    private final ConcurrentHashMap<String, BasicSession> sessions = new ConcurrentHashMap<>();

    private final CounterStatistic statistics = new CounterStatistic();

    private final AtomicLong expiredSessionCount = new AtomicLong();

    private final AtomicLong rejectedSessionCount = new AtomicLong();

    private volatile int maxSessions;

    public DefaultSessionCache(SessionHandler sessionHandler) {
        super(sessionHandler);
    }

    @Override
    public int getMaxSessions() {
        return maxSessions;
    }

    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    @Override
    public BasicSession doGet(String id) {
        if (id == null) {
            return null;
        }
        return sessions.get(id);
    }

    @Override
    public BasicSession doPutIfAbsent(String id, BasicSession session) {
        checkMaxSessions();
        BasicSession s = sessions.putIfAbsent(id, session);
        if (s == null && !(session instanceof PlaceHolderSession)) {
            statistics.increment();
        }
        return s;
    }

    @Override
    public BasicSession doDelete(String id) {
        BasicSession s = sessions.remove(id);
        if (s != null && !(s instanceof PlaceHolderSession)) {
            statistics.decrement();
            expiredSessionCount.incrementAndGet();
        }
        return s;
    }

    @Override
    public boolean doReplace(String id, BasicSession oldValue, BasicSession newValue) {
        checkMaxSessions();
        boolean result = sessions.replace(id, oldValue, newValue);
        if (result && oldValue instanceof PlaceHolderSession) {
            statistics.increment();
        }
        return result;
    }

    @Override
    public Set<String> getAllSessions() {
        return sessions.keySet();
    }

    @Override
    public long getActiveSessionCount() {
        return statistics.getCurrent();
    }

    @Override
    public long getHighestSessionCount() {
        return statistics.getMax();
    }

    @Override
    public long getCreatedSessionCount() {
        return statistics.getTotal();
    }

    @Override
    public long getExpiredSessionCount() {
        return expiredSessionCount.get();
    }

    @Override
    public long getRejectedSessionCount() {
        return rejectedSessionCount.get();
    }

    @Override
    public void resetStatistics() {
        statistics.reset();
        expiredSessionCount.set(0L);
        rejectedSessionCount.set(0L);
    }

    @Override
    public void clear() {
        // loop over all the sessions in memory (a few times if necessary to catch sessions that have been
        // added while we're running
        int loop = 100;
        while (!sessions.isEmpty() && loop-- >= 0) {
            for (BasicSession session : sessions.values()) {
                // if we have a backing store so give the session to it to write out if necessary
                if (getSessionDataStore() != null) {
                    session.willPassivate();
                    try {
                        getSessionDataStore().store(session.getId(), session.getSessionData());
                    } catch (Exception e) {
                        log.warn("Failed to save session data", e);
                    }
                    doDelete(session.getId()); // remove from memory
                } else {
                    // not preserving sessions on exit
                    try {
                        session.invalidate();
                        session.setDestroyedReason(Session.DestroyedReason.UNDEPLOY);
                    } catch (Exception e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Session invalidation failed, but ignored", e);
                        }
                    }
                }
            }
        }
    }

    private void checkMaxSessions() {
        if (maxSessions > 0 && statistics.getCurrent() > maxSessions) {
            rejectedSessionCount.incrementAndGet();
            throw new IllegalStateException("Session was rejected as the maximum number of sessions " +
                    maxSessions + " has been hit");
        }
    }

}
