/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.context.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.aspectran.core.context.loader.resource.InvalidResourceException;
import com.aspectran.core.context.loader.resource.LocalResourceManagerTest;
import com.aspectran.core.context.loader.resource.ResourceManagerTest;
import com.aspectran.core.util.ClassUtils;
import com.aspectran.core.util.ResourceUtils;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

public class AspectranClassLoaderTest extends ClassLoader {
	
	private final Log log = LogFactory.getLog(AspectranClassLoader.class);
	
	private final int id;
	
	private final AspectranClassLoaderTest root;

	private final String resourceLocation;
	
	private final ResourceManagerTest resourceManager;
	
	private final List<AspectranClassLoaderTest> children = new LinkedList<AspectranClassLoaderTest>();

	private final boolean firstborn;
	
	private int reloadedTimes;
	
	private Set<String> excludeClassNames;
	
	private Set<String> excludePackageNames;
	
	public AspectranClassLoaderTest() {
		this(getDefaultClassLoader());
	}
	
	public AspectranClassLoaderTest(ClassLoader parent) {
		this((String)null, parent);
	}
	
	public AspectranClassLoaderTest(String resourceLocation) {
		this(resourceLocation, getDefaultClassLoader());
	}

	public AspectranClassLoaderTest(String resourceLocation, ClassLoader parent) {
		super(parent);
		
//		if(resourceLocation == null) {
//			String className = getClass().getName();
//			String resourceName = classNameToResourceName(className);
//			URL url = parent.getResource(resourceName);
///*
//			System.out.println(className);
//			System.out.println(resourceName);
//			System.out.println(url);
//*/		
//			if(ResourceUtils.isJarURL(url)) {
//				resourceLocation = url.getFile();
//				
//				int separatorIndex = resourceLocation.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
//				if(separatorIndex != -1) {
//					resourceLocation = resourceLocation.substring(0, separatorIndex);
//				}
//			} else {
//				resourceLocation = url.getFile();
//				resourceLocation = resourceLocation.substring(0, resourceLocation.indexOf(resourceName));
//			}
//
//			if(resourceLocation.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
//				resourceLocation = resourceLocation.substring(ResourceUtils.FILE_URL_PREFIX.length());
//			}
///*
//			System.out.println(resourceLocation);
//			System.out.println(resourceLocation);
//			File f = new File(resourceLocation + "/.///");
//			System.out.println(f.exists());
//			try {
//				System.out.println(f.getCanonicalPath());
//				System.out.println(f.getCanonicalFile());
//				System.out.println(f.getAbsolutePath());
//				System.out.println(f.getAbsoluteFile());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			System.out.println(resourceLocation);
//			System.out.println(resourceLocation);
//			System.out.println(resourceLocation);
//			System.out.println(resourceLocation);
//			System.out.println(resourceLocation);
//			System.out.println(resourceLocation);
//			System.out.println(resourceLocation);
//			System.out.println(resourceLocation);
//			System.out.println(resourceLocation);
//			System.out.println(url);
//			System.out.println(url);
//			System.out.println(url);
//			System.out.println(url);
//			System.out.println(url);
//			System.out.println(url);
//			System.out.println(url);
//			System.out.println(url);
//			System.out.println(url);
//			System.out.println(url);
//			System.out.println(url);
//*/
//		}
		
		this.id = 1000;
		this.root = this;
		this.firstborn = true;
		this.resourceLocation = resourceLocation;
		this.resourceManager = new LocalResourceManagerTest(resourceLocation, this);
		
		log.debug("created a root AspectranClassLoader. " + this);
	}
	
	public AspectranClassLoaderTest(String[] resourceLocations) {
		this(resourceLocations, getDefaultClassLoader());
	}		
	
	public AspectranClassLoaderTest(String[] resourceLocations, ClassLoader parent) {
		this(parent);
		
		AspectranClassLoaderTest acl = this;
		
		for(String resourceLocation : resourceLocations) {
			acl = acl.createChild(resourceLocation);
		}
	}

	protected AspectranClassLoaderTest(String resourceLocation, AspectranClassLoaderTest parent) {
		super(parent);
		
		int brotherSize = parent.addChild(this);
		
		this.id = (Math.abs(parent.getId() / 1000) + 1) * 1000 + parent.getChildren().size();
		this.root = parent.getRoot();
		this.firstborn = (brotherSize == 1);
		this.resourceLocation = resourceLocation;
		this.resourceManager = new LocalResourceManagerTest(resourceLocation, this);
	}
	
	public synchronized void setResourceLocation(String resourceLocation) {
		//if(!isRoot())
		//	throw new UnsupportedOperationException("Can specify the resource location to the root AspectranClassLoader.");
		
		synchronized(children) {
			if(children.size() > 0) {
				children.clear();
			}
			
			createChild(resourceLocation);
		}
	}
	
	public synchronized void setResourceLocations(String[] resourceLocations) {
		//if(!isRoot())
		//	throw new UnsupportedOperationException("Can specify the resource location to the root AspectranClassLoader.");

		synchronized(children) {
			if(children.size() > 0) {
				children.clear();
			}
			
			AspectranClassLoaderTest acl = this;
			
			for(String resourceLocation : resourceLocations) {
				if(resourceLocation != null)
					acl = acl.createChild(resourceLocation);
			}
		}
	}
	
	protected AspectranClassLoaderTest createChild(String resourceLocation) {
		if(!firstborn)
			throw new UnsupportedOperationException("Can create a child only firstborn.");
		
		AspectranClassLoaderTest child = new AspectranClassLoaderTest(resourceLocation, this);
		
		log.debug("create a new child AspectranClassLoader. " + child);
		
		return child;
	}
	
	public AspectranClassLoaderTest wishBrother(String resourceLocation) {
		AspectranClassLoaderTest parent = (AspectranClassLoaderTest)getParent();

		return parent.createChild(resourceLocation);
	}
	
	/**
	 * Add a package name to exclude.
	 *
	 * @param packageName the package name to exclude
	 */
	public void excludePackage(String packageName) {
		if(excludePackageNames == null) {
			excludePackageNames = new HashSet<String>();
		}

		excludePackageNames.add(packageName + ClassUtils.PACKAGE_SEPARATOR);
	}
	
	public void excludeClass(String className) {
		if(isPackageExcluded(className))
			return;
		
		if(excludeClassNames == null) {
			excludeClassNames = new HashSet<String>();
		}
		
		excludeClassNames.add(className);
	}
	
	public void excludePackage(String[] packageNames) {
		if(packageNames == null) {
			excludePackageNames = null;
			return;
		}
		
		for(String packageName : packageNames) {
			excludePackage(packageName);
		}
	}
	
	public void excludeClass(String[] classNames) {
		if(classNames == null) {
			excludeClassNames = null;
			return;
		}
		
		for(String className : classNames) {
			excludeClass(className);
		}
	}
	
	protected boolean isExcluded(String className) {
		if(isPackageExcluded(className))
			return true;
		
		if(isClassExcluded(className))
			return true;
		
		return false;
	}
	
	private boolean isPackageExcluded(String className) {
		if(excludePackageNames != null) {
			for(String packageName : excludePackageNames) {
				if(className.startsWith(packageName))
					return true;
			}
		}
		
		return false;
	}
	
	private boolean isClassExcluded(String className) {
		return (excludeClassNames != null && excludeClassNames.contains(className));
	}
	
	public int getId() {
		return id;
	}

	public AspectranClassLoaderTest getRoot() {
		return root;
	}
	
	public boolean isRoot() {
		return this == root;
	}
	
	public List<AspectranClassLoaderTest> getChildren() {
		return children;
	}
	
	private int addChild(AspectranClassLoaderTest child) {
		children.add(child);
		return children.size();
	}
	
	public boolean isFirstborn() {
		return firstborn;
	}

	public ResourceManagerTest getResourceManager() {
		return resourceManager;
	}

	public String getResourceLocation() {
		return resourceLocation;
	}

	public void reload() {
		reload(root);
	}
	
	protected void reload(AspectranClassLoaderTest self) {
		self.increaseReloadingTimes();
		
		log.debug("reload a AspectranClassLoader. " + self);

		if(self.getResourceManager() != null)
			self.getResourceManager().reset();
		
		AspectranClassLoaderTest firstborn = null;
		
		for(AspectranClassLoaderTest child : self.getChildren()) {
			if(child.isFirstborn()) {
				firstborn = child;
			} else {
				self.kickout(child);
			}
		}
		
		if(firstborn != null) {
			reload(firstborn);
		}
	}
	
	protected void increaseReloadingTimes() {
		reloadedTimes++;
	}
	
	protected void leave() {
		AspectranClassLoaderTest parent = (AspectranClassLoaderTest)getParent();
		parent.kickout(this);
	}
	
	protected void kickout(AspectranClassLoaderTest child) {
		log.debug("kickout a child AspectranClassLoader: " + child);

		ResourceManagerTest rm = child.getResourceManager();
		if(rm != null) {
			rm.release();
		}
		children.remove(child);
	}
	
	public URL[] extractResources() {
		Enumeration<URL> res = ResourceManagerTest.getResources(getAspectranClassLoaders(root));
		List<URL> resources = new LinkedList<URL>();
		
		URL url = null;
		
		while(res.hasMoreElements()) {
			url = res.nextElement();
			
			if(!ResourceUtils.URL_PROTOCOL_JAR.equals(url.getProtocol()))
				resources.add(url);
		}
		
		return resources.toArray(new URL[resources.size()]);
	}
	
	public Enumeration<URL> getResources(String name) throws IOException {
		ClassLoader parentClassLoader = root.getParent();
		Enumeration<URL> parentResources = null;
		
		if(parentClassLoader != null)
			parentResources = parentClassLoader.getResources(name);
		
		return ResourceManagerTest.getResources(getAspectranClassLoaders(root), name, parentResources);
	}
	
	public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
	    // First check if the class is already loaded
        //System.out.println("$$$$$$$$$$$$$$$$$$$find Class: " + name);

		Class<?> c = findLoadedClass(name);

		//System.out.println("==findLoadedClass(name): " + c);

		if(c == null) {
	    	byte[] classData = null;

	    	try  {
		    	classData = loadClassData(name, root);
		    	//System.out.println("   classData: " + classData);
	    	} catch(InvalidResourceException e) {
	    		log.error("failed to load class \"" + name + "\"", e);
	    	}

	    	if(classData != null) {
	    		c = defineClass(name, classData, 0, classData.length);
	    		resolveClass(c);
	    		//System.out.println("	defineClass: " + c);
	    	}
	    }
	    
	    if(c == null && root.getParent() != null) {
	    	try {
            	//System.out.println("  getParent().loadClass");
                c = root.getParent().loadClass(name);
                //System.out.println("	getParent().loadClass: " + c);
	        } catch(ClassNotFoundException e) {
	            // If still not found, then invoke
	            // findClass to find the class.
	            c = findClass(name);
	        }
	    }
        //System.out.println("$$$$$$$$$$$$$$$$$$$complete: " + name);

	    return c;		
    }
	
	public URL getResource(String name) {
		URL url = super.getResource(name);

		if(url == null) {
			Enumeration<URL> res = ResourceManagerTest.getResources(getAspectranClassLoaders(root), name);
			
			if(res.hasMoreElements())
				url = res.nextElement();
		}

		if(url == null)
			return findResource(name);
		
		return url;
	}
	
	protected byte[] loadClassData(String className, AspectranClassLoaderTest owner) {
		if(isExcluded(className))
			return null;
		
		String resourceName = classNameToResourceName(className);
		
		URL url = null;
		Enumeration<URL> res = ResourceManagerTest.getResources(getAspectranClassLoaders(owner), resourceName);
		
		if(res.hasMoreElements())
			url = res.nextElement();

		//System.out.println(" **finded resource: " + url);
		
		if(url == null)
			return null;
		
		try {
	        URLConnection connection = url.openConnection();
	        InputStream input = connection.getInputStream();
	        ByteArrayOutputStream output = new ByteArrayOutputStream();
	
			byte[] buffer = new byte[8192];
			int len = 0;

			while((len = input.read(buffer)) >= 0) {
				output.write(buffer, 0, len);
			}   
	        
	        /*
	        int data = input.read();
	
	        while(data != -1) {
	            output.write(data);
	            data = input.read();
	        }
	        */
	
	        input.close();

	        return output.toByteArray();
		} catch(IOException e) {
			throw new InvalidResourceException("cannot read a class file: " + url, e);
		}
	}

	public Iterator<AspectranClassLoaderTest> getAllAspectranClassLoaders() {
		return getAspectranClassLoaders(root);
	}
	
	public static Iterator<AspectranClassLoaderTest> getAspectranClassLoaders(final AspectranClassLoaderTest root) {
		return new Iterator<AspectranClassLoaderTest>() {
			private AspectranClassLoaderTest next = root;
			private Iterator<AspectranClassLoaderTest> children = root.getChildren().iterator();
			private AspectranClassLoaderTest firstChild;
			private AspectranClassLoaderTest current;
			
			public boolean hasNext() {
				return (next != null);
			}
			
			public AspectranClassLoaderTest next() {
				if(next == null)
					throw new NoSuchElementException();
				
				current = next;
				
				if(children.hasNext()) {
					next = children.next();

					if(firstChild == null) {
						firstChild = next;
					}
				} else {
					if(firstChild != null) {
						children = firstChild.getChildren().iterator();
						
						if(children.hasNext()) {
							next = children.next();
							firstChild = next;
						} else {
							next = null;
						}
					} else {
						next = null;
					}
				}

				return current;
			}
		};
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch(Throwable ex) {
		}

		if(cl == null) {
			cl = AspectranClassLoader.class.getClassLoader();
		}
		
		if(cl == null) {
			cl = ClassLoader.getSystemClassLoader();
		}
		
		return cl;
	}

	public static String resourceNameToClassName(String resourceName) {
		String className = resourceName.substring(0, resourceName.length() - ClassUtils.CLASS_FILE_SUFFIX.length());
		className = className.replace(ResourceUtils.RESOURCE_NAME_SPEPARATOR_CHAR, ClassUtils.PACKAGE_SEPARATOR_CHAR);
		return className;
	}
	
	public static String classNameToResourceName(String className) {
		String resourceName = className.replace(ClassUtils.PACKAGE_SEPARATOR_CHAR, ResourceUtils.RESOURCE_NAME_SPEPARATOR_CHAR) + 
				ClassUtils.CLASS_FILE_SUFFIX;
		return resourceName;
	}
	
	public static String packageNameToResourceName(String packageName) {
		String resourceName = packageName.replace(ClassUtils.PACKAGE_SEPARATOR_CHAR, ResourceUtils.RESOURCE_NAME_SPEPARATOR_CHAR);
		
		if(resourceName.endsWith(ResourceUtils.RESOURCE_NAME_SPEPARATOR))
			resourceName = resourceName.substring(0, resourceName.length() - 1);
		
		return resourceName;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{id=").append(id);
		if(getParent() instanceof AspectranClassLoader)
			sb.append(", parent=").append(((AspectranClassLoader)getParent()).getId());
		else
			sb.append(", parent=").append(getParent().getClass().getName());
		sb.append(", root=").append(this == root);
		sb.append(", firstborn=").append(firstborn);
		sb.append(", resourceLocation=").append(resourceLocation);
		sb.append(", numberOfResource=").append(resourceManager.getResourceEntriesSize());
		sb.append(", numberOfChildren=").append(children.size());
		sb.append(", reloadedTimes=").append(reloadedTimes);
		sb.append("}");
		
		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String[] resourceLocations = new String[3];
			//resourceLocations[0] = "/WEB-INF/classes";
			//resourceLocations[1] = "/WEB-INF/lib";
			resourceLocations[0] = "file:/c:/Users/Gulendol/Projects/aspectran/ADE/workspace/aspectran.example/webapp/WEB-INF/aspectran/./classes/";
			resourceLocations[1] = "/WEB-INF/aspectran/./lib";
			//resourceLocations[2] = "file:/c:/Users/Gulendol/Projects/aspectran/ADE/workspace/aspectran.example/webapp/WEB-INF/aspectran/xml";
			resourceLocations[2] = "/WEB-INF/aspectran/./xml";
			
			//resourceLocations = ActivityContextLoadingManager.checkResourceLocations("/c:/Users/Gulendol/Projects/aspectran/ADE/workspace/aspectran.example/webapp", resourceLocations);
			
			for(String r : resourceLocations) {
				System.out.println("resourceLocation: " + r);
			}
			
			AspectranClassLoaderTest acl = new AspectranClassLoaderTest(resourceLocations);
			
			//acl.extractResources();
			
			//ResourceManager rm = acl.getResourceManager();
			//ClassLoader acl =AspectranClassLoader.getDefaultClassLoader();
			
			//Enumeration<URL> res = acl.getResources("org/junit/After.class");
			//Enumeration<URL> res = acl.getResources("com/aspectran/core/context/bean/BeanRegistry.class");
			//Enumeration<URL> res = acl.getResources("web.xml");
			
//			while(res.hasMoreElements()) {
//				System.out.println(res.nextElement().toString());
//			}

//			URL[] res = acl.extractResources();
//			for(URL url : res) {
//				System.out.println(url);
//			}
//			
//			//acl.loadClass("com.aspectran.web.activity.multipart.MultipartFileItem");
//			System.out.println("---------------------------------------------");
//			acl.reload();
//			System.out.println("---------------------------------------------");
//			acl.reload();
//			System.out.println("---------------------------------------------");
//
//			res = acl.extractResources();
//			for(URL url : res) {
//				System.out.println(url);
//			}
//			System.out.println("---------------------------------------------");

			//acl.loadClass("com.aspectran.web.activity.multipart.MultipartFileItem");
			Class<?> c = acl.loadClass("test.TestClass");
			//acl.reload();
			//acl.loadClass("test.TestClass");
			
			Object object = c.newInstance();
			System.out.println(object);
			
			//Thread.sleep(6000);
			/*
			//for(int i = 0; i < 1000; i++) {
				acl = new AspectranClassLoaderTest(resourceLocations);
				
				acl.reload();
	
				c = acl.loadClass("test.TestClass");
				object = c.newInstance();
				System.out.println(object);
			//}
			*/
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
