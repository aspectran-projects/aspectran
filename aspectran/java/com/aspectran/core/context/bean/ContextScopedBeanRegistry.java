package com.aspectran.core.context.bean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectran.core.activity.CoreActivity;
import com.aspectran.core.activity.CoreActivityException;
import com.aspectran.core.activity.VoidActivityImpl;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.aspect.AspectRuleRegistry;
import com.aspectran.core.context.aspect.pointcut.PointcutPattern;
import com.aspectran.core.context.bean.proxy.CglibDynamicBeanProxy;
import com.aspectran.core.context.bean.proxy.JdkDynamicBeanProxy;
import com.aspectran.core.context.bean.scope.RequestScope;
import com.aspectran.core.context.bean.scope.Scope;
import com.aspectran.core.var.ValueMap;
import com.aspectran.core.var.rule.AspectRule;
import com.aspectran.core.var.rule.BeanRule;
import com.aspectran.core.var.rule.BeanRuleMap;
import com.aspectran.core.var.rule.ItemRule;
import com.aspectran.core.var.rule.ItemRuleMap;
import com.aspectran.core.var.token.ItemTokenExpression;
import com.aspectran.core.var.token.ItemTokenExpressor;
import com.aspectran.core.var.type.BeanProxyModeType;
import com.aspectran.core.var.type.JoinpointScopeType;
import com.aspectran.core.var.type.ScopeType;

/**
 * SINGLETON: 모든 singleton 빈은context 생성시 초기화 된다.
 * APPLICATION: 최초 참조시 초기화 된다.
 * 초기화 시점이 다르지만, 소멸 시점은 동일하다.(context 소멸시) 
 * <p>
 * Created: 2009. 03. 09 오후 23:48:09
 * </p>
 */
public class ContextScopedBeanRegistry extends ScopedBeanRegistry implements ContextBeanRegistry {
	
	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(AbstractBeanRegistry.class);
	
	private final Map<String, List<AspectRule>> aspectRuleListCache = new HashMap<String, List<AspectRule>>();

	private final ActivityContext context;
	
	private final BeanProxyModeType beanProxyMode;
	
	private final AspectRuleRegistry aspectRuleRegistry;
	
	public ContextScopedBeanRegistry(ActivityContext context, BeanRuleMap beanRuleMap, BeanProxyModeType beanProxyMode) {
		super(beanRuleMap);
		
		this.context = context;
		this.beanProxyMode = (beanProxyMode == null ? BeanProxyModeType.CGLIB_PROXY : beanProxyMode);
		this.aspectRuleRegistry = context.getAspectRuleRegistry();
	}

	protected Scope getRequestScope() {
		Scope requestScope = context.getLocalCoreActivity().getRequestScope();
		
		if(requestScope == null) {
			requestScope = new RequestScope();
			context.getLocalCoreActivity().setRequestScope(requestScope);
		}
		
		return requestScope;
	}

	protected Scope getSessionScope() {
		return context.getLocalCoreActivity().getSessionAdapter().getScope();
	}
	
	protected Scope getApplicationScope() {
		return context.getApplicationAdapter().getScope();
	}
	
	protected Object createBean(BeanRule beanRule) {
		ItemTokenExpressor expressor = new ItemTokenExpression(context.getLocalCoreActivity());
		return createBean(beanRule, expressor);
	}

	private Object createBean(BeanRule beanRule, ItemTokenExpressor expressor) {
		try {
			ItemRuleMap constructorArgumentItemRuleMap = beanRule.getConstructorArgumentItemRuleMap();
			
			if(constructorArgumentItemRuleMap != null) {
				ValueMap valueMap = expressor.express(constructorArgumentItemRuleMap);
	
				int parameterSize = constructorArgumentItemRuleMap.size();
				Object[] args = new Object[parameterSize];
				Class<?>[] argTypes = new Class<?>[args.length];
				
				Iterator<ItemRule> iter = constructorArgumentItemRuleMap.iterator();
				int i = 0;
				
				while(iter.hasNext()) {
					ItemRule ir = iter.next();
					Object o = valueMap.get(ir.getName());
					args[i] = o;
					argTypes[i] = o.getClass();
					
					i++;
				}
				
				return createBean(beanRule, argTypes, args);
			} else {
				return createBean(beanRule, null, null);
			}
		} catch(Exception e) {
			throw new BeanCreationException(beanRule, e);
		}
	}

	private Object createBean(BeanRule beanRule, Class<?>[] argTypes, Object[] args) {
		CoreActivity activity = context.getLocalCoreActivity();
		
		/*
		 * 0. DynamicProxy 빈을 만들 것인지를 먼저 결정하라. 
		 * 1. 빈과 관련된 AspectRule을 추출하고, 캐슁하라.
		 * 2. 추출된 AspectRule을 AspectAdviceRegistry로 변환하고, DynamicProxy에 넘겨라
		 * 3. DynamicProxy에서 현재 실행 시점의 JoinScope에 따라 해당 JoinScope의 AspectAdviceRegistry에 Advice를 등록하라.
		 * 4. DynamicProxy에서 일치하는 메쏘드를 가진 AspectRule의 Advice를 실행하라.
		 */

		/*
		 * Bean과 관련된 AspectRule을 모두 추출.
		 */
		List<AspectRule> aspectRuleList = retrieveAspectRuleList(activity, beanRule);
		
		Object obj = null;
		
		if(aspectRuleList != null) {
			if(activity == null) {
				try {
					activity = new VoidActivityImpl(context);
					activity.ready(null);
				} catch(CoreActivityException e) {
					throw new BeanCreationException(beanRule, e);
				}
			}
			
			if(beanProxyMode == BeanProxyModeType.JDK_PROXY) {
				logger.debug("JdkDynamicBeanProxy " + beanRule);
				obj = JdkDynamicBeanProxy.newInstance(activity, aspectRuleList, beanRule, obj);
			} else {
				logger.debug("CglibDynamicBeanProxy " + beanRule);
				obj = CglibDynamicBeanProxy.newInstance(activity, aspectRuleList, beanRule, argTypes, args);
			}
		} else {
			if(argTypes != null && args != null)
				obj = newInstance(beanRule, argTypes, args);
			else
				obj = newInstance(beanRule, new Class[0], new Object[0]);
		}
		
		return obj;
	}
	
	/**
	 * Retrieve all Aaspect Rules associated with a bean.
	 * Bean과 관련된 모든 AspectRule을 모두 추출하라.
	 *
	 * @param activity the activity
	 * @param beanRule the bean rule
	 * @return the list
	 */
	private List<AspectRule> retrieveAspectRuleList(CoreActivity activity, BeanRule beanRule) {
		String transletName = null;
		JoinpointScopeType joinpointScope = null;
		String beanId = beanRule.getId();

		if(activity != null) {
			/*
			 * Translet, JoinpointScope의 적용여부를 결정 
			 */
			if(beanRule.getScopeType() == ScopeType.PROTOTYPE || beanRule.getScopeType() == ScopeType.REQUEST) {
				transletName = activity.getTransletName();
			}
			if(beanRule.getScopeType() == ScopeType.PROTOTYPE) {
				joinpointScope = activity.getJoinpointScope();
			}
		}
		
		String joinpointScopeString = joinpointScope == null ? null : joinpointScope.toString();
		String patternString = PointcutPattern.combinePatternString(joinpointScopeString, transletName, beanId, null);

		List<AspectRule> aspectRuleList;
		
		synchronized(aspectRuleListCache) {
			aspectRuleList = aspectRuleListCache.get(patternString);
			
			if(aspectRuleList == null) {
				aspectRuleList = aspectRuleRegistry.getBeanRelevantedAspectRuleList(joinpointScope, transletName, beanId);
				aspectRuleListCache.put(patternString, aspectRuleList);
			}
		}
		
		if(aspectRuleList.size() == 0)
			return null;
		else
			return aspectRuleList;
	}
	
}
