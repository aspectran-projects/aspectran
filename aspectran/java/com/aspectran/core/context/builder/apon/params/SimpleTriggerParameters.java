package com.aspectran.core.context.builder.apon.params;

import com.aspectran.core.var.apon.AbstractParameters;
import com.aspectran.core.var.apon.ParameterDefine;
import com.aspectran.core.var.apon.ParameterValueType;
import com.aspectran.core.var.apon.Parameters;

public class SimpleTriggerParameters extends AbstractParameters implements Parameters {

	public static final ParameterDefine withIntervalInMilliseconds;
	public static final ParameterDefine withIntervalInMinutes;
	public static final ParameterDefine withIntervalInSeconds;
	public static final ParameterDefine withIntervalInHours;
	public static final ParameterDefine withRepeatCount;
	public static final ParameterDefine repeatForever;

	private final static ParameterDefine[] parameterDefines;
	
	static {
		withIntervalInMilliseconds = new ParameterDefine("withIntervalInMilliseconds", ParameterValueType.INTEGER);
		withIntervalInMinutes = new ParameterDefine("withIntervalInMinutes", ParameterValueType.INTEGER);
		withIntervalInSeconds = new ParameterDefine("withIntervalInSeconds", ParameterValueType.INTEGER);
		withIntervalInHours = new ParameterDefine("withIntervalInHours", ParameterValueType.INTEGER);
		withRepeatCount = new ParameterDefine("withRepeatCount", ParameterValueType.INTEGER);
		repeatForever = new ParameterDefine("repeatForever", ParameterValueType.BOOLEAN);
		
		parameterDefines = new ParameterDefine[] {
				withIntervalInMilliseconds,
				withIntervalInMinutes,
				withIntervalInSeconds,
				withIntervalInHours,
				withRepeatCount,
				repeatForever
		};
	}
	
	public SimpleTriggerParameters() {
		super(SimpleTriggerParameters.class.getName(), parameterDefines);
	}
	
	public SimpleTriggerParameters(String plaintext) {
		super(SimpleTriggerParameters.class.getName(), parameterDefines, plaintext);
	}
	
}
