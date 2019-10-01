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

import com.aspectran.core.component.Component;

import java.util.Set;

/**
 * A SessionDataStore is a mechanism for (persistently) storing data associated with sessions.
 *
 * <p>Created: 2017. 6. 15.</p>
 */
public interface SessionDataStore extends Component {

    /**
     * Read in session data.
     *
     * @param id identity of session to load
     * @return the SessionData matching the id
     * @throws Exception if unable to load session data
     */
    SessionData load(String id) throws Exception;

    /**
     * Store the session data.
     *
     * @param id identity of session to store
     * @param sessionData info of session to store
     * @throws Exception if unable to write session data
     */
    void store(String id, SessionData sessionData) throws Exception;

    /**
     * Delete session data.
     *
     * @param id identity of session to delete
     * @return true if the session was deleted
     * @throws Exception if unable to delete session data
     */
    boolean delete(String id) throws Exception;

    /**
     * Test if data exists for a given session id.
     *
     * @param id Identity of session whose existence should be checked
     * @return true if valid, non-expired session exists
     * @throws Exception if there is a problem checking the existence with persistence layer
     */
    boolean exists(String id) throws Exception;

    /**
     * Called periodically, this method should search the data store
     * for sessions that have been expired for a 'reasonable' amount
     * of time.
     *
     * @param candidates if provided, these are keys of sessions that
     *      the SessionDataStore thinks has expired and should be verified by the
     *      SessionDataStore
     * @return set of session ids
     */
    Set<String> getExpired (Set<String> candidates);

    /**
     * Create a new SessionData.
     *
     * @param id the id
     * @param createdTime the timestamp when created
     * @param accessedTime the timestamp when accessed
     * @param lastAccessedTime the timestamp when last accessed
     * @param maxInactiveInterval the max inactive time in milliseconds
     * @return a new SessionData object
     */
    SessionData createSessionData(String id, long createdTime, long accessedTime, long lastAccessedTime, long maxInactiveInterval);

    /**
     * Returns the names of the attributes that should be excluded from serialization.
     *
     * @return the attribute names to be excluded from serialization
     */
    Set<String> getExcludeAttrsFromSerialization();

}
