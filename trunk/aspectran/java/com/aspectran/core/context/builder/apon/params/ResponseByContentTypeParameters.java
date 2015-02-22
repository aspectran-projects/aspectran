package com.aspectran.core.context.builder.apon.params;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefine;
import com.aspectran.core.util.apon.ParameterValueType;
import com.aspectran.core.util.apon.Parameters;

public class ResponseByContentTypeParameters extends AbstractParameters implements Parameters {

	public static final ParameterDefine exceptionType;
	public static final ParameterDefine transforms;
	public static final ParameterDefine dispatchs;
	public static final ParameterDefine forwards;
	public static final ParameterDefine redirects;
	
	private static final ParameterDefine[] parameterDefines;

	static {
		exceptionType = new ParameterDefine("exceptionType", ParameterValueType.STRING);
		transforms = new ParameterDefine("transform", TransformParameters.class, true);
		dispatchs = new ParameterDefine("dispatch", DispatchParameters.class, true);
		forwards = new ParameterDefine("forward", ForwardParameters.class, true);
		redirects = new ParameterDefine("redirect", RedirectParameters.class, true);

		parameterDefines = new ParameterDefine[] {
				exceptionType,
				transforms,
				dispatchs,
				forwards,
				redirects
		};
	}
	
	public ResponseByContentTypeParameters() {
		super(ResponseByContentTypeParameters.class.getName(), parameterDefines);
	}
	
	public ResponseByContentTypeParameters(String plaintext) {
		super(ResponseByContentTypeParameters.class.getName(), parameterDefines, plaintext);
	}
	
}
