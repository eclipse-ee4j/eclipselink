/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware, ssmith = 1.0 - deployment of JPA with EE and SE
 *     12/23/2008-1.1M5 Michael O'Brien 
 *        - 253701: add unsetInitializer() for JavaSECMPInitializer undeploy()
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.NoSuchElementException;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Provides some JPA initialization behavior in a manner that can be overridden
 * by subclasses
 * @author tware
 *
 */
public class PersistenceInitializationHelper {
   
    /**
     * Return the singleton instance of a JPAInitializer
     * @param classLoader
     * @param m
     * @return
     */
    public JPAInitializer getInitializer(ClassLoader classLoader, Map m){
        return JavaSECMPInitializer.getJavaSECMPInitializer();
    }

    /**
     * Clear all references inside the initializer
     */
    public void unsetInitializer() {
        // 253701: release reference to classloader tree for non-OSGI initializers
        if(null != JavaSECMPInitializer.javaSECMPInitializer && JavaSECMPInitializer.javaSECMPInitializer.isSingletonInitialized()) {
            JavaSECMPInitializer.javaSECMPInitializer.initializationClassloader = null;
            JavaSECMPInitializer.javaSECMPInitializer.globalInstrumentation = null;
        }
    }
    
    /**
     * Answer the classloader to use to create an EntityManager.
     * If a classloader is not found in the properties map then 
     * use the current thread classloader.
     * 
     * @param properties
     * @return ClassLoader
     */
    public ClassLoader getClassLoader(String emName, Map properties) {
        ClassLoader classloader = null;
        if (properties != null) {
            classloader = (ClassLoader)properties.get(PersistenceUnitProperties.CLASSLOADER);
        }
        if (classloader == null) {
            classloader = Thread.currentThread().getContextClassLoader();
        }
        return classloader;
    }
    
    public static final String PERSISTENCE_XML_PATH = "META-INF/persistence.xml";
    public Enumeration<URL> getPersistenceResources(ClassLoader classloader) {
        Enumeration<URL> persistenceResources = EmptyPersistenceResourcesEnumeration.theEmptyEnumeration;
        try {
            persistenceResources = classloader.getResources(PERSISTENCE_XML_PATH);
        }
        catch (IOException e) {
            // e.printStackTrace();
        }
        return persistenceResources;
    }
    // for getPersistenceResources(), instead of returning <tt>null</tt> if PERSISTENCE_XML_PATH
    // doesn't resolve, return a special 'empty' enumeration
    private static final class EmptyPersistenceResourcesEnumeration implements Enumeration<URL> {
        static EmptyPersistenceResourcesEnumeration theEmptyEnumeration = 
            new EmptyPersistenceResourcesEnumeration();
        public boolean hasMoreElements() {
            return false;
        }
        public URL nextElement() {
            throw new NoSuchElementException();
        }
    }
}
