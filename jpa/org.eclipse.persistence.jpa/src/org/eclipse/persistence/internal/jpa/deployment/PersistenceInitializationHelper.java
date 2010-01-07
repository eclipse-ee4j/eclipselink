/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.Map;

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
        return JavaSECMPInitializer.getJavaSECMPInitializer(classLoader);
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
      
}
