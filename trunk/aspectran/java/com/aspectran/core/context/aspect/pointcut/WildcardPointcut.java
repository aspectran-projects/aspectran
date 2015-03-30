package com.aspectran.core.context.aspect.pointcut;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.aspectran.core.context.rule.PointcutPatternRule;
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
	
	public WildcardPointcut(List<PointcutPatternRule> pointcutPatternRuleList) {
		super(pointcutPatternRuleList);
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
	
	public void clear() {
		wildcardPatternCache.clear();
	}
	
}
