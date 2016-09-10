/**
 * Copyright 2008-2016 Juho Jeong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.context.bean;

import java.util.concurrent.locks.StampedLock;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.adapter.RequestAdapter;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.core.context.bean.scope.Scope;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.type.BeanProxifierType;
import com.aspectran.core.context.rule.type.ScopeType;

/**
 * The Class ContextBeanRegistry.
 * 
 * <p>Created: 2009. 03. 09 PM 23:48:09</p>
 */
public class ContextBeanRegistry extends AbstractBeanRegistry {

	private final StampedLock singletonScopeLock = new StampedLock();

	public ContextBeanRegistry(BeanRuleRegistry beanRuleRegistry, BeanProxifierType beanProxifierType) {
		super(beanRuleRegistry, beanProxifierType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getBean(BeanRule beanRule) {
		if(beanRule.getScopeType() == ScopeType.PROTOTYPE) {
			return (T)createBean(beanRule);
		} else if(beanRule.getScopeType() == ScopeType.SINGLETON) {
			return (T)getSingletonScopeBean(beanRule);
		} else if(beanRule.getScopeType() == ScopeType.REQUEST) {
			return (T)getRequestScopeBean(beanRule);
		} else if(beanRule.getScopeType() == ScopeType.SESSION) {
			return (T)getSessionScopeBean(beanRule);
		} else if(beanRule.getScopeType() == ScopeType.APPLICATION) {
			return (T)getApplicationScopeBean(beanRule);
		}
		
		throw new BeanException();
	}

	private Object getSingletonScopeBean(BeanRule beanRule) {
		long stamp = singletonScopeLock.tryOptimisticRead();

		if(!beanRule.isRegistered() || !singletonScopeLock.validate(stamp)) {
			stamp = singletonScopeLock.readLock();

			try {
				while(!beanRule.isRegistered()) {
					long writeStamp = singletonScopeLock.tryConvertToWriteLock(stamp);

					if(writeStamp != 0L) {
						stamp = writeStamp;

						beanRule.setBean(createBean(beanRule));
						beanRule.setRegistered(true);

						break;
					} else {
						singletonScopeLock.unlockRead(stamp);
						stamp = singletonScopeLock.writeLock();
					}
				}
			} finally {
				singletonScopeLock.unlock(stamp);
			}
		}

		return beanRule.getBean();
	}

//	private Object getSingletonScopeBean(BeanRule beanRule) {
//		System.out.println("getSingletonScopeBean " + beanRule);
//		System.out.println("***isReadLocked: " + singletonScopeLock.isReadLocked());
//		System.out.println("***isWriteLocked: " + singletonScopeLock.isWriteLocked());
//		//singletonScopeLock.tryReadLock();
//		long stamp = singletonScopeLock.readLock();
//		System.out.println("**tryReadLock " + stamp);
//
//		try {
//			if(beanRule.isRegistered()) {
//				System.out.println("***return " + beanRule.getBean());
//				return beanRule.getBean();
//			}
//
//			System.out.println("***tryConvertToWriteLock " + stamp);
//			long writeStamp = singletonScopeLock.tryConvertToWriteLock(stamp);
//			System.out.println("***writeStamp " + writeStamp);
//
//			if(writeStamp != 0L) {
//				stamp = writeStamp;
//			} else {
//				System.out.println("***unlockRead " + stamp);
//				singletonScopeLock.unlockRead(stamp);
//				stamp = singletonScopeLock.writeLock();
//				System.out.println("***unlockRead writeLock " + stamp);
//			}
//
//			System.out.println("***stamp " + stamp);
//
//			Object bean = createBean(beanRule);
//			System.out.println("***createBean " + bean);
//
//			beanRule.setBean(bean);
//			beanRule.setRegistered(true);
//
//			return bean;
//		} finally {
//			System.out.println("***unlock " + stamp);
//			if(stamp != 0L) {
//				singletonScopeLock.unlock(stamp);
//			}
//			System.out.println("**unlock " + stamp + " beanRule " + beanRule);
//		}
//	}

	private Object getRequestScopeBean(BeanRule beanRule) {
		Scope scope = getRequestScope();
		if(scope == null) {
			throw new UnsupportedBeanScopeException(ScopeType.REQUEST, beanRule);
		}
		return getScopedBean(scope, beanRule);
	}
	
	private Object getSessionScopeBean(BeanRule beanRule) {
		Scope scope = getSessionScope();
		if(scope == null) {
			throw new UnsupportedBeanScopeException(ScopeType.SESSION, beanRule);
		}
		return getScopedBean(scope, beanRule);
	}

	private Object getApplicationScopeBean(BeanRule beanRule) {
		Scope scope = getApplicationScope();
		if(scope == null) {
			throw new UnsupportedBeanScopeException(ScopeType.APPLICATION, beanRule);
		}
		return getScopedBean(scope, beanRule);
	}
	
	private Object getScopedBean(Scope scope, BeanRule beanRule) {
		StampedLock lock = scope.getScopeStampedLock();
		long stamp = lock.readLock();

		try {
			Object bean = scope.getBean(beanRule);

			if(bean == null) {
				long tryStamp = lock.tryConvertToWriteLock(stamp);

				if(tryStamp != 0L) {
					stamp = tryStamp;
				} else {
					lock.unlockRead(stamp);
					stamp = lock.writeLock();
				}

				bean = createBean(beanRule);
				scope.putBean(beanRule, bean);
			}

			return bean;
		} finally {
			lock.unlock(stamp);
		}
	}

	private Scope getRequestScope() {
		Activity activity = context.getCurrentActivity();
		if(activity != null) {
			RequestAdapter requestAdapter = activity.getRequestAdapter();
			if(requestAdapter != null) {
				return requestAdapter.getRequestScope();
			}
		}
		return null;
	}

	private Scope getSessionScope() {
		Activity activity = context.getCurrentActivity();
		if(activity != null) {
			SessionAdapter sessionAdapter = activity.getSessionAdapter();
			if(sessionAdapter != null) {
				return sessionAdapter.getSessionScope();
			}
		}
		return null;
	}
	
	private Scope getApplicationScope() {
		return context.getApplicationAdapter().getApplicationScope();
	}
	
}
