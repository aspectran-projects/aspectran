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
package com.aspectran.core.adapter;

import com.aspectran.core.component.bean.scope.ApplicationScope;
import com.aspectran.core.context.builder.resource.AspectranClassLoader;
import com.aspectran.core.util.ResourceUtils;
import com.aspectran.core.util.ToStringBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * The Class AbstractApplicationAdapter.
 *
 * @since 2011. 3. 13.
*/
public abstract class AbstractApplicationAdapter implements ApplicationAdapter {

    protected final Object adaptee;

    protected final ApplicationScope scope;

    protected ClassLoader classLoader;

    protected String basePath;

    /**
     * Instantiates a new AbstractApplicationAdapter.
     *
     * @param adaptee the adaptee object
     */
    public AbstractApplicationAdapter(Object adaptee) {
        this.adaptee = adaptee;
        this.scope = new ApplicationScope();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdaptee() {
        return (T)adaptee;
    }

    @Override
    public ApplicationScope getApplicationScope() {
        return scope;
    }

    @Override
    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            return AspectranClassLoader.getDefaultClassLoader();
        } else {
            return classLoader;
        }
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String getBasePath() {
        return basePath;
    }

    /**
     * Sets the application base path.
     *
     * @param basePath the new application base path
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String toRealPath(String filePath) throws IOException {
        File file = toRealPathAsFile(filePath);
        return file.getCanonicalPath();
    }

    @Override
    public File toRealPathAsFile(String filePath) throws IOException {
        File file;
        if (filePath.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
            // Using url fully qualified paths
            URI uri = URI.create(filePath);
            file = new File(uri);
        } else if (filePath.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
            // Using classpath relative resources
            URL url = getClassLoader().getResource(filePath);
            if (url == null) {
                throw new IOException("Could not find the resource with the given name: " + filePath);
            }
            file = new File(url.getFile());
        } else {
            if (basePath != null) {
                file = new File(basePath, filePath);
            } else {
                file = new File(filePath);
            }
        }
        return file;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder();
        tsb.append("basePath", basePath);
        tsb.append("classLoader", getClassLoader());
        tsb.append("adaptee", adaptee);
        return tsb.toString();
    }

}