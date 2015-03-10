package com.aspectran.core.context.builder.apon.params;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefine;
import com.aspectran.core.util.apon.ParameterValueType;
import com.aspectran.core.util.apon.Parameters;

public class JobParameters extends AbstractParameters implements Parameters {

	public static final ParameterDefine translet;
	public static final ParameterDefine disabled;
	
	private static final ParameterDefine[] parameterDefines;
	
	static {
		translet = new ParameterDefine("translet", ParameterValueType.STRING);
		disabled = new ParameterDefine("disabled", ParameterValueType.BOOLEAN);
		
		parameterDefines = new ParameterDefine[] {
				translet,
				disabled
		};
	}
	
	public JobParameters() {
		super(parameterDefines);
	}
	
	public JobParameters(String text) {
		super(parameterDefines, text);
	}
	
}
