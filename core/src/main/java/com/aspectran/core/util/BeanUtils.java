/*
 * Copyright (c) 2008-2017 The Aspectran Project
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
package com.aspectran.core.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * BeanUtils provides methods that allow simple, reflective access to
 * JavaBeans style properties.  Methods are provided for all simple types as
 * well as object types.
 * 
 * <p>Created: 2008. 04. 22 PM 3:47:15</p>
 */
public class BeanUtils {

    /** An empty immutable {@code Object} array. */
    private static final Object[] NO_ARGUMENTS = new Object[0];

    /**
     * Returns an array of the readable properties exposed by a bean
     *
     * @param object the bean
     * @return the properties
     */
    public static String[] getReadablePropertyNames(Object object) {
        return getBeanDescriptor(object.getClass()).getReadablePropertyNames();
    }

    /**
     * Returns an array of the writeable properties exposed by a bean
     *
     * @param object the bean
     * @return the properties
     */
    public static String[] getWriteablePropertyNames(Object object) {
        return getBeanDescriptor(object.getClass()).getWriteablePropertyNames();
    }

    /**
     * Returns the class that the setter expects to receive as a parameter when
     * setting a property value.
     *
     * @param object the bean to check
     * @param name the name of the property
     * @return the type of the property
     * @throws NoSuchMethodException the no such method exception
     */
    public static Class<?> getPropertyTypeForSetter(Object object, String name) throws NoSuchMethodException {
        Class<?> type = object.getClass();

        if (object instanceof Class<?>) {
            type = getClassPropertyTypeForSetter((Class<?>)object, name);
        } else if (object instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>)object;
            Object value = map.get(name);
            if (value == null) {
                type = Object.class;
            } else {
                type = value.getClass();
            }
        } else {
            if (name.contains(".")) {
                StringTokenizer parser = new StringTokenizer(name, "");
                while (parser.hasMoreTokens()) {
                    name = parser.nextToken();
                    type = getBeanDescriptor(type).getSetterType(name);
                }
            } else {
                type = getBeanDescriptor(type).getSetterType(name);
            }
        }

        return type;
    }

    /**
     * Returns the class that the getter will return when reading a property value.
     *
     * @param object the bean to check
     * @param name the name of the property
     * @return the type of the property
     * @throws NoSuchMethodException the no such method exception
     */
    public static Class<?> getPropertyTypeForGetter(Object object, String name) throws NoSuchMethodException {
        Class<?> type = object.getClass();

        if (object instanceof Class<?>) {
            type = getClassPropertyTypeForGetter((Class<?>)object, name);
        } else if (object instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>)object;
            Object value = map.get(name);
            if (value == null) {
                type = Object.class;
            } else {
                type = value.getClass();
            }
        } else {
            if (name.contains(".")) {
                StringTokenizer parser = new StringTokenizer(name, "");
                while (parser.hasMoreTokens()) {
                    name = parser.nextToken();
                    type = getBeanDescriptor(type).getGetterType(name);
                }
            } else {
                type = getBeanDescriptor(type).getGetterType(name);
            }
        }

        return type;
    }

    /**
     * Returns the class that the getter will return when reading a property value.
     *
     * @param type The class to check
     * @param name the name of the property
     * @return the type of the property
     * @throws NoSuchMethodException the no such method exception
     */
    public static Class<?> getClassPropertyTypeForGetter(Class<?> type, String name) throws NoSuchMethodException {
        if (name.contains(".")) {
            StringTokenizer parser = new StringTokenizer(name, "");
            while (parser.hasMoreTokens()) {
                name = parser.nextToken();
                type = getBeanDescriptor(type).getGetterType(name);
            }
        } else {
            type = getBeanDescriptor(type).getGetterType(name);
        }

        return type;
    }

    /**
     * Returns the class that the setter expects to receive as a parameter when
     * setting a property value.
     *
     * @param type The class to check
     * @param name the name of the property
     * @return the type of the property
     * @throws NoSuchMethodException the no such method exception
     */
    public static Class<?> getClassPropertyTypeForSetter(Class<?> type, String name) throws NoSuchMethodException {
        if (name.contains(".")) {
            StringTokenizer parser = new StringTokenizer(name, "");
            while (parser.hasMoreTokens()) {
                name = parser.nextToken();
                type = getBeanDescriptor(type).getSetterType(name);
            }
        } else {
            type = getBeanDescriptor(type).getSetterType(name);
        }

        return type;
    }

    /**
     * Invokes the static method of the specified class to get the bean property value.
     *
     * @param clazz the class for which to lookup
     * @param name the property name
     * @return the property value (as an Object)
     * @throws InvocationTargetException the invocation target exception
     */
    public static Object getObject(Class<?> clazz, String name) throws InvocationTargetException {
        try {
            BeanDescriptor cd = getBeanDescriptor(clazz);
            Method method = cd.getGetter(name);
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new IllegalArgumentException("Non-static method " + method + " in " + clazz);
            }
            try {
                return method.invoke(null, NO_ARGUMENTS);
            } catch (Throwable t) {
                throw unwrapThrowable(t);
            }
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Throwable t) {
            throw new InvocationTargetException(t, "Could not get property '" + name +
                    "' from " + clazz.getName() + ". Cause: " + t.toString());
        }
    }

    public static boolean hasStaticProperty(Class<?> clazz, String name) {
        try {
            BeanDescriptor cd = getBeanDescriptor(clazz);
            Method method = cd.getGetter(name);
            return Modifier.isStatic(method.getModifiers());
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Gets a property value from a static class.
     *
     * @param object the bean
     * @param name the property name
     * @return the property value (as an Object)
     * @throws InvocationTargetException the invocation target exception
     */
    public static Object getObject(Object object, String name) throws InvocationTargetException {
        if (name.contains(".")) {
            StringTokenizer parser = new StringTokenizer(name, "");
            Object value = object;
            while (parser.hasMoreTokens()) {
                value = getProperty(value, parser.nextToken());
                if (value == null) {
                    break;
                }
            }
            return value;
        } else {
            return getProperty(object, name);
        }
    }

    /**
     * Sets the value of a bean property to an Object.
     *
     * @param object the bean to change
     * @param name the name of the property to set
     * @param value the new value to set
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException the no such method exception
     */
    public static void setObject(Object object, String name, Object value) throws InvocationTargetException, NoSuchMethodException {
        if (name.contains(".")) {
            StringTokenizer parser = new StringTokenizer(name, "");
            String property = parser.nextToken();
            Object child = object;

            while (parser.hasMoreTokens()) {
                Class<?> type = getPropertyTypeForSetter(child, property);
                Object parent = child;
                child = getProperty(parent, property);

                if (child == null) {
                    if (value == null) {
                        return; // don't instantiate child path if value is null
                    } else {
                        try {
                            child = ClassUtils.createInstance(type);
                            setObject(parent, property, child);
                        } catch (Exception e) {
                            throw new InvocationTargetException(e, "Cannot set value of property '" + name
                                    + "' because '" + property + "' is null and cannot be instantiated on instance of "
                                    + type.getName() + ". Cause: " + e.toString());
                        }
                    }
                }

                property = parser.nextToken();
            }

            setProperty(child, property, value);
        } else {
            setProperty(object, name, value);
        }
    }

    /**
     * Checks to see if a bean has a writable property be a given name.
     *
     * @param object the bean to check
     * @param propertyName the property to check for
     * @return true if the property exists and is writable
     * @throws NoSuchMethodException the no such method exception
     */
    public static boolean hasWritableProperty(Object object, String propertyName) throws NoSuchMethodException {
        boolean hasProperty = false;

        if (object instanceof Map<?, ?>) {
            hasProperty = true; // ((Map)object).containsKey(propertyName);
        } else {
            if (propertyName.contains(".")) {
                StringTokenizer parser = new StringTokenizer(propertyName, "");
                Class<?> type = object.getClass();

                while (parser.hasMoreTokens()) {
                    propertyName = parser.nextToken();
                    type = getBeanDescriptor(type).getGetterType(propertyName);
                    hasProperty = getBeanDescriptor(type).hasWritableProperty(propertyName);
                }
            } else {
                hasProperty = getBeanDescriptor(object.getClass()).hasWritableProperty(propertyName);
            }
        }

        return hasProperty;
    }

    /**
     * Checks to see if a bean has a readable property be a given name.
     *
     * @param object the bean to check
     * @param propertyName the property to check for
     * @return true if the property exists and is readable
     * @throws NoSuchMethodException the no such method exception
     */
    public static boolean hasReadableProperty(Object object, String propertyName) throws NoSuchMethodException {
        boolean hasProperty = false;

        if (object instanceof Map<?, ?>) {
            hasProperty = true; // ((Map)object).containsKey(propertyName);
        } else {
            if (propertyName.contains(".")) {
                StringTokenizer parser = new StringTokenizer(propertyName, "");
                Class<?> type = object.getClass();

                while (parser.hasMoreTokens()) {
                    propertyName = parser.nextToken();
                    type = getBeanDescriptor(type).getGetterType(propertyName);
                    hasProperty = getBeanDescriptor(type).hasReadableProperty(propertyName);
                }
            } else {
                hasProperty = getBeanDescriptor(object.getClass()).hasReadableProperty(propertyName);
            }
        }

        return hasProperty;
    }

    private static Object getProperty(Object object, String name) throws InvocationTargetException {
        try {
            Object value;
            if (name.contains("[")) {
                value = getIndexedProperty(object, name);
            } else {
                if (object instanceof Map<?, ?>) {
                    int index = name.indexOf('.');
                    if (index > -1) {
                        String key = name.substring(0, index);
                        value = getProperty(((Map<?, ?>)object).get(key), name.substring(index + 1));
                    } else {
                        value = ((Map<?, ?>)object).get(name);
                    }
                } else {
                    int index = name.indexOf('.');
                    if (index > -1) {
                        String newName = name.substring(0, index);
                        value = getProperty(getObject(object, newName), name.substring(index + 1));
                    } else {
                        BeanDescriptor cd = getBeanDescriptor(object.getClass());
                        Method method = cd.getGetter(name);
                        try {
                            value = method.invoke(object, NO_ARGUMENTS);
                        } catch (Throwable t) {
                            throw unwrapThrowable(t);
                        }
                    }
                }
            }
            return value;
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Throwable t) {
            if (object == null) {
                throw new InvocationTargetException(t, "Could not get property '" + name +
                        "' from null reference. Cause: " + t.toString());
            } else {
                throw new InvocationTargetException(t, "Could not get property '" + name +
                        "' from " + object.getClass().getName() + ". Cause: " + t.toString());
            }
        }
    }

    private static void setProperty(Object object, String name, Object value) throws InvocationTargetException {
        try {
            if (name.contains("[")) {
                setIndexedProperty(object, name, value);
            } else {
                if (object instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>)object;
                    map.put(name, value);
                } else {
                    BeanDescriptor cd = getBeanDescriptor(object.getClass());
                    Method method = cd.getSetter(name);
                    Object[] params = new Object[] { value };
                    try {
                        method.invoke(object, params);
                    } catch (Throwable t) {
                        throw unwrapThrowable(t);
                    }
                }
            }
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Throwable t) {
            try {
                if (value != null) {
                    MethodUtils.invokeSetter(object, name, value);
                    return;
                }
            } catch (Throwable tt) {
                //ignore
            }

            if (object == null) {
                throw new InvocationTargetException(t, "Could not set property '" + name + "' to value '" +
                        value + "' for null reference. Cause: " + t.toString());
            } else {
                throw new InvocationTargetException(t, "Could not set property '" + name + "' to value '" + value +
                        "' for " + object.getClass().getName() + ". Cause: " + t.toString());
            }
        }
    }

    public static Object getIndexedProperty(Object object, String indexedName) throws InvocationTargetException {
        try {
            String name = indexedName.substring(0, indexedName.indexOf("["));
            int index = Integer.parseInt(indexedName.substring(indexedName.indexOf("[") + 1, indexedName.indexOf("]")));
            Object list;

            if (name.length() > 0) {
                list = getProperty(object, name);
            } else {
                list = object;
            }

            Object value;

            if (list instanceof List<?>) {
                value = ((List<?>)list).get(index);
            } else if (list instanceof Object[]) {
                value = ((Object[])list)[index];
            } else if (list instanceof char[]) {
                value = ((char[])list)[index];
            } else if (list instanceof boolean[]) {
                value = ((boolean[])list)[index];
            } else if (list instanceof byte[]) {
                value = ((byte[])list)[index];
            } else if (list instanceof double[]) {
                value = ((double[])list)[index];
            } else if (list instanceof float[]) {
                value = ((float[]) list)[index];
            } else if (list instanceof int[]) {
                value = ((int[])list)[index];
            } else if (list instanceof long[]) {
                value = ((long[])list)[index];
            } else if (list instanceof short[]) {
                value = ((short[])list)[index];
            } else {
                throw new IllegalArgumentException("The '" + name + "' property of the " +
                        object.getClass().getName() + " class is not a List or Array");
            }

            return value;
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e, "Error getting ordinal list from JavaBean. Cause: " + e);
        }
    }

    public static Class<?> getIndexedType(Object object, String indexedName) throws InvocationTargetException {
        try {
            String name = indexedName.substring(0, indexedName.indexOf("["));
            int i = Integer.parseInt(indexedName.substring(indexedName.indexOf("[") + 1, indexedName.indexOf("]")));
            Object list;

            if (name.length() > 0) {
                list = getProperty(object, name);
            } else {
                list = object;
            }

            Class<?> value;

            if (list instanceof List<?>) {
                value = ((List<?>)list).get(i).getClass();
            } else if (list instanceof Object[]) {
                value = ((Object[])list)[i].getClass();
            } else if (list instanceof char[]) {
                value = Character.class;
            } else if (list instanceof boolean[]) {
                value = Boolean.class;
            } else if (list instanceof byte[]) {
                value = Byte.class;
            } else if (list instanceof double[]) {
                value = Double.class;
            } else if (list instanceof float[]) {
                value = Float.class;
            } else if (list instanceof int[]) {
                value = Integer.class;
            } else if (list instanceof long[]) {
                value = Long.class;
            } else if (list instanceof short[]) {
                value = Short.class;
            } else {
                throw new IllegalArgumentException("The '" + name + "' property of the " +
                        object.getClass().getName() + " class is not a List or Array");
            }

            return value;
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e, "Error getting ordinal list from JavaBean. Cause: " + e);
        }
    }

    public static void setIndexedProperty(Object object, String indexedName, Object value) throws InvocationTargetException {
        try {
            String name = indexedName.substring(0, indexedName.indexOf("["));
            int index = Integer.parseInt(indexedName.substring(indexedName.indexOf("[") + 1, indexedName.indexOf("]")));
            Object list = getProperty(object, name);

            if (list instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<Object> l = (List<Object>)list;
                l.set(index, value);
            } else if (list instanceof Object[]) {
                ((Object[])list)[index] = value;
            } else if (list instanceof char[]) {
                ((char[])list)[index] = (Character)value;
            } else if (list instanceof boolean[]) {
                ((boolean[])list)[index] = (Boolean)value;
            } else if (list instanceof byte[]) {
                ((byte[])list)[index] = (Byte)value;
            } else if (list instanceof double[]) {
                ((double[])list)[index] = (Double)value;
            } else if (list instanceof float[]) {
                ((float[])list)[index] = (Float)value;
            } else if (list instanceof int[]) {
                ((int[])list)[index] = (Integer)value;
            } else if (list instanceof long[]) {
                ((long[])list)[index] = (Long)value;
            } else if (list instanceof short[]) {
                ((short[])list)[index] = (Short)value;
            } else {
                throw new IllegalArgumentException("The '" + name + "' property of the " +
                        object.getClass().getName() + " class is not a List or Array");
            }
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e, "Error getting ordinal value from JavaBean. Cause: " + e);
        }
    }

    /**
     * Examines a Throwable object and gets it's root cause
     *
     * @param t the exception to examine
     * @return the root cause
     */
    private static Throwable unwrapThrowable(Throwable t) {
        Throwable t2 = t;

        while (true) {
            if (t2 instanceof InvocationTargetException) {
                t2 = ((InvocationTargetException)t).getTargetException();
            } else if (t instanceof UndeclaredThrowableException) {
                t2 = ((UndeclaredThrowableException)t).getUndeclaredThrowable();
            } else {
                return t2;
            }
        }
    }

    /**
     * Gets an instance of BeanDescriptor for the specified class.
     *
     * @param clazz the class for which to lookup the ClassDescriptor cache.
     * @return the ClassDescriptor cache for the class
     */
    private static BeanDescriptor getBeanDescriptor(Class<?> clazz) {
        return BeanDescriptor.getInstance(clazz);
    }

}
