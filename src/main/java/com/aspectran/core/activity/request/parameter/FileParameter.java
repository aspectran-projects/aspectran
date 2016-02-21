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
package com.aspectran.core.activity.request.parameter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.aspectran.core.util.FileUtils;
import com.aspectran.core.util.ToStringBuilder;

/**
 * The Class FileParameter.
 * 
 * <p>Created: 2008. 04. 11 PM 4:19:40</p>
 */
public class FileParameter {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	private final File file;
	
	private boolean refused;
	
	protected File savedFile;
	
	/**
	 * Instantiates a new FileParameter.
	 */
	protected FileParameter() {
		this.file = null;
	}
	
	/**
	 * Instantiates a new FileParameter.
	 *
	 * @param file the file
	 */
	public FileParameter(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	/**
	 * Gets the actual name of the file uploaded.
	 * 
	 * @return the actual name of the file uploaded
	 */
	public String getFileName() {
		return file.getName();
	}

	/**
	 * Gets the size of the file uploaded.
	 * 
	 * @return the size of the file uploaded
	 */
	public long getFileSize() {
		return file.length();
	}

	/**
	 * Gets the the content type of the file.
	 * 
	 * @return the content type of the file
	 */
	public String getContentType() {
		return null;
	}

	/**
	 * Gets the input stream of the file.
	 * 
	 * @return An {@link java.io.OutputStream OutputStream} that can be used
	 *         for storing the contensts of the file.
	 * @throws IOException if an error occurs.
	 */
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	/**
	 * Gets the bytes.
	 * Can not use a large array of memory than the JVM Heap deal.
	 *
	 * @return the bytes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] getBytes() throws IOException {
		InputStream input = getInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int len;

		try {
			while((len = input.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}
		} finally {
			try {
				output.close();
			} catch(IOException e) {
				// ignore
			}
			try {
				input.close();
			} catch(IOException e) {
				// ignore
			}
		}

		input.close();
		output.close();
		
		return output.toByteArray();
	}
	
	/**
	 * Checks if is refused.
	 * 
	 * @return true, if is refused
	 */
	public boolean isRefused() {
		return refused;
	}

	/**
	 * Sets the refused.
	 * 
	 * @param refused the new refused
	 */
	public void setRefused(boolean refused) {
		this.refused = refused;
	}
	
	/**
	 * Save the uploaded file to the given destination file.
	 * If the file already exists in directory the save with a different name.
	 *
	 * @param destFile the destination file
	 * @return a saved file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File saveAs(File destFile) throws IOException {
		return saveAs(destFile, false);
	}

	/**
	 * Save the uploaded file to the given destination file.
	 *
	 * @param dest the destination file
	 * @param overwrite Whether overwritten if the file already exists
	 * @return a saved file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File saveAs(File dest, boolean overwrite) throws IOException {
		if(!overwrite) {
			String path = FileUtils.getPathWithoutFileName(dest.getAbsolutePath());
			String fileName = dest.getName();
			String newFileName = FileUtils.obtainUniqueFileName(path, dest.getName());
			
			if(fileName != newFileName)
				dest = new File(path, fileName);
		}
		
		InputStream input = getInputStream();
		OutputStream output = new FileOutputStream(dest);

		final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int len;

		try {
			while((len = input.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}
		} finally {
			try {
				output.close();
			} catch(IOException e) {
				// ignore
			}
			try {
				input.close();
			} catch(IOException e) {
				// ignore
			}
		}

		savedFile = dest;
		
		return dest;
	}
	
	public File getSavedFile() {
		return savedFile;
	}

	/**
	 * Delete a file.
	 */
	public void delete() {
		file.delete();
	}
	
	/**
	 * Delete a saved file.
	 */
	public void rollback() {
		if(savedFile != null)
			savedFile.delete();
	}
	
	public void release() {
		if(file != null) {
			file.setWritable(true);
		}
		if(savedFile != null) {
			savedFile.setWritable(true);
		}
	}
	
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder();
		tsb.append("file", file);
		tsb.append("savedFile", savedFile);
		tsb.append("refused", refused);
		return tsb.toString();
	}

}
