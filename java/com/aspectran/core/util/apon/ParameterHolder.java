package com.aspectran.core.util.apon;

import java.util.List;

public class ParameterHolder {

	private static final String PARAMETER_NAME = "item";
	
	private final GenericParameters parameters;
	
	public ParameterHolder(String text, Class<? extends AbstractParameters> parametersClass, boolean array) {
		ParameterDefine[] parameterDefines = new ParameterDefine[] { new ParameterDefine(PARAMETER_NAME, parametersClass, array) };
		
		if(text != null) {
			if(array)
				text = PARAMETER_NAME + ": [\n\t{\n" + text + "\n\t}\n]";
			else
				text = PARAMETER_NAME + ": {\n" + text + "\n}";
		}
		
		this.parameters = new GenericParameters(parameterDefines, text);
	}
	
	public Parameters[] getParametersArray() {
		return parameters.getParametersArray(PARAMETER_NAME);
	}

	public List<Parameters> getParametersList() {
		return parameters.getParametersList(PARAMETER_NAME);
	}
	
}
