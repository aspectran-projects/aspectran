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

import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.config.SessionFileStoreConfig;
import com.aspectran.core.context.config.SessionManagerConfig;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.SystemUtils;

import java.io.File;

public class SessionDataStoreFactory {

    public static FileSessionDataStore createFileSessionDataStore(SessionManagerConfig sessionManagerConfig,
                                                                  ApplicationAdapter applicationAdapter) {
        FileSessionDataStore fileSessionDataStore = new FileSessionDataStore();
        if (sessionManagerConfig.hasExcludeAttrsFromSerialization()) {
            fileSessionDataStore.setExcludeAttrsFromSerialization(sessionManagerConfig.getExcludeAttrsFromSerialization());
        }
        SessionFileStoreConfig fileStoreConfig = sessionManagerConfig.getFileStoreConfig();
        if (fileStoreConfig != null) {
            String storeDir = fileStoreConfig.getStoreDir();
            if (StringUtils.hasText(storeDir)) {
                if (applicationAdapter != null) {
                    String basePath = applicationAdapter.getBasePath();
                    fileSessionDataStore.setStoreDir(new File(basePath, storeDir));
                } else {
                    fileSessionDataStore.setStoreDir(new File(storeDir));
                }
            } else {
                fileSessionDataStore.setStoreDir(new File(SystemUtils.getJavaIoTmpDir()));
            }
            boolean deleteUnrestorableFiles = fileStoreConfig.isDeleteUnrestorableFiles();
            if (deleteUnrestorableFiles) {
                fileSessionDataStore.setDeleteUnrestorableFiles(true);
            }
        }
        return fileSessionDataStore;
    }

}
