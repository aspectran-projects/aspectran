/*
 * Copyright 2008-2017 Juho Jeong
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
import org.junit.Test;

import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Test cases for Session Handler.
 *
 * <p>Created: 2017. 9. 12.</p>
 */
public class SessionManagerTest {

    private Log log = LogFactory.getLog(getClass());

    @Test
    public void testShortLifecycle() throws Exception {
        SessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setGroupName("TEST1-");
        sessionManager.initialize();

        SessionAgent agent = sessionManager.newSessionAgent();

        log.info("Created Session: " + agent.getSession(true));

        agent.setAttribute("attr1", "val-1");
        agent.setAttribute("attr2", "val-2");
        agent.setAttribute("attr3", "val-3");

        Enumeration<String> enumeration = agent.getAttributeNames();
        while(enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            log.info("getAttribute " + key + "=" + agent.getAttribute(key));
        }

        agent.complete();
        agent.invalidate();
    }

    @Test
    public void testInactivityTimer() throws Exception {
        SessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setGroupName("TEST2-");
        sessionManager.initialize();

        SessionHandler sessionHandler = sessionManager.getSessionHandler();
        sessionHandler.setDefaultMaxIdleSecs(1);

        SessionAgent agent = sessionManager.newSessionAgent();

        log.info("Created Session: " + agent.getSession(true));

        await().atMost(2, TimeUnit.SECONDS).until(() -> sessionHandler.getSessionCache().getSessionsCurrent() == 0);
    }

}