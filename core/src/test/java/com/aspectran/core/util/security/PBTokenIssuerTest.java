/*
 * Copyright (c) 2008-2020 The Aspectran Project
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
package com.aspectran.core.util.security;

import com.aspectran.core.util.PBEncryptionUtils;
import com.aspectran.core.util.apon.Parameters;
import com.aspectran.core.util.apon.VariableParameters;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>Created: 2019/11/25</p>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PBTokenIssuerTest {

    private String oldPassword;

    private String oldAlgorithm;

    @BeforeAll
    void saveProperties() {
        oldPassword = System.getProperty(PBEncryptionUtils.ENCRYPTION_PASSWORD_KEY);
        oldAlgorithm = System.getProperty(PBEncryptionUtils.ENCRYPTION_ALGORITHM_KEY);
        System.setProperty(PBEncryptionUtils.ENCRYPTION_PASSWORD_KEY, "encryption-password-for-test");
    }

    @AfterAll
    void restoreProperties() {
        if (oldPassword == null) {
            System.clearProperty(PBEncryptionUtils.ENCRYPTION_PASSWORD_KEY);
        } else {
            System.setProperty(PBEncryptionUtils.ENCRYPTION_PASSWORD_KEY, oldPassword);
        }
        if (oldAlgorithm == null) {
            System.clearProperty(PBEncryptionUtils.ENCRYPTION_ALGORITHM_KEY);
        } else {
            System.setProperty(PBEncryptionUtils.ENCRYPTION_ALGORITHM_KEY, oldAlgorithm);
        }
    }

    @Test
    void testPBToken() throws InvalidPBTokenException {
        System.setProperty(PBEncryptionUtils.ENCRYPTION_PASSWORD_KEY, "encryption-password-for-test");

        Parameters params = new VariableParameters();
        params.putValue("p1", "v1");
        params.putValue("p2", "v2");
        params.putValue("p3", "v3");
        String token = PBTokenIssuer.getToken(params);
        Parameters params2 = PBTokenIssuer.getPayload(token);
        assertEquals(params.toString(), params2.toString());
    }

}
