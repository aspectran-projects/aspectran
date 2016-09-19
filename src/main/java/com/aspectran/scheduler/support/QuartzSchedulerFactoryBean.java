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
package com.aspectran.scheduler.support;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.StdSchedulerFactory;

import com.aspectran.core.context.bean.ablility.FactoryBean;

/**
 * The type Quartz scheduler factory bean.
 *
 * @since 3.0.0
 */
public class QuartzSchedulerFactoryBean implements FactoryBean<Scheduler> {

	private String schedulerName;

	private Properties quartzProperties;

	private boolean exposeSchedulerInRepository;

	/**
	 * Set the name of the Scheduler to create via the SchedulerFactory.
	 * <p>If not specified, the bean name will be used as default scheduler name.
	 *
	 * @param schedulerName the scheduler name
	 * @see org.quartz.SchedulerFactory#getScheduler()
	 * @see org.quartz.SchedulerFactory#getScheduler(String)
	 */
	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	/**
	 * Sets quartz properties.
	 *
	 * @param quartzProperties the quartz properties
	 */
	public void setQuartzProperties(Properties quartzProperties) {
		this.quartzProperties = quartzProperties;
	}

	/**
	 * Set whether to expose the Aspectran-managed {@link Scheduler} instance in the
	 * Quartz {@link SchedulerRepository}. Default is "false", since the Aspectran-managed
	 * Scheduler is usually exclusively intended for access within the Aspectran context.
	 * <p>Switch this flag to "true" in order to expose the Scheduler globally.
	 * This is not recommended unless you have an existing Aspectran application that
	 * relies on this behavior.
	 *
	 * @param exposeSchedulerInRepository whether to expose scheduler in the quartz scheduler repository
	 */
	public void setExposeSchedulerInRepository(boolean exposeSchedulerInRepository) {
		this.exposeSchedulerInRepository = exposeSchedulerInRepository;
	}

	/**
	 * Create the Scheduler instance.
	 * <p>The default implementation invokes SchedulerFactory's {@code getScheduler}
	 * method. Can be overridden for custom Scheduler creation.
	 *
	 * @return the Scheduler instance
	 * @throws SchedulerException if thrown by Quartz methods
	 * @see org.quartz.SchedulerFactory#getScheduler
	 */
	protected Scheduler createScheduler() throws SchedulerException {
		Properties props;
		if(this.quartzProperties != null) {
			props = new Properties(this.quartzProperties);
		} else {
			props = new Properties();
		}

		if (this.schedulerName != null) {
			props.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, this.schedulerName);
		}

		String schedulerName = props.getProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME);
		if(schedulerName != null) {
			SchedulerRepository repository = SchedulerRepository.getInstance();
			Scheduler existingScheduler = repository.lookup(schedulerName);
			if (existingScheduler != null) {
				throw new IllegalStateException("Active Scheduler of name '" + schedulerName + "' already registered " +
						"in Quartz SchedulerRepository. Cannot create a new Aspectran-managed Scheduler of the same name!");
			}
		}

		SchedulerFactory schedulerFactory = new StdSchedulerFactory(props);
		Scheduler newScheduler = schedulerFactory.getScheduler();

		if (!this.exposeSchedulerInRepository) {
			// Need to remove it in this case, since Quartz shares the Scheduler instance by default!
			SchedulerRepository.getInstance().remove(newScheduler.getSchedulerName());
		}

		return newScheduler;
	}

	@Override
	public Scheduler getObject() throws SchedulerException {
		return createScheduler();
	}

}