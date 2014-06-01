package com.aspectran.core.context.aspect.pointcut;

import java.util.Map;
import java.util.WeakHashMap;

import com.aspectran.core.context.AspectranConstant;
import com.aspectran.core.util.wildcard.WildcardPattern;

/**
 * The Class WildcardPointcut.
 * 
 * java.io.* : java.io 패키지 내에 속한 모든 요소
 * org.myco.myapp..* : org.myco.myapp 패키지 또는 서브 패키지 내에 속한 모든 요소
 * org.myco.myapp..*@abc.action : org.myco.myapp 패키지 또는 서브 패키지 내에 속한 모든 요소의 Action ID
 */
public class WildcardPointcut extends AbstractPointcut implements Pointcut {

	private Map<String, WildcardPattern> wildcardPatternCache = new WeakHashMap<String, WildcardPattern>();
	
	public boolean matches(String transletName) {
		return matches(transletName, null, null);
	}

	public boolean matches(String transletName, String beanOrActionId) {
		return matches(transletName, beanOrActionId, null);
	}
	
	public boolean matches(String transletName, String beanOrActionId, String beanMethodName) {
		if(getExcludePatternList() != null) {
			for(PointcutPattern pointcutPattern : getExcludePatternList()) {
				if(matches(pointcutPattern, transletName, beanOrActionId, beanMethodName))
					return false;
			}
		}
		
		if(getIncludePatternList() != null) {
			for(PointcutPattern pointcutPattern : getIncludePatternList()) {
				if(matches(pointcutPattern, transletName, beanOrActionId, beanMethodName))
					return true;
			}
		}
		
		return false;
	}
	
	protected boolean matches(PointcutPattern pointcutPattern, String transletName) {
		return matches(pointcutPattern, transletName, null, null);
	}	

	protected boolean matches(PointcutPattern pointcutPattern, String transletName, String beanOrActionId) {
		return matches(pointcutPattern, transletName, beanOrActionId, null);
	}	
	
	protected boolean matches(PointcutPattern pointcutPattern, String transletName, String beanOrActionId, String beanMethodName) {
		boolean matched = true;
		
		if(transletName != null)
			matched = patternMatches(pointcutPattern.getTransletNamePattern(), transletName, AspectranConstant.TRANSLET_NAME_SEPARATOR);

		if(beanOrActionId != null && matched)
			matched = patternMatches(pointcutPattern.getBeanOrActionIdPattern(), beanOrActionId, AspectranConstant.ID_SEPARATOR);
		
		if(beanMethodName != null && matched)
			matched = patternMatches(pointcutPattern.getBeanMethodNamePattern(), beanMethodName);
		
		return matched;
	}	
	
	protected boolean patternMatches(String pattern, String str) {
		WildcardPattern wildcardPattern;
		
		synchronized(wildcardPatternCache) {
			wildcardPattern = wildcardPatternCache.get(pattern);
			
			if(wildcardPattern == null) {
				wildcardPattern = new WildcardPattern(pattern);
				wildcardPatternCache.put(pattern, wildcardPattern);
			}
		}

		//System.out.println("pattern:" + pattern + " str:" + str + " result:" + wildcardPattern.matches(str));

		return wildcardPattern.matches(str);
	}
	
	protected boolean patternMatches(String pattern, String str, String separator) {
		String patternId = pattern + separator;
		
		WildcardPattern wildcardPattern;
		
		synchronized(wildcardPatternCache) {
			wildcardPattern = wildcardPatternCache.get(patternId);
			
			if(wildcardPattern == null) {
				wildcardPattern = new WildcardPattern(pattern, separator);
				wildcardPatternCache.put(patternId, wildcardPattern);
			}
		}

		//System.out.println("pattern:" + pattern + " str:" + str + " result:" + wildcardPattern.matches(str));

		return wildcardPattern.matches(str);
	}
	
	protected boolean patternMatches(String pattern, String str, char separator) {
		String patternId = pattern + separator;
		
		WildcardPattern wildcardPattern;
		
		synchronized(wildcardPatternCache) {
			wildcardPattern = wildcardPatternCache.get(patternId);
			
			if(wildcardPattern == null) {
				wildcardPattern = new WildcardPattern(pattern, separator);
				wildcardPatternCache.put(patternId, wildcardPattern);
			}
		}

		//System.out.println("pattern:" + pattern + " str:" + str + " result:" + wildcardPattern.matches(str));

		return wildcardPattern.matches(str);
	}
	
}
