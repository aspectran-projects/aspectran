package com.aspectran.core.var.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.aspectran.core.util.StringUtils;

public abstract class AbstractOptions implements Options {

	private static final String DELIMITERS = "\n\r\f";
	
	private static final String CURLY_BRAKET_OPEN = "{";

	private static final String CURLY_BRAKET_CLOSE = "}";
	
	private static final String SQUARE_BRAKET_OPEN = "[";
	
	private static final String SQUARE_BRAKET_CLOSE = "]";
	
	protected final Map<String, Option> optionMap;
	
	private final String title;
	
	protected AbstractOptions(String title, Option[] options) {
		this(title, options, null);
	}

	protected AbstractOptions(String title, Option[] options, String plaintext) {
		this.title = title;
		
		this.optionMap = new HashMap<String, Option>();
		
		for(Option option : options) {
			optionMap.put(option.getName(), option);
		}

		if(plaintext != null)
			parse(plaintext);
	}

	public Object getValue(String name) {
		Option o = optionMap.get(name);
		
		if(o == null)
			return null;
		
		return o.getValue();
	}

	public Object getValue(Option option) {
		return getValue(option.getName());
	}
	
	public String getString(String name) {
		Object o = getValue(name);
		
		if(o == null)
			return null;
		
		return o.toString();
	}
	
	public String getString(String name, String defaultValue) {
		String val = getString(name);
		
		if(val == null)
			return defaultValue;
		
		return val;
	}

	public int getInt(String name, int defaultValue) {
		Option o = optionMap.get(name);
		
		if(o == null || o.getValue() == null || o.getValueType() != OptionValueType.INTEGER)
			return defaultValue;
		
		return ((Integer)o.getValue()).intValue();
	}
	
	public boolean getBoolean(String name, boolean defaultValue) {
		Option o = optionMap.get(name);
		
		if(o == null || o.getValue() == null || o.getValueType() != OptionValueType.BOOLEAN)
			return defaultValue;
		
		return ((Boolean)o.getValue()).booleanValue();
		
	}
	
	public String[] getStringArray(String name) {
		Option o = optionMap.get(name);
		
		if(o == null)
			return null;
		
		return (String[])o.getValue();
	}
	
	public Options getOptions(String name) {
		Option o = optionMap.get(name);
		
		if(o == null)
			return null;
		
		return (Options)o.getValue();
	}
	
	public String getString(Option option) {
		return getString(option.getName());
	}
	
	public String getString(Option option, String defaultValue) {
		return getString(option.getName(), defaultValue);
	}
	
	public int getInt(Option option, int defaultValue) {
		return getInt(option.getName(), defaultValue);
	}
	
	public boolean getBoolean(Option option, boolean defaultValue) {
		return getBoolean(option.getName(), defaultValue);
	}
	
	public String[] getStringArray(Option option) {
		return getStringArray(option.getName());
	}

	public Options getOptions(Option option) {
		return getOptions(option.getName());
	}

	protected void parse(String plaintext) {
		StringTokenizer st = new StringTokenizer(plaintext, DELIMITERS);

		parse(st, false);
	}
	
	protected void parse(StringTokenizer st, boolean subOptions) {
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			
			if(StringUtils.hasText(token)) {
				token = token.trim();

				if(subOptions) {
					if(CURLY_BRAKET_CLOSE.equals(token))
						return;
				}
				
				int index = token.indexOf(":");

				if(index == -1)
					throw new InvalidOptionException(title + ": Cannot parse into name-value pair. \"" + token + "\"");
				
				String name = token.substring(0, index).trim();
				String value = token.substring(index + 1).trim();

				Option option = optionMap.get(name);
				
				if(option == null)
					throw new InvalidOptionException(title + ": invalid option \"" + token + "\"");
				
				if(StringUtils.hasText(value)) {
					if(option.getValueType() == OptionValueType.STRING) {
						option.setValue(value);
					} else if(option.getValueType() == OptionValueType.INTEGER) {
						try {
							option.setValue(new Integer(value));
						} catch(NumberFormatException ex) {
							throw new InvalidOptionException(title + ": Cannot parse value of '" + name + "' to an integer. \"" + token + "\"");
						}
					} else if(option.getValueType() == OptionValueType.BOOLEAN) {
						option.setValue(Boolean.valueOf(value));
					} else if(option.getValueType() == OptionValueType.STRING_ARRAY) {
						if(SQUARE_BRAKET_OPEN.equals(value)) {
							List<String> stringList = new ArrayList<String>();
							boolean squareBraceClosed = false;

							while(st.hasMoreTokens()) {
								token = st.nextToken();
								value = token.trim();
								
								if(SQUARE_BRAKET_CLOSE.equals(value)) {
									squareBraceClosed = true;
									break;
								}
								
								if(StringUtils.hasText(value)) {
									stringList.add(value);
								}
							}
							
							if(!squareBraceClosed)
								throw new InvalidOptionException(title + ": Cannot parse value of '" + name + "' to an array of strings. \"" + token + "\"");
							
							option.setValue(stringList.toArray(new String[stringList.size()]));
						} else {
							String[] stringArray = new String[] { value };
							option.setValue(stringArray);
						}
					} else if(option.getValueType() == OptionValueType.OPTIONS) {
						if(CURLY_BRAKET_OPEN.equals(value)) {
							AbstractOptions options2 = (AbstractOptions)option.getOptions();
							options2.parse(st, true);
						}
					}
				}
			}
		}
	}
	
}
