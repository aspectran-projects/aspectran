package com.aspectran.core.context.loader.config;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefine;
import com.aspectran.core.util.apon.ParameterValueType;
import com.aspectran.core.util.apon.Parameters;

public class AspectranSchedulerConfig extends AbstractParameters implements Parameters {

	public static final ParameterDefine startDelaySeconds;
	public static final ParameterDefine waitOnShutdown;
	public static final ParameterDefine startup;
	
	private final static ParameterDefine[] parameterDefines;
	
	static {
		startDelaySeconds = new ParameterDefine("startDelaySeconds", ParameterValueType.INT);
		waitOnShutdown = new ParameterDefine("waitOnShutdown", ParameterValueType.BOOLEAN);
		startup = new ParameterDefine("startup", ParameterValueType.BOOLEAN);

		parameterDefines = new ParameterDefine[] {
				startDelaySeconds,
				waitOnShutdown,
				startup
		};
	}
	
	public AspectranSchedulerConfig() {
		super(parameterDefines);
	}
	
	public AspectranSchedulerConfig(String plaintext) {
		super(parameterDefines, plaintext);
	}
	
}
