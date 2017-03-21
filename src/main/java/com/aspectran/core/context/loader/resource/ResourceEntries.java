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
package com.aspectran.core.context.loader.resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.jar.JarEntry;

import com.aspectran.core.util.ResourceUtils;
import com.aspectran.core.util.StringUtils;

/**
 * The Class ResourceEntries.
 * 
 * <p>Created: 2014. 12. 24 PM 4:54:13</p>
 */
public class ResourceEntries extends LinkedHashMap<String, URL> {

    /** @serial */
    private static final long serialVersionUID = -6936820061673430782L;

    public ResourceEntries() {
        super();
    }

    public void putResource(String resourceName, File file) throws InvalidResourceException {
        URL url;

        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new InvalidResourceException("Invalid resource file: " + file, e);
        }

        put(resourceName, url);
    }

    public void putResource(File file, JarEntry entry) throws InvalidResourceException {
        String resourceName = entry.getName();
        URL url;

        try {
            //"jar:file:///C:/proj/parser/jar/parser.jar!/test.xml"
            url = new URL(ResourceUtils.JAR_URL_PREFIX + file.toURI() + ResourceUtils.JAR_URL_SEPARATOR + resourceName);
        } catch (MalformedURLException e) {
            throw new InvalidResourceException("Invalid resource jar file: " + file, e);
        }

        put(resourceName, url);
    }

    @Override
    public URL put(String resourceName, URL url) {
        if (resourceName.indexOf('\\') != -1) {
            resourceName = resourceName.replace('\\', ResourceUtils.PATH_SPEPARATOR_CHAR);
        }

        if (StringUtils.endsWith(resourceName, ResourceUtils.PATH_SPEPARATOR_CHAR)) {
            resourceName = resourceName.substring(0, resourceName.length() - 1);
        }

        return super.put(resourceName, url);
    }

}
