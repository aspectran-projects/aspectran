package com.aspectran.core.context;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class AspectranClassLoader extends ClassLoader {

	private String[] resourceLocations;
	
	public AspectranClassLoader() {
		super(getDefaultClassLoader());
	}
	
	public AspectranClassLoader(String[] resourceLocations) {
		this(getDefaultClassLoader(), resourceLocations);
	}

	public AspectranClassLoader(ClassLoader parent, String[] resourceLocations) {
		super(parent);
		
		this.resourceLocations = resourceLocations;
	}

	public String[] getResourceLocations() {
		return resourceLocations;
	}

	public void setResourceLocations(String[] resourceLocations) {
		this.resourceLocations = resourceLocations;
	}
	
	public String[] getResources() {
		return resourceLocations;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
        if(!"reflection.MyObject".equals(name))
                return super.loadClass(name);

        try {
            String url = "file:C:/data/projects/tutorials/web/WEB-INF/" +
                            "classes/reflection/MyObject.class";
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while(data != -1){
                buffer.write(data);
                data = input.read();
            }

            input.close();

            byte[] classData = buffer.toByteArray();

            return defineClass("reflection.MyObject",
                    classData, 0, classData.length);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
	
	public Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			//String path = root + File.separatorChar + name.replace('.', File.separatorChar) + ".class";
			String path = "";

			FileInputStream file = new FileInputStream(path);
			byte[] classByte = new byte[file.available()];
			file.read(classByte);

			return defineClass(name, classByte, 0, classByte.length);
		} catch (IOException ex) {
			throw new ClassNotFoundException();
		}
	}
	
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch(Throwable ex) {
		}

		if(cl == null) {
			cl = ActivityContext.class.getClassLoader();
		}
		return cl;
	}

}
