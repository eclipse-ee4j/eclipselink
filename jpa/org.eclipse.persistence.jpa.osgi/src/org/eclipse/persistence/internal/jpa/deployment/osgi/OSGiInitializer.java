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
 *     tware, ssmith = 1.0 - bootstrap the deployment of Entities
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment.osgi;

import java.util.Collection;
import java.util.Map;

import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;

/**
 * INTERNAL:
 *
 * OSGiInitializer is used to bootstrap the deployment of Entities
 * when deployed in a non-managed OSGi environment
 *
 * It is called internally by our Provider
 *
 * @see org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider
 */
public class OSGiInitializer extends JPAInitializer {

    public OSGiInitializer(ClassLoader classLoader) {
    	this.initializationClassloader = classLoader;
    }
     
    /**
     * Check whether weaving is possible and update the properties and variable as appropriate
     * @param properties The list of properties to check for weaving and update if weaving is not needed
     */
    public void checkWeaving(Map properties){
        String weaving = "false";
        properties.put(PersistenceUnitProperties.WEAVING, weaving);
        shouldCreateInternalLoader=false;
    }
    
    /**
     * Create a temporary class loader that can be used to inspect classes and then
     * thrown away.  This allows classes to be introspected prior to loading them
     * with application's main class loader enabling weaving.
     */
    protected ClassLoader createTempLoader(Collection classNames) {
        return initializationClassloader;
    }

    protected ClassLoader createTempLoader(Collection classNames, boolean shouldOverrideLoadClassForCollectionMembers) {
        return initializationClassloader;
    }
    
    /**
     * Register a transformer.  In this case, this is a no-op since weaving does not work on default OSGi 
     * environments
     * @param transformer
     * @param persistenceUnitInfo
     */
    public void registerTransformer(final ClassTransformer transformer, PersistenceUnitInfo persistenceUnitInfo, Map properties){
    }

}
