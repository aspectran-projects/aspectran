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
package com.aspectran.scheduler.adapter;

import java.io.IOException;
import java.io.OutputStream;

import org.quartz.JobDetail;

import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import com.aspectran.scheduler.service.QuartzSchedulerService;

/**
 * The Class QuartzJobOutputStream.
 */
public class QuartzJobOutputStream extends OutputStream {

	private final Log log = LogFactory.getLog(QuartzJobOutputStream.class);

	private JobDetail jobDetail;

	private StringBuilder buffer;

	public QuartzJobOutputStream(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
		if(log.isDebugEnabled()) {
			this.buffer = new StringBuilder();
		} else {
			this.buffer = null;
		}
	}

	@Override
	public void write(int b) throws IOException {
		if(buffer != null) {
			buffer.append(b);
		}
	}

	@Override
	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		if((off | len | (b.length - (len + off)) | (off + len)) < 0) {
			throw new IndexOutOfBoundsException();
		}
		for(int i = 0 ; i < len ; i++) {
			write(b[off + i]);
		}
	}

	@Override
	public void flush() {
		if(buffer != null && buffer.length() > 0) {
			String msg = "results of job [" +
					jobDetail.getJobDataMap().get(QuartzSchedulerService.TRANSLET_NAME_DATA_KEY) + "]" +
					ActivityContext.LINE_SEPARATOR +
					buffer + ActivityContext.LINE_SEPARATOR;

			log.debug(msg);

			buffer.setLength(0);
		}
	}

	@Override
	public void close() throws IOException {
		flush();
	}

}
