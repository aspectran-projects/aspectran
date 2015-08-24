package com.aspectran.core.context.builder.apon.params;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefine;
import com.aspectran.core.util.apon.ParameterValueType;
import com.aspectran.core.util.apon.Parameters;

public class TransletParameters extends AbstractParameters implements Parameters {

	public static final ParameterDefine name;
	public static final ParameterDefine request;
	public static final ParameterDefine contents1;
	public static final ParameterDefine contents2;
	public static final ParameterDefine responses;
	public static final ParameterDefine exception;
	public static final ParameterDefine actions;
	public static final ParameterDefine transform;
	public static final ParameterDefine dispatch;
	public static final ParameterDefine redirect;
	public static final ParameterDefine forward;

	private static final ParameterDefine[] parameterDefines;
	
	static {
		name = new ParameterDefine("name", ParameterValueType.STRING);
		request = new ParameterDefine("request", RequestParameters.class);
		contents1 = new ParameterDefine("contents", ContentsParameters.class);
		contents2 = new ParameterDefine("content", ContentParameters.class, true, true);
		responses = new ParameterDefine("response", ResponseParameters.class, true, true);
		exception = new ParameterDefine("exception", ExceptionParameters.class, true, true);
		actions = new ParameterDefine("action", ActionParameters.class, true, true);
		transform = new ParameterDefine("transform", TransformParameters.class);
		dispatch = new ParameterDefine("dispatch", DispatchParameters.class);
		redirect = new ParameterDefine("redirect", RedirectParameters.class);
		forward = new ParameterDefine("forward", ForwardParameters.class);
		
		parameterDefines = new ParameterDefine[] {
				name,
				request,
				contents1,
				contents2,
				responses,
				exception,
				actions,
				transform,
				dispatch,
				redirect,
				forward
		};
	}
	
	public TransletParameters() {
		super(parameterDefines);
	}
	
	public TransletParameters(String text) {
		super(parameterDefines, text);
	}
	
}
