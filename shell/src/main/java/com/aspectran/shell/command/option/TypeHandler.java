/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.shell.command.option;

import com.aspectran.core.util.ClassUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * This is a temporary implementation. TypeHandler will handle the
 * pluggableness of OptionTypes and it will direct all of these types
 * of conversion functionalities to ConvertUtils component in Commons
 * already. BeanUtils I think.
 */
public class TypeHandler {

    /**
     * Returns the {@code Object} of type {@code obj}
     * with the value of {@code str}.
     *
     * @param str the command line value
     * @param obj the type of argument
     * @return the instance of {@code obj} initialised with
     *      the value of {@code str}.
     * @throws OptionParseException if the value creation for the given object type failed
     */
    public static Object createValue(String str, Object obj) throws OptionParseException {
        return createValue(str, (Class<?>)obj);
    }

    /**
     * Returns the {@code Object} of type {@code clazz}
     * with the value of {@code str}.
     *
     * @param str the command line value
     * @param clazz the type of argument
     * @return the instance of {@code clazz} initialised with
     *      the value of {@code str}.
     * @throws OptionParseException if the value creation for the given class failed
     */
    @SuppressWarnings("unchecked") // returned value will have type T because it is fixed by clazz
    public static <T> T createValue(final String str, final Class<T> clazz) throws OptionParseException {
        if (PatternOptionBuilder.STRING_VALUE == clazz)
        {
            return (T) str;
        }
        else if (PatternOptionBuilder.OBJECT_VALUE == clazz)
        {
            return (T) createObject(str);
        }
        else if (PatternOptionBuilder.NUMBER_VALUE == clazz)
        {
            return (T) createNumber(str);
        }
        else if (PatternOptionBuilder.DATE_VALUE == clazz)
        {
            return (T) createDate(str);
        }
        else if (PatternOptionBuilder.CLASS_VALUE == clazz)
        {
            return (T) createClass(str);
        }
        else if (PatternOptionBuilder.FILE_VALUE == clazz)
        {
            return (T) createFile(str);
        }
        else if (PatternOptionBuilder.EXISTING_FILE_VALUE == clazz)
        {
            return (T) openFile(str);
        }
        else if (PatternOptionBuilder.FILES_VALUE == clazz)
        {
            return (T) createFiles(str);
        }
        else if (PatternOptionBuilder.URL_VALUE == clazz)
        {
            return (T) createURL(str);
        }
        else
        {
            return null;
        }
    }

    /**
      * Creates an instance with the specified class name and an empty constructor.
      *
      * @param className the class name
      * @return the initialised object
      * @throws OptionParseException if the class could not be found or the object could not be created
      */
    public static Object createObject(String className) throws OptionParseException {
        Class<?> cl;
        try {
            cl = Class.forName(className);
        } catch (ClassNotFoundException cnfe) {
            throw new OptionParseException("Unable to find the class: " + className);
        }
        try {
            return ClassUtils.createInstance(cl);
        } catch (final Exception e) {
            throw new OptionParseException(e.getClass().getName() + "; Unable to create an instance of: " + className);
        }
    }

    /**
     * Create a number from a String. If a . is present, it creates a
     * Double, otherwise a Long.
     *
     * @param str the value
     * @return the number represented by {@code str}
     * @throws OptionParseException if {@code str} is not a number
     */
    public static Number createNumber(String str) throws OptionParseException {
        try {
            if (str.indexOf('.') != -1) {
                return Double.valueOf(str);
            }
            return Long.valueOf(str);
        } catch (final NumberFormatException e) {
            throw new OptionParseException(e.getMessage());
        }
    }

    /**
     * Returns the class whose name is {@code classname}.
     *
     * @param classname the class name
     * @return the class if it is found
     * @throws OptionParseException if the class could not be found
     */
    public static Class<?> createClass(final String classname) throws OptionParseException {
        try {
            return Class.forName(classname);
        } catch (final ClassNotFoundException e) {
            throw new OptionParseException("Unable to find the class: " + classname);
        }
    }

    /**
     * Returns the date represented by {@code str}.
     * <p>
     * This method is not yet implemented and always throws an
     * {@link UnsupportedOperationException}.</p>
     *
     * @param str the date string
     * @return The date if {@code str} is a valid date string,
     * otherwise return null.
     * @throws UnsupportedOperationException always
     */
    public static Date createDate(String str) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns the URL represented by {@code str}.
     *
     * @param str the URL string
     * @return The URL in {@code str} is well-formed
     * @throws OptionParseException if the URL in {@code str} is not well-formed
     */
    public static URL createURL(String str) throws OptionParseException {
        try {
            return new URL(str);
        } catch (final MalformedURLException e) {
            throw new OptionParseException("Unable to parse the URL: " + str);
        }
    }

    /**
     * Returns the File represented by {@code str}.
     *
     * @param str the File location
     * @return the file represented by {@code str}.
     */
    public static File createFile(final String str) {
        return new File(str);
    }

    /**
     * Returns the opened FileInputStream represented by {@code str}.
     *
     * @param str the file location
     * @return the file input stream represented by {@code str}
     * @throws OptionParseException if the file is not exist or not readable
     */
    public static FileInputStream openFile(String str) throws OptionParseException {
        try {
            return new FileInputStream(str);
        } catch (FileNotFoundException e) {
            throw new OptionParseException("Unable to find file: " + str);
        }
    }

    /**
     * Returns the File[] represented by {@code str}.
     * <p>
     * This method is not yet implemented and always throws an
     * {@link UnsupportedOperationException}.
     *
     * @param str the paths to the files
     * @return The File[] represented by {@code str}.
     * @throws UnsupportedOperationException always
     */
    public static File[] createFiles(final String str) {
        // to implement/port:
        //        return FileW.findFiles(str);
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
