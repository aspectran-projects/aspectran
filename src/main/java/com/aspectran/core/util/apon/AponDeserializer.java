/**
 * Copyright 2008-2016 Juho Jeong
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
package com.aspectran.core.util.apon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

/**
 * Converts an APON formatted string into a Parameters object.
 */
public class AponDeserializer extends AponFormat {

	private BufferedReader reader;
	
	private boolean addable;
	
	private int lineNumber;
	
	/**
	 * Instantiates a new AponDeserializer.
	 *
	 * @param text the APON formatted string
	 */
	public AponDeserializer(String text) {
		this(new StringReader(text));
	}

	/**
	 * Instantiates a new AponDeserializer.
	 *
	 * @param reader the character-input streams
	 */
	public AponDeserializer(Reader reader) {
		this.reader = new BufferedReader(reader);
	}
	
	/**
	 * Converts an APON formatted string into a GenericParameters object.
	 *
	 * @return the parameters object
	 * @throws IOException An I/O error occurs.
	 */
	public Parameters read() throws IOException {
		Parameters parameters = new GenericParameters();
		return read(parameters);
	}
	
	/**
	 * Converts an APON formatted string into a given Parameters object.
	 *
	 * @param <T> the generic type
	 * @param parameters the parameters object
	 * @return the parameters object
	 * @throws IOException An I/O error occurs.
	 */
	public <T extends Parameters> T read(T parameters) throws IOException {
		addable = parameters.isAddable();
		
		valuelize(parameters, NO_CONTROL_CHAR, null, null, null);
		
		return parameters;
	}
	
	/**
	 * Valuelize.
	 *
	 * @param parameters the parameters
	 * @param openBracket the open bracket character
	 * @param name the parameter name
	 * @param parameterValue the parameter value
	 * @param parameterValueType the parameter value type
	 * @return the current line number
	 * @throws IOException An I/O error occurs.
	 */
	private int valuelize(Parameters parameters, char openBracket, String name, ParameterValue parameterValue, ParameterValueType parameterValueType) throws IOException {
		Map<String, ParameterValue> parameterValueMap = parameters.getParameterValueMap();
		
		String line;
		String value;
		String trim;
		int tlen;
		int vlen;
		char cchar;
		
		while((line = reader.readLine()) != null) {
			lineNumber++;
			trim = line.trim();
			tlen = trim.length();
			
			if(tlen == 0 || trim.charAt(0) == COMMENT_LINE_START)
				continue;

			if(openBracket == SQUARE_BRACKET_OPEN) {
				value = trim;
				vlen = value.length();
				cchar = (vlen == 1) ? value.charAt(0) : NO_CONTROL_CHAR;
				
				if(SQUARE_BRACKET_CLOSE == cchar)
					return lineNumber;
			} else {
				if(tlen == 1) {
					if(openBracket == CURLY_BRACKET_OPEN && CURLY_BRACKET_CLOSE == trim.charAt(0)) {
						return lineNumber;
					}
				}

				int index = trim.indexOf(NAME_VALUE_SEPARATOR);
				if(index == -1)
					throw new InvalidParameterException(lineNumber, line, trim, "Cannot parse into name-value pair.");
				
				if(index == 0)
					throw new InvalidParameterException(lineNumber, line, trim, "Cannot recognize the parameter name.");
				
				name = trim.substring(0, index).trim();
				value = trim.substring(index + 1).trim();
				vlen = value.length();
				cchar = (vlen == 1) ? value.charAt(0) : NO_CONTROL_CHAR;
				
				parameterValue = parameterValueMap.get(name);

				if(parameterValue != null) {
					parameterValueType = parameterValue.getParameterValueType();
				} else {
					if(!addable)
						throw new InvalidParameterException(lineNumber, line, trim, "Only acceptable pre-defined parameters. Undefined parameter name: " + name);

					parameterValueType = ParameterValueType.lookupByHint(name);
					if(parameterValueType != null) {
						name = ParameterValueType.stripHintedValueType(name);
						parameterValue = parameterValueMap.get(name);
						if(parameterValue != null)
							parameterValueType = parameterValue.getParameterValueType();
					}
				}
				
				if(parameterValueType == ParameterValueType.VARIABLE)
					parameterValueType = null;
				
				if(parameterValueType != null) {
					if(parameterValue != null && !parameterValue.isArray() && SQUARE_BRACKET_OPEN == cchar)
						throw new IncompatibleParameterValueTypeException(lineNumber, line, trim, "Parameter value is not array type.");
					if(parameterValueType != ParameterValueType.PARAMETERS && CURLY_BRACKET_OPEN == cchar)
						throw new IncompatibleParameterValueTypeException(lineNumber, line, trim, parameterValue, parameterValueType);
					if(parameterValueType != ParameterValueType.TEXT && ROUND_BRACKET_OPEN == cchar)
						throw new IncompatibleParameterValueTypeException(lineNumber, line, trim, parameterValue, parameterValueType);
				}
			}
			
			if(parameterValue != null && !parameterValue.isArray()) {
				if(parameterValueType == ParameterValueType.PARAMETERS && CURLY_BRACKET_OPEN != cchar)
					throw new IncompatibleParameterValueTypeException(lineNumber, line, trim, parameterValue, parameterValueType);
				if(parameterValueType == ParameterValueType.TEXT && ROUND_BRACKET_OPEN != cchar)
					throw new IncompatibleParameterValueTypeException(lineNumber, line, trim, parameterValue, parameterValueType);
			}
			if(parameterValueType == null || (parameterValue != null && parameterValue.isArray())) {
				if(SQUARE_BRACKET_OPEN == cchar) {
					valuelize(parameters, SQUARE_BRACKET_OPEN, name, parameterValue, parameterValueType);
					continue;
				}
			}

			if(parameterValueType == null) {
				if(CURLY_BRACKET_OPEN == cchar) {
					parameterValueType = ParameterValueType.PARAMETERS;
				} else if(ROUND_BRACKET_OPEN == cchar) {
					parameterValueType = ParameterValueType.TEXT;
				}
			}
			
			if(parameterValueType == ParameterValueType.PARAMETERS) {
				if(parameterValue == null) {
					parameterValue = parameters.newParameterValue(name, parameterValueType, (openBracket == SQUARE_BRACKET_OPEN));
				}

				Parameters parameters2 = parameters.newParameters(parameterValue.getName());
				addable = parameters2.isAddable();
				
				valuelize(parameters2, CURLY_BRACKET_OPEN, null, null, null);
			} else if(parameterValueType == ParameterValueType.TEXT) {
				if(parameterValue == null) {
					parameterValue = parameters.newParameterValue(name, parameterValueType, (openBracket == SQUARE_BRACKET_OPEN));
				}

				StringBuilder sb = new StringBuilder();
				valuelizeText(sb);
				parameterValue.putValue(sb.toString());
			} else {
				if(vlen == 0) {
					value = null;
					
					if(parameterValueType == null)
						parameterValueType = ParameterValueType.STRING;
				} else {
					if(value.charAt(0) == QUOTE_CHAR) {
						if(vlen == 1 || value.charAt(vlen - 1) != QUOTE_CHAR)
							throw new InvalidParameterException(lineNumber, line, trim, "Unclosed quotation mark.");						
							
						value = value.substring(1, vlen - 1);
						
						if(parameterValueType == null)
							parameterValueType = ParameterValueType.STRING;
					} else if(parameterValueType == null) {
						if(NULL.equals(value)) {
							value = null;
						} else if(TRUE.equals(value) || FALSE.equals(value)) {
							parameterValueType = ParameterValueType.BOOLEAN;
						} else {
							try {
								Integer.parseInt(value);
								parameterValueType = ParameterValueType.INT;
							} catch(NumberFormatException e1) {
								try {
									Long.parseLong(value);
									parameterValueType = ParameterValueType.LONG;
								} catch(NumberFormatException e2) {
									try {
										Float.parseFloat(value);
										parameterValueType = ParameterValueType.FLOAT;
									} catch(NumberFormatException e3) {
										try {
											Double.parseDouble(value);
											parameterValueType = ParameterValueType.DOUBLE;
										} catch(NumberFormatException e4) {
											throw new InvalidParameterException(lineNumber, line, trim, "Unknown value type. Strings must be enclosed between double quotation marks.");
										}
									}
								}
							}
						}
					}
				}
				
				if(parameterValue == null) {
					parameterValue = parameters.newParameterValue(name, parameterValueType, (openBracket == SQUARE_BRACKET_OPEN));
				} else {
					if(parameterValue.getParameterValueType() == ParameterValueType.VARIABLE) {
						parameterValue.setParameterValueType(parameterValueType);
					} else if(parameterValue.getParameterValueType() != parameterValueType) {
						throw new IncompatibleParameterValueTypeException(lineNumber, line, trim, parameterValue, parameterValue.getParameterValueType());
					}
				}
				
				if(parameterValueType == ParameterValueType.STRING) {
					parameterValue.putValue(unescape(value, lineNumber, line, trim));
				} else if(parameterValueType == ParameterValueType.INT) {
					parameterValue.putValue(new Integer(value));
				} else if(parameterValueType == ParameterValueType.LONG) {
					parameterValue.putValue(new Long(value));
				} else if(parameterValueType == ParameterValueType.FLOAT) {
					parameterValue.putValue(new Float(value));
				} else if(parameterValueType == ParameterValueType.DOUBLE) {
					parameterValue.putValue(new Double(value));
				} else if(parameterValueType == ParameterValueType.BOOLEAN) {
					parameterValue.putValue(Boolean.valueOf(value));
				}
			}
			
			if(parameterValue.isArray() && parameterValue.isBracketed()) {
				if(openBracket != SQUARE_BRACKET_OPEN) {
					parameterValue.setBracketed(false);
				}
			}
		}
		
		if(openBracket == CURLY_BRACKET_OPEN) {
			throw new MissingClosingBracketException("curly", name, parameterValue);
		} else if(openBracket == SQUARE_BRACKET_OPEN) {
			throw new MissingClosingBracketException("square", name, parameterValue);
		}
		
		return lineNumber;
	}
	
	private int valuelizeText(StringBuilder sb) throws IOException {
		String line;
		String trim = null;
		int tlen;
		char tchar;
		
		while((line = reader.readLine()) != null) {
			lineNumber++;
			trim = line.trim();
			tlen = trim.length();
			tchar = tlen > 0 ? trim.charAt(0) : NO_CONTROL_CHAR;
			
			if(tlen == 1 && ROUND_BRACKET_CLOSE == tchar)
				return lineNumber;
				
			if(TEXT_LINE_START == tchar) {
				if(sb.length() > 0)
					sb.append(NEXT_LINE_CHAR);
				sb.append(line.substring(line.indexOf(TEXT_LINE_START) + 1));
			} else if(tlen > 0) {
				throw new InvalidParameterException(lineNumber, line, trim, "The closing round bracket was missing or Each text line is must start with a ';' character.");
			}
		}

		throw new InvalidParameterException(lineNumber, line, trim, "The end of the text line was reached with no closing round bracket found.");
	}
	
	private String unescape(String value, int lineNumber, String line, String trim) {
		String s = unescape(value);
		
		if(value == s)
			return value;
		
		if(s == null)
			throw new InvalidParameterException(lineNumber, line, trim, "Invalid escape sequence (valid ones are  \\b  \\t  \\n  \\f  \\r  \\\"  \\\\ )");
		
		return s;
	}
	
	public void close() throws IOException {
		if(reader != null)
			reader.close();
		
		reader = null;
	}

	/**
	 * Converts an APON formatted string into a Parameters object.
	 *
	 * @param text the APON formatted string
	 * @return the parameters object
	 */
	public static Parameters deserialize(String text) {
		Parameters parameters = new GenericParameters();
		return deserialize(text, parameters);
	}

	/**
	 * Converts an APON formatted string into a given Parameters object.
	 *
	 * @param <T> the generic type
	 * @param text the APON formatted string
	 * @param parameters the parameters object
	 * @return the parameters object
	 */
	public static <T extends Parameters> T deserialize(String text, T parameters) {
		try {
			AponDeserializer deserializer = new AponDeserializer(new StringReader(text));
			deserializer.read(parameters);
			deserializer.close();
	
			return parameters;
		} catch(IOException e) {
			throw new AponReadFailedException(e);
		}
	}

	/**
	 * Converts to a Parameters object from a file.
	 *
	 * @param <T> the generic type
	 * @param file the file
	 * @return the parameters object
	 * @throws IOException An I/O error occurs.
	 */
	public static <T extends Parameters> T deserialize(File file) throws IOException {
		return deserialize(file, null, null);
	}
	
	/**
	 * Converts to a Parameters object from a file.
	 *
	 * @param <T> the generic type
	 * @param file the file
	 * @param encoding the character encoding
	 * @return the parameters object
	 * @throws IOException An I/O error occurs.
	 */
	public static <T extends Parameters> T deserialize(File file, String encoding) throws IOException {
		return deserialize(file, encoding, null);
	}
	
	/**
	 * Converts into a given Parameters object from a file.
	 *
	 * @param <T> the generic type
	 * @param file the file
	 * @param parameters the parameters object
	 * @return the parameters object
	 * @throws IOException An I/O error occurs.
	 */
	public static <T extends Parameters> T deserialize(File file, T parameters) throws IOException {
		return deserialize(file, null, parameters);
	}
	
	/**
	 * Converts into a given Parameters object from a file.
	 *
	 * @param <T> the generic type
	 * @param file the file
	 * @param encoding the character encoding
	 * @param parameters the parameters object
	 * @return the parameters object
	 * @throws IOException An I/O error occurs.
	 */
	public static <T extends Parameters> T deserialize(File file, String encoding, T parameters) throws IOException {
		AponDeserializer deserializer;
		
		if(encoding == null) {
			deserializer = new AponDeserializer(new FileReader(file));
		} else {
			deserializer = new AponDeserializer(new InputStreamReader(new FileInputStream(file), encoding));
		}
		
		T p = deserializer.read(parameters);
		deserializer.close();

		return p;
	}
	
	/**
	 * Converts to a Parameters object from a character-input stream.
	 *
	 * @param reader the character-input stream
	 * @return the parameters
	 * @throws IOException An I/O error occurs.
	 */
	public static Parameters deserialize(Reader reader) throws IOException {
		AponDeserializer deserializer = new AponDeserializer(reader);
		Parameters p = deserializer.read();
		deserializer.close();
		
		return p;
	}

	/**
	 * Converts into a given Parameters object from a character-input stream.
	 *
	 * @param <T> the generic type
	 * @param reader the character-input stream
	 * @param parameters the parameters object
	 * @return the parameters object
	 * @throws IOException An I/O error occurs.
	 */
	public static <T extends Parameters> T deserialize(Reader reader, T parameters) throws IOException {
		AponDeserializer deserializer = new AponDeserializer(reader);
		deserializer.read(parameters);
		deserializer.close();
		
		return parameters;
	}
	
}
