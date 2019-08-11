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

import java.util.Collections;
import java.util.Enumeration;

/**
 * Session processing delegate by request.
 *
 * <p>Created: 2017. 9. 10.</p>
 */
public class SessionAgent {

    private final SessionHandler sessionHandler;

    private volatile Session session;

    public SessionAgent(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    public String getId() {
        return getSession(true).getId();
    }

    public long getCreationTime() {
        return getSession(true).getCreationTime();
    }

    public long getLastAccessedTime() {
        return getSession(true).getLastAccessedTime();
    }

    public int getMaxInactiveInterval() {
        return getSession(true).getMaxInactiveInterval();
    }

    public void setMaxInactiveInterval(int secs) {
        getSession(true).setMaxInactiveInterval(secs);
    }

    public Enumeration<String> getAttributeNames() {
        Session session = getSession(false);
        if (session == null) {
            return null;
        }
        return Collections.enumeration(session.getAttributeNames());
    }

    public <T> T getAttribute(String name) {
        Session session = getSession(false);
        if (session == null) {
            return null;
        }
        return session.getAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        getSession(true).setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        Session session = getSession(false);
        if (session != null) {
            session.removeAttribute(name);
        }
    }

    public Session getSession(boolean create) {
        if (session != null) {
            if (session.isValid()) {
                return session;
            }
            session = null;
        }
        if (!create) {
            return null;
        }
        String id = sessionHandler.createSessionId(hashCode());
        session = sessionHandler.createSession(id);
        return session;
    }

    public void invalidate() {
        Session session = getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public boolean isNew() {
        Session session = getSession(false);
        if (session == null) {
            return true;
        }
        return session.isNew();
    }

    /**
     * Called when a session is first accessed by an {@code Activity}.
     */
    public void access() {
        Session session = getSession(false);
        if (session != null) {
            session.access();
        }
    }

    /**
     * Called when a session is last accessed by an {@code Activity}.
     */
    public void complete() {
        Session session = getSession(false);
        if (session != null) {
            session.complete();
        }
    }

}
