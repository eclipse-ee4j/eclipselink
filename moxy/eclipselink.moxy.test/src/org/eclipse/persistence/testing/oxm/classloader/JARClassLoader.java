/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.classloader;

import java.io.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import java.util.zip.*;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;


/**
 * This class is an implementation of the <code>ClassLoader</code> abstract class.
 * Its purpose is to allow the ability to load a class from
 * a particular JAR file without regard to the system class path.
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 */
public class JARClassLoader extends ClassLoader {
	/** Map each package name to the appropriate JAR file. */
	private Hashtable overridePackageNames;

/**
 * Default constructor - initialize the new instance.
 */
protected JARClassLoader() {
	super();
	this.initialize();
}
/**
 * Construct a file loader for the JAR files.
 */
public JARClassLoader (String[] jarFileNames) {
	this();
	this.initialize(jarFileNames);
}
/**
 * Construct a file loader for the JAR file.
 */
public JARClassLoader (String jarFileName) {
	this();
	this.initialize(jarFileName);
}
/**
 * Build and return a ZIP file for the specified JAR file name.
 */
protected ZipFile buildJARFile(String jarFileName) {
	try {
		return new ZipFile(jarFileName);
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
}
/**
 * Return the package name for the specified class.
 */
protected String buildPackageName(String className) {
	return className.substring(0, className.lastIndexOf("."));
}
/**
 * Return the class for the specified name, leaving it "unresolved".
 */
protected Class customLoadUnresolvedClass(String className) throws ClassNotFoundException {
	String jarFileName = (String) overridePackageNames.get(this.buildPackageName(className));
	ZipFile jarFile = this.buildJARFile(jarFileName);

	String url = className.replace('.', '/').concat(".class");
	ZipEntry jarEntry = jarFile.getEntry(url);
  if(jarEntry == null){
    return this.getParent().loadClass(className);
  }
	byte[] data;
	try {
		data = this.loadData(jarFile, jarEntry);
		jarFile.close();
	} catch (IOException e) {
		Object[] args = {jarFileName, url};
		throw new ClassNotFoundException();
	}
	return this.defineClass(className, data, 0, data.length);
}
/**
 * Extract the package names from the specified JAR file
 * and store them in the hashtable.
 */
protected void extractPackageNames(String jarFileName) {
	Enumeration stream = this.buildJARFile(jarFileName).entries();
	while (stream.hasMoreElements()) {
		String entryName = ((ZipEntry) stream.nextElement()).getName();
		int endIndex = entryName.lastIndexOf("/");
		// skip over entries for files in the root directory and directories
		if ((endIndex != -1) && (endIndex != (entryName.length() - 1))) {
			String packageName = (entryName.substring(0, endIndex)).replace('/', '.');
			// skip over JAR META-INF directory
			if (! packageName.equals("META-INF")) {
				overridePackageNames.put(packageName, jarFileName);
			}
		}
	}
}
/**
 * Initialize the newly-created instance.
 */
protected void initialize() {
	this.overridePackageNames = new Hashtable();
}
/**
 * Initialize
 */
protected void initialize(String[] jarFileNames) {
	if (jarFileNames == null) {
		throw new IllegalArgumentException();
	}
	// go backwards to maintain search order (earlier packages will overlay later ones)
	for (int i = jarFileNames.length - 1; i >= 0; i--) {
		this.extractPackageNames(jarFileNames[i]);
	}
}
/**
 * Initialize
 */
protected void initialize(String jarFileName) {
	if (jarFileName == null) {
		throw new IllegalArgumentException();
	}
	this.initialize(new String[] {jarFileName});
}
/**
 * Return the class for the specified name, resolving it if necessary.
 */
protected Class loadClass(String className, boolean resolve) throws ClassNotFoundException {
	Class c = this.loadUnresolvedClass(className);
	if (resolve) {
		this.resolveClass(c);
	}
	return c;
}
/**
 * Return a byte array holding the contents of the specified file.
 */
protected byte[] loadData(ZipFile jarFile, ZipEntry jarEntry) throws IOException {

	int size = (int) jarEntry.getSize();
	byte[] buffer = new byte[size];

	DataInputStream stream = new DataInputStream(jarFile.getInputStream(jarEntry));
	stream.readFully(buffer);
	stream.close();

	return buffer;
}
/**
 * Return the class for the specified name.
 */
protected Class loadUnresolvedClass(String className) throws ClassNotFoundException {
	// check whether we already loaded the class
	Class c = this.findLoadedClass(className);
	if (c != null) {
		return c;
	}
	// check whether we should custom load the class
	if (this.shouldCustomLoad(className)) {
		return this.customLoadUnresolvedClass(className);
	} else {
        ClassLoader cl = null;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            try{
                cl = (ClassLoader) AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(this.getClass()));
            }catch (PrivilegedActionException ex){
                throw (RuntimeException)ex.getCause();
            }
        }else{
             cl = PrivilegedAccessHelper.getClassLoaderForClass(this.getClass());
        }
		if (cl == null) {
			// this should only occur under jdk1.1.x
			return this.findSystemClass(className);
		} else {
			return cl.loadClass(className);
		}
	}
}
/**
 * Return whether the specified class should be custom loaded.
 * If it is a member of one of the override packages, it should be
 * custom loaded.
 */
protected boolean shouldCustomLoad(String className) {
	return overridePackageNames.containsKey(this.buildPackageName(className));
}
}
