package com.aspectran.core.context.builder.apon.parser.params;

import com.aspectran.core.var.apon.AbstractParameters;
import com.aspectran.core.var.apon.ParameterDefine;
import com.aspectran.core.var.apon.Parameters;

public class ArgumentParameters extends AbstractParameters implements Parameters {

	public static final ParameterDefine items;
	
	private static final ParameterDefine[] parameterDefines;
	
	static {
		items = new ParameterDefine("item", new ItemParameters(), true);
		
		parameterDefines = new ParameterDefine[] {
				items
		};
	}
	
	public ArgumentParameters() {
		super(ArgumentParameters.class.getName(), parameterDefines);
	}
	
	public ArgumentParameters(String plaintext) {
		super(ArgumentParameters.class.getName(), parameterDefines, plaintext);
	}
	
}
