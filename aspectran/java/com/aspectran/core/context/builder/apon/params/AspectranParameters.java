package com.aspectran.core.context.builder.apon.params;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.GenericParameters;
import com.aspectran.core.util.apon.ParameterDefine;
import com.aspectran.core.util.apon.Parameters;

public class AspectranParameters extends AbstractParameters implements Parameters {

	public static final ParameterDefine setting;
	public static final ParameterDefine typeAlias;
	public static final ParameterDefine aspects;
	public static final ParameterDefine beans;
	public static final ParameterDefine translets;
	public static final ParameterDefine imports;
	
	private static final ParameterDefine[] parameterDefines;
	
	static {
		setting = new ParameterDefine("setting", DefaultSettingsParameters.class);
		typeAlias = new ParameterDefine("typeAlias", GenericParameters.class);
		aspects = new ParameterDefine("aspect", AspectParameters.class, true, true);
		beans = new ParameterDefine("bean", BeanParameters.class, true);
		translets = new ParameterDefine("translet", TransletParameters.class, true, true);
		imports = new ParameterDefine("import", ImportParameters.class, true, true);
		
		parameterDefines = new ParameterDefine[] {
			setting,
			typeAlias,
			aspects,
			beans,
			translets,
			imports
		};
	}
	
	public AspectranParameters() {
		super(AspectranParameters.class.getName(), parameterDefines);
	}
	
	public AspectranParameters(String plaintext) {
		super(AspectranParameters.class.getName(), parameterDefines, plaintext);
	}
	
}
