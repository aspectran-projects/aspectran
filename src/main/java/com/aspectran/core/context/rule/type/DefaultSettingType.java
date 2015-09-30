/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.context.rule.type;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class ActivitySettingType.
 */
public final class DefaultSettingType extends Type {
	
	/** The Constant TRANSLET_NAME_PATTERN. */
	public static final DefaultSettingType TRANSLET_NAME_PATTERN;
	
	/** The Constant TRANSLET_NAME_PREFIX. */
	public static final DefaultSettingType TRANSLET_NAME_PREFIX;
	
	/** The Constant TRANSLET_NAME_SUFFIX. */
	public static final DefaultSettingType TRANSLET_NAME_SUFFIX;
	
	/** The Constant TRANSLET_INTERFACE. */
	public static final DefaultSettingType TRANSLET_INTERFACE_CLASS;
	
	/** The Constant TRANSLET_CLASS. */
	public static final DefaultSettingType TRANSLET_IMPLEMENT_CLASS;
	
	/** The Constant NULLABLE_CONTENT_ID. */
	public static final DefaultSettingType NULLABLE_CONTENT_ID;
	
	/** The Constant NULLABLE_ACTION_ID. */
	public static final DefaultSettingType NULLABLE_ACTION_ID;
	
	/** The Constant BEAN_PROXIFIER. */
	public static final DefaultSettingType BEAN_PROXIFIER;
	
	/** The Constant POINTCUT_PATTERN_VERIFIABLE. */
	public static final DefaultSettingType POINTCUT_PATTERN_VERIFIABLE;
	
	/** The Constant types. */
	private static final Map<String, DefaultSettingType> types;
	
	static {
		TRANSLET_NAME_PATTERN = new DefaultSettingType("transletNamePattern");
		TRANSLET_NAME_PREFIX = new DefaultSettingType("transletNamePrefix");
		TRANSLET_NAME_SUFFIX = new DefaultSettingType("transletNameSuffix");
		TRANSLET_INTERFACE_CLASS = new DefaultSettingType("transletInterfaceClass");
		TRANSLET_IMPLEMENT_CLASS = new DefaultSettingType("transletImplementClass");
		NULLABLE_CONTENT_ID = new DefaultSettingType("nullableContentId");
		NULLABLE_ACTION_ID = new DefaultSettingType("nullableActionId");
		BEAN_PROXIFIER = new DefaultSettingType("beanProxifier");
		POINTCUT_PATTERN_VERIFIABLE = new DefaultSettingType("pointcutPatternVerifiable");

		types = new HashMap<String, DefaultSettingType>();
		types.put(TRANSLET_NAME_PATTERN.toString(), TRANSLET_NAME_PATTERN);
		types.put(TRANSLET_NAME_PREFIX.toString(), TRANSLET_NAME_PREFIX);
		types.put(TRANSLET_NAME_SUFFIX.toString(), TRANSLET_NAME_SUFFIX);
		types.put(TRANSLET_INTERFACE_CLASS.toString(), TRANSLET_INTERFACE_CLASS);
		types.put(TRANSLET_IMPLEMENT_CLASS.toString(), TRANSLET_IMPLEMENT_CLASS);
		types.put(NULLABLE_CONTENT_ID.toString(), NULLABLE_CONTENT_ID);
		types.put(NULLABLE_ACTION_ID.toString(), NULLABLE_ACTION_ID);
		types.put(BEAN_PROXIFIER.toString(), BEAN_PROXIFIER);
		types.put(POINTCUT_PATTERN_VERIFIABLE.toString(), POINTCUT_PATTERN_VERIFIABLE);
	}

	/**
	 * Instantiates a new activity setting type.
	 *
	 * @param type the type
	 */
	private DefaultSettingType(String type) {
		super(type);
	}

	/**
	 * Value of.
	 *
	 * @param type the type
	 * @return the activity setting type
	 */
	public static DefaultSettingType valueOf(String type) {
		return types.get(type);
	}
}
