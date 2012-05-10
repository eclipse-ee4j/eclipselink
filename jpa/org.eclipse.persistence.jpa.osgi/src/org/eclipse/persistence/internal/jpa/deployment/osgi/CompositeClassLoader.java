/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: 
 *    ssmith = 1.1 - A ClassLoader that aggregates multiple ClassLoaders
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class CompositeClassLoader extends ClassLoader {
    private List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
    
    /**
     * Create a CompositeClassLoader with two class loaders.
     * 
     * @param loader1
     * @param loader2
     */
    public CompositeClassLoader(ClassLoader loader1, ClassLoader loader2) {
        classLoaders.add(loader1);
        classLoaders.add(loader2);
    }

    /**
     * Create a CompositeClassLoader from a list of class loaders.
     * 
     * @param loaders
     */
    public CompositeClassLoader(List<ClassLoader> loaders) {
        classLoaders.addAll(loaders);
    }

    /**
     * Get the contained class loaders.
     * 
     * @return the list of the contained class loaders
     */
    public List<ClassLoader> getClassLoaders() {
        return classLoaders;
    }

    /**
     * Sets the default assertion status for this class loader to
     * <tt>false</tt> and discards any package defaults or class assertion
     * on all contained class loaders.
     * 
     * @see  ClassLoader#clearAssertionStatus()
     */
    @Override
    public synchronized void clearAssertionStatus() {
        for (ClassLoader classLoader : getClassLoaders()) {
            classLoader.clearAssertionStatus();
        }
    }

    /**
     * Finds the resource with the given name.  Contained class 
     * loaders are queried until one returns the requested
     * resource or <tt>null</tt> if not found. 
     * 
     * @see  ClassLoader#getResource(String)
     */
    @Override
    public URL getResource(String name) {
        for (ClassLoader classLoader : getClassLoaders()) {
            URL resource = classLoader.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    /**
     * Returns an input stream for reading the specified resource.
     * Contained class loaders are queried until one returns the 
     * requested resource stream or <tt>null</tt> if not found.
     * 
     * @see  ClassLoader#getResourceAsStream(String)
     */ 
    @Override
    public InputStream getResourceAsStream(String name) {
        for (ClassLoader classLoader : getClassLoaders()) {
            InputStream stream = classLoader.getResourceAsStream(name);
            if (stream != null) {
                return stream;
            }
        }
        return null;
    }

    /**
     * Finds all the resources with the given name. Contained class 
     * loaders are queried and the results aggregated into a single
     * Enumeration.
     * 
     * @throws  IOException
     *          If I/O errors occur
     *          
     * @see  ClassLoader#getResources(String)
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Vector<URL> resourceList = new Vector<URL>();
        for (int i = 0; i < getClassLoaders().size(); i++) {
            Enumeration<URL> localResources = getClassLoaders().get(i).getResources(name);
            while(localResources.hasMoreElements()){
                URL resource = localResources.nextElement();
                if (!resourceList.contains(resource)){
                    resourceList.add(resource);
                }
            }
        }
        return resourceList.elements();
    }

   /**
     * Loads the class with the specified <a href="#name">binary name</a>.
     * Contained class loaders are queried until one returns the 
     * requested class.
     * 
     * @see  ClassLoader#loadClass(String)
     * 
     * @throws  ClassNotFoundException
     *          If the class was not found
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        for (ClassLoader classLoader : getClassLoaders()) {
            try {
                Class<?> aClass = classLoader.loadClass(name);
                return aClass;
            } catch (ClassNotFoundException e) {
            }            
        }
        throw new ClassNotFoundException(name);
    }

    /** 
     * Sets the desired assertion status for the named top-level class.
     * 
     * @see  ClassLoader#setClassAssertionStatus(String, boolean)
     */
    @Override
    public synchronized void setClassAssertionStatus(String className,
            boolean enabled) {
        for (ClassLoader classLoader : getClassLoaders()) {
            classLoader.setClassAssertionStatus(className, enabled);
        }
    }

    /**
     * Sets the default assertion status for this class loader. 
     * 
     * @see  ClassLoader#setDefaultAssertionStatus(boolean)
     */
    @Override
    public synchronized void setDefaultAssertionStatus(boolean enabled) {
        for (ClassLoader classLoader : getClassLoaders()) {
            classLoader.setDefaultAssertionStatus(enabled);
        }
    }

    /**
     * Sets the package default assertion status for the named package.
     * 
     * @see  ClassLoader#setPackageAssertionStatus(String,boolean)
     */
    @Override
    public synchronized void setPackageAssertionStatus(String packageName,
            boolean enabled) {
        for (ClassLoader classLoader : getClassLoaders()) {
            classLoader.setPackageAssertionStatus(packageName, enabled);
        }
    }
    
    
    
}
